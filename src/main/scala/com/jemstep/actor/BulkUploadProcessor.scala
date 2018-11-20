package com.jemstep.actor

import com.jemstep.logging.BulkStreamLogging
import com.jemstep.logging.failed.FailureLogging
import com.jemstep.logging.debugger.DebuggerLogging
import com.jemstep.logging.unprocessed.UnprocessedLogging
import com.jemstep.model.CustomModel
import com.jemstep.model.CustomModel.CsvData
import com.sforce.async.JobStateEnum
import scala.util.{Success, Failure, Try}
import scala.util.control.Breaks


trait BulkUploadProcessor extends RestClient with RestStatus {

  import BulkStreamLogging._
  import FailureLogging._
  import UnprocessedLogging._
  import DebuggerLogging._

  /**
    *
    * @param csvData
    * @return
    */
  def uploadProcessor(csvData: CsvData): Boolean = {
    val conDetails: Try[CustomModel.ConnectionDetails] =
      createFromRefreshToken(csvData.org)

    conDetails match {
      case Success(connectionDetails) =>

        val obj = csvData.entity
        val job: Try[String] = Try(createJob(addNameSpace(obj), connectionDetails))
        logInformation(s"job :: $job")
        job match {
          case Success(jobId) =>
          if(!jobId.equals("NOJOBID")) {
            val statusCode: Int = uploadCSVData(csvData.csvObject, jobId, connectionDetails)
            dlogDebugging(s"org: `${csvData.org}`, entity: `${csvData.entity}`, noOfRecords: `${csvData.noOfRows}`, actulData: `${csvData.csvObject}` \n\n")
            val closeStatus: String = closeOrAbortJob(jobId, connectionDetails)

            val loop = new Breaks;
            loop.breakable {
              while(true) {
                val state = isJobComplated(jobId, connectionDetails).replace("\"", "")
                if (state.equalsIgnoreCase(JobStateEnum.JobComplete.toString) || state.equalsIgnoreCase(JobStateEnum.Failed.toString)) {
                  loop.break()
                }
              }
            }

            logInformation(s"Job id: `$jobId` created for org: `${csvData.org}` and " +
              s"entity: `${csvData.entity}` got response code: $statusCode and close status: $closeStatus")

            // get the success response
            if (statusCode == 201) {
              val successMsg: String = getSuccessfulResultsString(jobId, connectionDetails)
              if (successMsg.count(_ == '\n') > 1) {
                logInformation(s"Job id: $jobId got processed successfully `${successMsg.count(_ == '\n') - 1}` records.")
                successMsg.split("\n").toList.foreach(record => logDebugging(s"$record"))
              }

              val failureMsg: String = getFailedResultsString(jobId, connectionDetails)
              val failureStatus: Boolean = failureMsg.count(_ == '\n') > 1
              if (failureStatus) {
                flogErrorMessage(s"Job id: $jobId got failed to process `${failureMsg.count(_ == '\n') - 1}` records.")
                failureMsg.split("\n").toList.foreach(errorRecord =>
                  flogErrorMessage(getErrorJson(ErrorLog(csvData.entity, csvData.org, csvData.offsets.distinct, errorRecord))))
              }

              val unprocessedMsg: String = getUnprocessedResultsString(jobId, connectionDetails)
              val unprocessedStatus: Boolean = unprocessedMsg.count(_ == '\n') > 1
              if (unprocessedStatus) {
                ulogInformation(s"Job id: $jobId got unable to process `${unprocessedMsg.count(_ == '\n') - 1}` records.")
                unprocessedMsg.split("\n").toList.foreach(errorRecord =>
                  ulogInformation(getErrorJson(ErrorLog(csvData.entity, csvData.org, csvData.offsets.distinct, errorRecord))))
              }
              !(failureStatus)

            } else {
              false
            }

          }else{
            logErrorMessage(s"Error: Unable create job for org: `${csvData.org}` and entity: `${csvData.entity}`")
            false
          }

          case Failure(ex) =>
            logErrorMessage(s"Error: Unable create job for org: `${csvData.org}` and entity: `${csvData.entity}`", ex)
            false
        }
      case Failure(ex) =>
        logErrorMessage(s"Error: Unable to create connection for org: `${csvData.org}` and entity: `${csvData.entity}`", ex)
        false
    }
  }

}
