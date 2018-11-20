package com.jemstep.logging.unprocessed

import org.apache.log4j.Logger

class UnprocessedLogging

object UnprocessedLogging {

  /* Get actual class name to be printed on *//* Get actual class name to be printed on */
  private val logger: Logger = Logger.getLogger(classOf[UnprocessedLogging].getName)

  /**
    *
    * @param log
    */
  def ulogInformation(log: String): Unit = {
    logger.info(log)
  }

  /**
    *
    * @param log
    */
  def ulogDebugging(log: String): Unit = {
    logger.debug(log)
  }

  /**
    *
    * @param log
    */
  def ulogErrorMessage(log: String): Unit = {
    logger.error(log)
  }

}

