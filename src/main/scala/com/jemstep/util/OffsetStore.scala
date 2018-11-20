package com.jemstep.util

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.kafka.clients.consumer.KafkaConsumer
import com.jemstep.util.Config._

import scala.util.{Failure, Success, Try}
import org.apache.kafka.common.TopicPartition
//import com.jemstep.logging.BulkStreamLogging.logErrorMessage
import com.jemstep.util.BulkStreamLogging._
import scala.collection.JavaConverters._
import java.util.Map
//import scala.util.control.Breaks._


object OffsetStore {

  val topic = "offsetStore333"

  val props = new Properties()

  val propMap: Map[String,String] = scala.collection.immutable.Map("bootstrap.servers" -> kafka_server,
    "client.id" -> "offsetProducer",
    "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
    "value.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
    "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
    "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
    "group.id" -> "offsetConsumer").asJava

  props.putAll(propMap)


  /**
    *
    * @return
    */
   def loadOffset: Long =  {
    val kafkaConsumer = new KafkaConsumer[String, String](props)
    val topicPartition: TopicPartition = new TopicPartition(topic, 0)
    val topics: List[TopicPartition] = topicPartition :: Nil
    kafkaConsumer.assign(topics.asJava)
    kafkaConsumer.seekToEnd(topics.asJava)
    val curpos = kafkaConsumer.position(topicPartition)
    kafkaConsumer.seek(topicPartition, if(curpos == 0)  curpos+1 else (curpos - 1))
    val records = kafkaConsumer.poll(10).asScala
    val value =  for{r <- records} yield r.value().toLong
    val value1 = value.toList
    if(value1.isDefinedAt(0)) value1(0) else -1
  }


  /**
    *
    * @param offset
    */
  def storeOffset(offset: Long): Unit ={

    Try{
      logInformation(s"max processed offset: ${offset}")
      val key: String = "offSet_value"
      val value: String = offset.toString

      val producer = new KafkaProducer[String, String](props)
      val data = new ProducerRecord[String, String](topic, key, value)

      val partialResult ={
        val x: Future[RecordMetadata] = Future {
          producer.send(data).get()
        }

        x.onComplete{
          case Success(o) =>
	    println(s"Producer offset : ${o.offset} and Producer offset value :")
          case Failure(er) =>
            println(s"Failed while producing the max offset to offsetstore: ${er.printStackTrace}")
        }
        x
      }

      val result1: Future[Seq[RecordMetadata]] =
        Future.sequence(Seq(partialResult))
      logInformation(Await.result(result1, 1.seconds).toString)
      producer.close()

    }match{
      case Success(a) => println(s"done: $a")
      case Failure(ex) => println(s"Failed while producing the max offset to offsetstore: ${ex.printStackTrace}")
    }
  }

}

