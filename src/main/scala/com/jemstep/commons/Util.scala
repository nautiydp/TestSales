package com.jemstep.commons

import java.util
import java.nio.charset.Charset

import org.apache.kafka.common.header.{Header, Headers}

object Util {

  @specialized def discard[A](evaluateForSideEffectOnly: A): Unit = {
    val _: A = evaluateForSideEffectOnly
    () //Return unit to prevent warning due to discarding value
  }

  /**
    *
    * @param key
    * @param headers
    * @return
    */
  def headerValue(key:String, headers:Headers):String = {
  	val iteratorHeader: util.Iterator[Header] = headers.headers(key).iterator()
    val schemaFullName: String = new String(iteratorHeader.next().value(), Charset.forName("UTF-8"))
    schemaFullName
  }

  /*def getcurrentDateTime(): String = {
    val timeFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSSZ")
    val milliSeconds = System.currentTimeMillis()
    timeFormat.format(milliSeconds)
  }*/
}
