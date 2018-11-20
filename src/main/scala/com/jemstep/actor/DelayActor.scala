package com.jemstep.actor

import akka.Done
import akka.actor._
import com.jemstep.logging.uploadfailed.UnableToUpload.uulogInformation
import com.jemstep.model.CustomModel.CsvData

class DelayActor extends Actor with BulkUploadProcessor{
  val RETRY: Int = 1

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def receive: Receive = {
    case csvData: CsvData => if(csvData.noOfRows > 0 ){
      if (!uploadHandler(csvData, 1)) {
         uulogInformation(s"Unable to upload the data for entity: "+ csvData.entity.toString +
          s" org: " + csvData.org.toString +" and offsets: " + csvData.offsets.toString + "\n" + csvData.csvObject.toString)
      }
      sender ! Done
    }
  }

  /**
    *
    * @param csvData
    * @param retryCount
    * @return
    */
  def uploadHandler(csvData: CsvData, retryCount: Int): Boolean = {
    val status = uploadProcessor(csvData)
    if(status) true
    else if(retryCount >= RETRY) false
    else uploadHandler(csvData, retryCount + 1)
  }

}


