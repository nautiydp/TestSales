package com.jemstep.logging.uploadfailed

import org.apache.log4j.Logger

class UnableToUpload

object UnableToUpload {

  /* Get actual class name to be printed on *//* Get actual class name to be printed on */
  private val logger: Logger = Logger.getLogger(classOf[UnableToUpload].getName)

  /**
    *
    * @param log
    */
  def uulogInformation(log: String): Unit = {
    logger.info(log)
  }

  /**
    *
    * @param log
    */
  def uulogDebugging(log: String): Unit = {
    logger.debug(log)
  }

  /**
    *
    * @param log
    */
  def uulogErrorMessage(log: String): Unit = {
    logger.error(log)
  }

}

