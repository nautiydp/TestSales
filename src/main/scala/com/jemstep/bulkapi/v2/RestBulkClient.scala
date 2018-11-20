package com.jemstep.bulkapi.v2

import java.io.{FileInputStream, FileNotFoundException, IOException}
import java.nio.file.{Files, Paths}
import java.util.{ArrayList, List, Properties}

import org.apache.http.{HttpResponse, NameValuePair}
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpGet, HttpPatch, HttpPost, HttpPut}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper, SerializationFeature}
import com.jemstep.util.BulkStreamLogging.logInformation
import com.sforce.async.JobStateEnum

import scala.collection.mutable.ListBuffer


object RestBulkClient {

  private val GRANT_TYPE: String = "refresh_token"

  private val BEARER: String = "Bearer"

  private val CONTENT_TYPE: String = "application/json"

  private val ACCEPT: String = "application/json"

  private val REST_URI: String = "/services/data/v42.0/jobs/ingest/"


  val configProps: Properties = new Properties()


  try {
    configProps.load(new FileInputStream("config.properties"))

  } catch {
    case e: FileNotFoundException => logInformation(e.getStackTrace().toString)

    case e: IOException => logInformation(e.getStackTrace().toString)

  }
  private val TOKEN_URL: String = configProps.getProperty("TOKEN_URL") //.asInstanceOf[String]

  private val CLIENT_ID: String = configProps.getProperty("CLIENT_ID") //.asInstanceOf[String]

  private val CLIENT_SECRET: String = configProps.getProperty("CLIENT_SECRET") //.asInstanceOf[String]

  //private val REFRESH_TOKEN: String = configProps.getProperty("REFRESH_TOKEN") //.asInstanceOf[String]

  private val auth: ListBuffer[String] = scala.collection.mutable.ListBuffer.empty[String]

  /**
    *
    * @param org
    */
  def createFromRefreshToken(org : String): Unit = {
    try {

      val httpclient = HttpClients.createDefault()
      val loginParams: List[NameValuePair] = new ArrayList[NameValuePair]()
      val bool1 = loginParams.add(new BasicNameValuePair("grant_type", GRANT_TYPE))
      val bool2 = loginParams.add(new BasicNameValuePair("client_id", CLIENT_ID))
      val bool3 = loginParams.add(new BasicNameValuePair("client_secret", CLIENT_SECRET))
      val bool4 = loginParams.add(new BasicNameValuePair("refresh_token", configProps.getProperty(org+"_REFRESH_TOKEN")))
      val post: HttpPost = new HttpPost(TOKEN_URL)
      post.setEntity(new UrlEncodedFormEntity(loginParams))
      val loginResponse: HttpResponse = httpclient.execute(post)
      // parse
      val mapper: ObjectMapper =
        new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
      val loginResult: JsonNode = mapper.readValue(
        loginResponse.getEntity.getContent,
        classOf[JsonNode])

      val val1 = auth += loginResult.get("access_token").asText()
      val val2 = auth += loginResult.get("instance_url").asText()
      logInformation(" aadasd " + val1.toString() + "" + val2.toString())

      logInformation(bool1.toString() + "" + bool2.toString() + "" + bool3.toString() + "" + bool4.toString())

    } catch {
      case e: IOException => logInformation(e.getStackTrace().toString)

    }

  }

  /**
    *
    * @param obj
    * @return
    */
  def createJob(obj: String): String = {
    val httpclient = HttpClients.createDefault()
    val uri1 = if(auth.isDefinedAt(1)) auth(1).toString else ""
    val uri: String = uri1 + REST_URI
    logInformation("createjob URI -> " + uri)
    val BEARER1 = if(auth.isDefinedAt(0)) auth(0).toString else ""
    val authorization: String = BEARER + " " + BEARER1.toString
    // Set JSON body
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val request: CreateJobRequest =
      new CreateJobRequest(obj, "CSV", "insert", "LF")
    val requestJson: String = mapper.writeValueAsString(request)
    logInformation(requestJson)
    val jsonBody: StringEntity = new StringEntity(requestJson)
    // post the request
    val post: HttpPost = new HttpPost(uri)
    post.setHeader("Authorization", authorization)
    post.setHeader("Content-Type", CONTENT_TYPE)
    post.setEntity(jsonBody)
    logInformation(
      "create job input jsonBody -> " +
        mapper.readValue(jsonBody.getContent, classOf[JsonNode]).asText())
    val response = httpclient.execute(post)
    val responseJson =
      mapper.readValue(response.getEntity.getContent, classOf[JsonNode])
    logInformation("create job responseJson -> " + responseJson.asText())
    responseJson.get("id").asText()

  }

