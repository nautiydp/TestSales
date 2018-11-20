package com.jemstep.logging.debugger

import org.apache.log4j.Logger

class DebuggerLogging

object DebuggerLogging {

  /* Get actual class name to be printed on *//* Get actual class name to be printed on */
  private val logger: Logger = Logger.getLogger(classOf[DebuggerLogging].getName)

  /**
    *
    * @param log
    */
  def dlogInformation(log: String): Unit = {
    logger.info(log)
  }

  /**
    *
    * @param log
    */
  def dlogDebugging(log: String): Unit = {
    logger.debug(log)
  }

  /**
    *
    * @param log
    */
  def dlogErrorMessage(log: String): Unit = {
    logger.error(log)
  }

}

