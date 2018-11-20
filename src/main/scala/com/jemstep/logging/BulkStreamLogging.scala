package com.jemstep.logging

import org.apache.log4j.Logger

class BulkStreamLogging

object BulkStreamLogging {

  /* Get actual class name to be printed on *//* Get actual class name to be printed on */
  private val logger: Logger = Logger.getLogger(classOf[BulkStreamLogging].getName)

  import net.liftweb.json._

  /**
    *
    * @param entity
    * @param org
    * @param offset
    * @param response
    */
  case class ErrorLog(entity: String, org: String, offset: List[Long], response: String)

  /**
    *
    * @param log
    */
  def logInformation(log: String): Unit = {
    logger.info(log)
  }

  /**
    *
    * @param log
    */
  def logDebugging(log: String): Unit = {
    logger.debug(log)
  }

  /**
    *
    * @param log
    */
  def logErrorMessage(log: String): Unit = {
    logger.error(log)
  }


  /**
    *
    * @param log
    * @param exception
    */
  def logErrorMessage(log: String, exception: Throwable): Unit = {
    logger.error(log, exception)
  }

  /**
    *
    * @param error
    * @return
    */
  def getErrorJson(error: ErrorLog): String = {
    import net.liftweb.json.Extraction._
    import net.liftweb.json.JsonAST._
    implicit val formats: DefaultFormats = net.liftweb.json.DefaultFormats
    prettyRender(render(decompose(error)).value)
      .replaceAll("\n"," ")
      .replaceAll("\t", " ")
      .replaceAll(" +", " ")
  }

}