  /**
    *
    * @param `object`
    * @return
    */
  def createJobForJsonUpload(`object`: String): String = {


    val httpclient = HttpClients.createDefault()
    val uri1 = if(auth.isDefinedAt(1)) auth(1).toString else ""
    val uri: String = uri1 + REST_URI
    logInformation("createjob URI -> " + uri)
    val BEARER1 = if(auth.isDefinedAt(0)) auth(0).toString else ""
    val authorization: String = BEARER + " " + BEARER1.toString
    // Set JSON body
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val request: CreateJobRequest =
      new CreateJobRequest(`object`, "JSON", "insert", "")
    val requestJson: String = mapper.writeValueAsString(request)
    //println(requestJson)
    val jsonBody: StringEntity = new StringEntity(requestJson)
    // post the request
    val post: HttpPost = new HttpPost(uri)
    post.setHeader("Authorization", authorization)
    post.setHeader("Content-Type", CONTENT_TYPE)
    post.setEntity(jsonBody)
    logInformation(
      "create job input jsonBody -> " +
        mapper.readValue(jsonBody.getContent, classOf[JsonNode]).asText())
    val response = httpclient.execute(post)
    val responseJson =
      mapper.readValue(response.getEntity.getContent, classOf[JsonNode])
    logInformation("create job responseJson -> " + responseJson.asText())
    val jobId = responseJson.get("id").asText()
    logInformation(s"created job id: $jobId")
    jobId
  }

  /**
    *
    * @param file
    * @param jobId
    * @return
    */
  def uploadData(file: String, jobId: String): Int = {
    val uri1 = if(auth.isDefinedAt(1)) auth(1).toString else ""
    val uploadUri: String = uri1 + REST_URI + jobId + "/batches"
    logInformation("upload data uri -> " + uploadUri)
    val BEARER1 = if(auth.isDefinedAt(0)) auth(0).toString else ""
    val authorization: String = BEARER + " " + BEARER1.toString
    //var data: StringEntity = null
    //var response: HttpResponse = null

    val httpclient = HttpClients.createDefault()
    val fileContents: String = new String(
      Files.readAllBytes(Paths.get(file)))
    val data = new StringEntity(fileContents)
    data.setContentType("text/csv")
    val put: HttpPut = new HttpPut(uploadUri)
    put.setHeader("Authorization", authorization)
    put.setHeader("Content-Type", "text/csv")
    put.setHeader("Accept", "application/json")
    put.setEntity(data)
    val response = httpclient.execute(put)

    logInformation(
      "upload data response status code -> " + response.getStatusLine.getStatusCode.toString())
    response.getStatusLine.getStatusCode
  }

  /**
    *
    * @param csvObject
    * @param jobId
    * @return
    */
  def uploadCsvData(csvObject: String, jobId: String): Int = {
    val uri1 = if(auth.isDefinedAt(1)) auth(1).toString else ""
    val uploadUri: String = uri1 + REST_URI + jobId + "/batches"
    logInformation("upload data uri -> " + uploadUri)
    val BEARER1 = if(auth.isDefinedAt(0)) auth(0).toString else ""
    val authorization: String = BEARER + " " + BEARER1.toString

    val httpclient = HttpClients.createDefault()
    val data = new StringEntity(csvObject)
    data.setContentType("text/csv")
    val put: HttpPut = new HttpPut(uploadUri)
    put.setHeader("Authorization", authorization)
    put.setHeader("Content-Type", "text/csv")
    put.setHeader("Accept", "application/json")
    put.setEntity(data)
    val response = httpclient.execute(put)

    logInformation("upload data response status code -> " + response.getStatusLine.getStatusCode.toString())
    response.getStatusLine.getStatusCode
  }


  def uploadJsonData(file: String, jobId: String): Int = {
    val uri1 = if(auth.isDefinedAt(1)) auth(1).toString else ""
    val uploadUri: String = uri1 + REST_URI + jobId + "/batches"
    logInformation("upload data uri -> " + uploadUri)
    val BEARER1 = if(auth.isDefinedAt(0)) auth(0).toString else ""
    val authorization: String = BEARER + " " + BEARER1.toString
    val httpclient = HttpClients.createDefault()
    val fileContents: String = new String(
      Files.readAllBytes(Paths.get(file)))
    val data = new StringEntity(fileContents)
    data.setContentType("application/json")
    val put: HttpPut = new HttpPut(uploadUri)
    put.setHeader("Authorization", authorization)
    put.setHeader("Content-Type", "application/json")
    put.setHeader("Accept", "application/json")
    put.setEntity(data)
    val response = httpclient.execute(put)

    logInformation(
      "upload data response status code -> " + response.getStatusLine.getStatusCode.toString())
    response.getStatusLine.getStatusCode
  }


  def getSucessfulResults(jobId: String): Unit = {
    val uri1 = if(auth.isDefinedAt(1)) auth(1).toString else ""
    val url: String = uri1 + REST_URI + jobId + "/successfulResults/"
    val BEARER1 = if(auth.isDefinedAt(0)) auth(0).toString else ""
    val authorization: String = BEARER + " " + BEARER1.toString
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    if (response != null && response.getEntity != null) {
      val responseEntity = EntityUtils.toString(response.getEntity)
      logInformation("get successful responseJson -> " + responseEntity)
    }
  }

