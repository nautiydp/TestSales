package com.jemstep.actor

import java.util.concurrent.TimeUnit

import akka.Done
import akka.actor._
import com.jemstep.logging.BulkStreamLogging.{logErrorMessage, logInformation}
import com.jemstep.model.CustomModel.{CacheRefresh, EntityHolder}
import com.jemstep.model.ExtractorModel.IncomingData
import com.jemstep.model._

import scala.util.{Failure, Success, Try}

import scala.concurrent.duration.FiniteDuration

class EntitySupervisor extends Actor {

  import context.dispatcher

  val bulkApiActor: ActorRef = context.actorOf(Props[BulkApiActor], "bulkApiActor")

  val cacheActor: ActorRef =
    context.actorOf(Props(new NewCacheActor(bulkApiActor)), "CacheActor")

  val cacheScheduler: Cancellable =
    context.system.scheduler.schedule(FiniteDuration(5, TimeUnit.MINUTES),
      FiniteDuration(5, TimeUnit.MINUTES), cacheActor, CacheRefresh)

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def receive: Receive = active()

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def active(): Receive = {
    case incomingData: IncomingData => if(isValidSchema(incomingData)){newSupervision(incomingData, sender())}
    else logErrorMessage("Invalid Schema :"+incomingData.recordSchemaFullName.toString)
  }

  /**
    *
    * @param incomingData
    * @return
    */
  private def isValidSchema(incomingData: IncomingData): Boolean =
    ExtractorModel.validSchema(incomingData.recordSchemaFullName).isDefined

  /**
    *
    * @param incomingData
    * @param sender
    */
  private def newSupervision(incomingData: IncomingData, sender: ActorRef): Unit = {
    Try {
      val businessData: BusinessModel = ExtractorModel.parser(incomingData)
      val listOfEntity: List[BusinessEntityModel.EntityModel] =
        businessData.extractEntityModels(incomingData.userId, incomingData.organizationId, incomingData.operation)

      listOfEntity.map(entityModel => {
        EntityHolder(incomingData.offSet, entityModel)
      })
        .foreach(ch => {
          cacheActor ! ch
        })
    } match {
      case Success(done) => logInformation(s"successul submitted to NewCacheActor... ${done}")
      case Failure(ex) => logErrorMessage("failed while submitting to cacheActor: ", ex)
    }

    sender ! Done
  }

}
