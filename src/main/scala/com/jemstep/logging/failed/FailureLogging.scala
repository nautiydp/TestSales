package com.jemstep.logging.failed

import org.apache.log4j.Logger

class FailureLogging

object FailureLogging {

  /* Get actual class name to be printed on *//* Get actual class name to be printed on */
  private val logger: Logger = Logger.getLogger(classOf[FailureLogging].getName)


  /**
    *
    * @param log
    */
  def flogInformation(log: String): Unit = {
    logger.info(log)
  }

  /**
    *
    * @param log
    */
  def flogDebugging(log: String): Unit = {
    logger.debug(log)
  }

  /**
    *
    * @param log
    */
  def flogErrorMessage(log: String): Unit = {
    logger.error(log)
  }

}

