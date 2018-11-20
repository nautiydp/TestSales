package com.jemstep.commons

import java.io.{File, FileInputStream, FilenameFilter}

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.file.DataFileStream
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}

object DataIO {

  /**
    *
    * @param file
    * @return
    */
  def streamFromFile(file: File) : Stream[GenericRecord] = {
    val inputStream = new FileInputStream(file)
    val datumReader = new GenericDatumReader[GenericRecord]()
    val dataFileStream = new DataFileStream[GenericRecord](inputStream, datumReader)
    import scala.collection.JavaConverters._
    dataFileStream.iterator.asScala.toStream
  }

}

object SchemaIO {

  val availableSchemas = getFromFiles()

  /**
    *
    * @param schemaFullname
    * @return
    */
  def findSchema(schemaFullname: String): Option[Schema] = availableSchemas.find((schema: Schema) => schema.getFullName == schemaFullname)

  def getFromFiles(): List[Schema] = getFromFiles("./schema/")

  /**
    *
    * @param str
    * @return
    */
  def getFromFiles(str: String): List[Schema]  = getFromFiles(new File(str))

  /**
    *
    * @param dir
    * @return
    */
  def getFromFiles(dir: File): List[Schema] = {
    val filter = new FilenameFilter {
      override def accept(f: File, name: String): Boolean = name.toLowerCase().endsWith(".avsc")
    }

    val files = dir.listFiles(filter).toList
    files.map(getFromFile(_))
  }

  /**
    *
    * @param filepath
    * @return
    */
  def getFromFile(filepath: String) : Schema = getFromFile(new File(filepath))

  /**
    *
    * @param file
    * @return
    */
  def getFromFile(file: File) : Schema = new Parser().parse(file)

}
