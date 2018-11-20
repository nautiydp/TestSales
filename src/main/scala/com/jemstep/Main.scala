package com.jemstep

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.kafka.ConsumerSettings
import akka.stream.ActorMaterializer
import com.jemstep.actor.EntitySupervisor
import com.jemstep.util.{Config, CustomConsumer}

object Main extends App with CustomConsumer {

  implicit val system: ActorSystem = ActorSystem("StreamConsumer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val supervisor: ActorRef = system.actorOf(Props[EntitySupervisor], "streamSupervisor")

  //set the system property
  val dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss")
  val setLogSuffix = System.setProperty("current.date", dateFormat.format(new Date()))
  val logsDir = new File("logs")
  println(logsDir.mkdir().toString)
  val setLogDir = System.setProperty("log.dir", "logs")
  if (setLogSuffix == setLogDir) {

    // one supervisor
    // create consumer
    val consumerSetting: ConsumerSettings[AnyRef, AnyRef] = getConsumerSetting(Config.kafka_server)

    val kafkaTopics: String = "investor"
    // start streaming
    startStreaming(kafkaTopics, consumerSetting)
  }
}

