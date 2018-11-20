package com.jemstep.actor

import akka.actor.{Actor, ActorRef, Props, Scheduler}
import com.jemstep.logging.BulkStreamLogging

import scala.concurrent.{Future, Promise}
import scala.util.control.Breaks
import com.jemstep.model.CustomModel.{CsvDataList, OffsetCsvStore}
import com.jemstep.util.OffsetStore

import scala.annotation.tailrec
import scala.concurrent.duration.FiniteDuration

class BulkApiActor extends Actor with BulkUploadProcessor{

  import context.dispatcher
  val delayActor: ActorRef = context.actorOf(Props[DelayActor])
  import BulkStreamLogging._
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def receive: Receive = {
    case csvDataList: OffsetCsvStore =>
      logInformation("Bulk Api Upload actor ")

      if(!uploadHandler(csvDataList.csvDataList, csvDataList.offset, 1, 0))
        {
          logInformation(s"csvDataList ${csvDataList.toString}")
        }

  }

  /**
    *
    * @param csvDataList
    */
  /*private def uploadHandler(csvDataList: OffsetCsvStore): Unit = {
    uploadInOrder(csvDataList.csvDataList, csvDataList.offset, 0)
  }*/

  @tailrec
  private def uploadHandler(csvDataList: CsvDataList, offset: Long, retryCount: Int, step: Int): Boolean = {
    val response = uploadInOrder(csvDataList, offset, step)
    if (response == 11) true
    else if (retryCount >= 1) false
    else uploadHandler(csvDataList, offset, retryCount + 1, response)
  }




  //  private def uploadInOrder(csvDataList1: OffsetCsvStore): Unit = {
//    val csvDataList = csvDataList1.csvDataList
//    if (uploadProcess(Future{delayActor ! csvDataList.rtqPeU}))
//      if (uploadProcess(Future{delayActor ! csvDataList.rtqPeH}))
//        if (uploadProcess(Future{delayActor ! csvDataList.backtestMatricsPe}))
//          if (uploadProcess(Future{delayActor ! csvDataList.goalPeG}))
//            if (uploadProcess(Future{delayActor ! csvDataList.goalPeP}))
//              if (uploadProcess(Future{delayActor ! csvDataList.goalPeT}))
//                if (uploadProcess(Future{delayActor ! csvDataList.goalDetailsPeG}))
//                  if (uploadProcess(Future{delayActor ! csvDataList.accPeP}))
//                    if (uploadProcess(Future{delayActor ! csvDataList.hdPeP}))
//                      if (uploadProcess(Future{delayActor ! csvDataList.contactPeH}))
//                        if (uploadProcess(Future{delayActor ! csvDataList.profileDetailsPeMQ}))
//                        {
//                          logInformation("list of batch jobs complated")
//                          OffsetStore.storeOffset(csvDataList1.offset)
//                        }
//  }

  /**
    *
    * @param csvDataList
    */

private def uploadInOrder(csvDataList: CsvDataList, offset: Long, i: Int): Int =
  if (i >= 1 || uploadProcessor(csvDataList.rtqPeU))
    if (i >= 2 || uploadProcessor(csvDataList.rtqPeH) )
      if(i >=  3 ||  uploadProcessor(csvDataList.backtestMatricsPe))
        if(i >= 4 ||  uploadProcessor(csvDataList.goalPeG))
          if (i >= 5 || uploadProcessor(csvDataList.goalPeP))
            if(i >= 6 || uploadProcessor(csvDataList.goalPeT))
              if (i >= 7 || uploadProcessor(csvDataList.goalDetailsPeG))
                if (i >= 8 || uploadProcessor(csvDataList.accPeP))
                  if (i >= 9 || uploadProcessor(csvDataList.hdPeP))
                    if (i >= 10 || uploadProcessor(csvDataList.contactPeH))
                      if (i >= 11 || uploadProcessor(csvDataList.profileDetailsPeMQ)) {
                        OffsetStore.storeOffset(offset)
                        11
                      }else 10
                  else 9
                else 8
              else 7
            else 6
          else 5
        else 4
      else 3
    else 2
  else 1
  else 0



  val delay = configProps.getProperty("DELAY_TIME_MILLISECONDS").toLong
  import scala.concurrent.duration.Duration
  import java.util.concurrent.TimeUnit

  /**
    *
    * @param future
    * @return
    */
  def uploadProcess(future: Future[Unit]): Boolean = {
    val finiteDuration = Duration.create(delay, TimeUnit.MILLISECONDS)
    val flag = delayProcessor(finiteDuration, context.system.scheduler)(future)
    val loop = new Breaks
    loop.breakable {
      while (!flag.isCompleted) {

      }
    }
    flag.isCompleted
  }

  /**
    *
    * @param duration
    * @param using
    * @param value
    * @tparam T
    * @return
    */
  def delayProcessor[T](duration: FiniteDuration, using: Scheduler)(value: => Future[T]): Future[T] = {
    val promise = Promise[T]()
    val cancellable = using.scheduleOnce(duration){
      val complete = promise.completeWith(value)
      logInformation(complete.isCompleted.toString)
    }
    logInformation(cancellable.isCancelled.toString)
    promise.future
  }

  def persistProcessedMaxOffset(csvDataList: AnyRef)=
    (Map[String, AnyRef]() /: csvDataList.getClass.getDeclaredFields) { (a, f) =>
      f.setAccessible(true)
      a + (f.getName -> f.get(csvDataList))
    }
}