  def getSucessfulResultsString(jobId: String): String = {
    val uri1 = if(auth.isDefinedAt(1)) auth(1).toString else ""
    val url: String = uri1 + REST_URI + jobId + "/successfulResults/"
    val BEARER1 = if(auth.isDefinedAt(0)) auth(0).toString else ""
    val authorization: String = BEARER + " " + BEARER1.toString
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    if (response != null && response.getEntity != null) {
      val responseEntity = EntityUtils.toString(response.getEntity)
      responseEntity
    } else {
      "No response"
    }
  }


  def getFailedResults(jobId: String): Unit = {
    val uri1 = if(auth.isDefinedAt(1)) auth(1).toString else ""
    val url: String = uri1 + REST_URI + jobId + "/failedResults/"
    val BEARER1 = if(auth.isDefinedAt(0)) auth(0).toString else ""
    val authorization: String = BEARER + " " + BEARER1.toString
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    val responseEntity = EntityUtils.toString(response.getEntity)

    logInformation("get failed responseJson -> " + responseEntity)
  }

  def getFailedResultsString(jobId: String): String = {
    val uri1 = if(auth.isDefinedAt(1)) auth(1).toString else ""
    val url: String = uri1 + REST_URI + jobId + "/failedResults/"
    val BEARER1 = if(auth.isDefinedAt(0)) auth(0).toString else ""
    val authorization: String = BEARER + " " + BEARER1.toString
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    if (response != null && response.getEntity != null) {
      val responseEntity = EntityUtils.toString(response.getEntity)
      //println("get failed responseJson -> " + responseEntity)
      responseEntity
    } else{
      "no response"
    }
  }

  def getUnprocessedResults(jobId: String): Unit = {
    val uri1 = if(auth.isDefinedAt(1)) auth(1).toString else ""
    val url: String = uri1 + REST_URI + jobId + "/unprocessedrecords/"
    val BEARER1 = if(auth.isDefinedAt(0)) auth(0).toString else ""
    val authorization: String = BEARER + " " + BEARER1.toString
    // Set Headers
    val apiParams: List[NameValuePair] = new ArrayList[NameValuePair]()
    val bool1 = apiParams.add(new BasicNameValuePair("Authorization", authorization))
    val bool2 = apiParams.add(new BasicNameValuePair("Content-Type", CONTENT_TYPE))
    val bool3 = apiParams.add(new BasicNameValuePair("Accept", ACCEPT))
    logInformation(bool1.toString() + "" + bool2.toString() + "" + bool3.toString())
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)

    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    val responseEntity = EntityUtils.toString(response.getEntity)
    logInformation("get unprocessed responseJson -> " + responseEntity)
  }

  def getUnprocessedResultsString(jobId: String): String = {
    val uri1 = if(auth.isDefinedAt(1)) auth(1).toString else ""
    val url: String = uri1 + REST_URI + jobId + "/unprocessedrecords/"
    val BEARER1 = if(auth.isDefinedAt(0)) auth(0).toString else ""
    val authorization: String = BEARER + " " + BEARER1.toString
    // Set Headers
    val apiParams: List[NameValuePair] = new ArrayList[NameValuePair]()
    val bool1 = apiParams.add(new BasicNameValuePair("Authorization", authorization))
    val bool2 = apiParams.add(new BasicNameValuePair("Content-Type", CONTENT_TYPE))
    val bool3 = apiParams.add(new BasicNameValuePair("Accept", ACCEPT))
    logInformation(bool1.toString() + "" + bool2.toString() + "" + bool3.toString())
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    if (response != null && response.getEntity != null) {
      val responseEntity = EntityUtils.toString(response.getEntity)
      responseEntity
    }else{
      "no response"
    }
  }

  def closeOrAbortJob(jobId: String): Unit = {
    // Set JSON body
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val uri1 = if(auth.isDefinedAt(1)) auth(1).toString else ""
    val url: String = uri1 + REST_URI + jobId
    val BEARER1 = if(auth.isDefinedAt(0)) auth(0).toString else ""
    val authorization: String = BEARER + " " + BEARER1.toString
    val patch: HttpPatch = new HttpPatch(url)
    patch.setHeader("Authorization", authorization)
    patch.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val jsonBody = new StringEntity(
      "{\"state\":" + "\"" + JobStateEnum.UploadComplete.toString +
        "\"}")
    patch.setEntity(jsonBody)
    val response = httpclient.execute(patch)
    val responseJson =
      mapper.readValue(response.getEntity.getContent, classOf[JsonNode])

    logInformation("close job responseJson -> " + responseJson.asText())
  }


}
