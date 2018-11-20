package com.jemstep.util


import java.io.{FileInputStream, File}
import java.util.Properties

import com.jemstep.util.BulkStreamLogging.{logErrorMessage, logInformation}

import scala.util.{Failure, Success, Try}

trait FullQuestionConfig {

  val map = scala.collection.mutable.Map.empty[String, Properties]

  Try {
    val listOfFiles = getListOfFiles("properties")
    listOfFiles.foreach(file => {
      val fileName = file.getName
      logInformation("fileName: "+fileName)
      val properties: Properties = new Properties()
      val array = fileName.split("\\.")
      val key = array(0).toUpperCase + "" +{if(array(2).toUpperCase.equalsIgnoreCase("PROPERTIES")) "" else array(2).toUpperCase}
      logInformation(key)
      properties.load(new FileInputStream(file))
      map += (key -> properties)
    })
  } match {
    case Success(props) => props
    case Failure(e) => e.getStackTrace().foreach(x => logErrorMessage(x.toString))
  }


  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

}
