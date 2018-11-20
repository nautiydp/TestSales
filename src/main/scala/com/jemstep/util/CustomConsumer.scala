package com.jemstep.util

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import com.jemstep.commons.Util.headerValue
import com.jemstep.model.ExtractorModel.IncomingData
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import com.jemstep.commons.Config._
import com.jemstep.schema_registry.SchemaRegistry.{client, props}

trait CustomConsumer{

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit val timeout: Timeout = Timeout(5.seconds)
  val supervisor: ActorRef

  val minMegaBytes = 5
  val minBytes = (minMegaBytes * 1024 * 1000).toString
  val maxWaitTime: FiniteDuration = 5.seconds

  import com.jemstep.logging.BulkStreamLogging._
  /**
    * get the consumer setting with custom deserializer
    *
    * @return consumer with avro array byes key and generic record type value
    */
  def getConsumerSetting(kafka_server: String): ConsumerSettings[AnyRef, AnyRef] =
    ConsumerSettings(materializer.system,
      new io.confluent.kafka.serializers.KafkaAvroDeserializer(client, props),
      new io.confluent.kafka.serializers.KafkaAvroDeserializer(client, props))
      .withBootstrapServers(kafka_server)
      .withGroupId("SalesforceSyncService")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      .withProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, minBytes)
      .withProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, maxWaitTime.toMillis.toString)
      .withProperty("schema.registry.url",schema_registry_url)

  /**
    * start the streaming for given topics and consumer settings
    *
    * @param topics           give topics string
    * @param consumerSettings given consumer setting
    * @return
    */
  /*def startStreaming(topics: String,
                     consumerSettings: ConsumerSettings[AnyRef, AnyRef]): Unit = {
    val source: Source[Done, Consumer.Control] =
      Consumer.committableSource(consumerSettings, Subscriptions.assignmentWithOffset(new TopicPartition(topics, 0), 0))
        .mapAsync(1) { msg =>
          logInformation("Streaming Processing offset:"+msg.record.offset.toString)
          logInformation(s"Streaming Processing JSON: ${msg.record}")
          sendToSupervisor(msg.record)
            .flatMap { _ => msg.committableOffset.commitScaladsl() }
        }
    logInformation("Streaming Processing Done")
    source.runWith(Sink.ignore)
      .foreach( _ => logInformation("Streaming Processing Done for received Messages."))
  }*/


  def startStreaming(topics: String,
                     consumerSettings: ConsumerSettings[AnyRef, AnyRef]): Unit = {
    val beginningOffset = OffsetStore.loadOffset+1
    logInformation(s"beginningOffset: $beginningOffset")
    import org.apache.kafka.common.TopicPartition
    val source: Source[Done, Consumer.Control] =
      Consumer.committableSource(consumerSettings, Subscriptions.assignmentWithOffset(new TopicPartition(topics, 0), beginningOffset))
        .mapAsync(1) { msg =>
          logInformation(s"Streaming Processing offset: ${msg.record.offset.toString} -> ${msg.record.partition}")
          logInformation(s"Streaming Processing JSON: ${msg}")
          sendToSupervisor(msg.record)
        }
    logInformation("Streaming Processing Done")
    source.runWith(Sink.ignore).foreach( _ => logInformation("Streaming Processing Done for received Messages."))
  }





  /**
    *
    * @param record
    * @return
    */
  def sendToSupervisor(record: ConsumerRecord[AnyRef, AnyRef]): Future[Done] = {
    val userId: String = headerValue("userId", record.headers)
    val organizationId: String = headerValue("organizationId", record.headers)
    val operation: String = headerValue("operation", record.headers)
    val recordSchemaFullName: String = headerValue("schema", record.headers)
    // create incoming data
    val incomingData: IncomingData =
      IncomingData(recordSchemaFullName,
        record.value().toString,
        userId,
        organizationId,
        operation,
        record.offset())
    // IncomingMessage send to  Supervisor
    supervisor ! incomingData
    Future.successful(Done)
  }

}
