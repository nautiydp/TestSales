package com.jemstep.actor

import java.io.{ByteArrayInputStream, InputStream}
import java.util

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper, SerializationFeature}
import com.jemstep.logging.BulkStreamLogging
import com.jemstep.bulkapi.v2.CreateJobRequest
import com.jemstep.model.CustomModel.ConnectionDetails
import com.sforce.async.JobStateEnum
import org.apache.http.HttpResponse
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpGet, HttpPatch, HttpPost, HttpPut}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair

import scala.collection.JavaConverters._
import scala.util.Try

trait RestClient extends RestConfig {

  /**
    * create the refresh token for given org
    *
    * @param org given user input
    * @return
    */
  def createFromRefreshToken(org: String): Try[ConnectionDetails] = {
    // create http client
   Try{
    val httpclient = HttpClients.createDefault()
    // login params
    val refresh_token = getToken(org)
    val loginParams: util.List[BasicNameValuePair] =
      List(
        new BasicNameValuePair("grant_type", GRANT_TYPE),
        new BasicNameValuePair("client_id", CLIENT_ID),
        new BasicNameValuePair("client_secret", CLIENT_SECRET),
        new BasicNameValuePair("refresh_token", refresh_token)
      ).asJava

    // http post body
    val post: HttpPost = new HttpPost(PRODUCTION_TOKEN_URL)
    post.setEntity(new UrlEncodedFormEntity(loginParams))
    val loginResponse: Option[HttpResponse] = Try(httpclient.execute(post)).toOption

    // parse
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
     val inputStream: InputStream = new ByteArrayInputStream("".getBytes())
    val loginResult: JsonNode = mapper.readValue(
      loginResponse.fold(inputStream)(x => x.getEntity.getContent),
      classOf[JsonNode])

     BulkStreamLogging.logInformation(s"mapper : $mapper")

     val accessToken = Option(loginResult.get("access_token")).fold("")(x => x.asText)
     val instanceUrl = Option(loginResult.get("instance_url")).fold("")(x => x.asText)

    BulkStreamLogging.logInformation(s"Request login for `$org` organization got http login response code ${loginResponse.fold(0)(x => x.getStatusLine.getStatusCode)}. Instance URL: $instanceUrl")
    ConnectionDetails(accessToken, instanceUrl)
   }
  }
  /**
    * create the job
    *
    * @param obj               object
    * @param connectionDetails connection information
    * @return
    */
  def createJob(obj: String, connectionDetails: ConnectionDetails): String = {
    val httpclient = HttpClients.createDefault()
    val uri: String = connectionDetails.instanceUrl + REST_URI
    val authorization: String = BEARER + " " + connectionDetails.accessToken
    // Set JSON body
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val request: CreateJobRequest =
      new CreateJobRequest(obj, "CSV", "insert", "LF")
    val requestJson: String = mapper.writeValueAsString(request)
    val jsonBody: StringEntity = new StringEntity(requestJson)
    // post the request
    val post: HttpPost = new HttpPost(uri)
    post.setHeader("Authorization", authorization)
    post.setHeader("Content-Type", CONTENT_TYPE)
    post.setEntity(jsonBody)

    val response = httpclient.execute(post)
    val responseJson =
      mapper.readValue(response.getEntity.getContent, classOf[JsonNode])
    Option(responseJson.get("id")).fold("NOJOBID")(x => x.asText)
  }

  /**
    * upload the data
    *
    * @param csvObject         csv data
    * @param jobId             job id
    * @param connectionDetails connection information
    * @return
    */
  def uploadCSVData(csvObject: String, jobId: String, connectionDetails: ConnectionDetails): Int = {
    val uploadUri: String = connectionDetails.instanceUrl + REST_URI + jobId + "/batches"
    val authorization: String = BEARER + " " + connectionDetails.accessToken

    val httpclient = HttpClients.createDefault()
    val data = new StringEntity(csvObject)
    data.setContentType("text/csv")
    val put: HttpPut = new HttpPut(uploadUri)
    put.setHeader("Authorization", authorization)
    put.setHeader("Content-Type", "text/csv")
    put.setHeader("Accept", "application/json")
    put.setEntity(data)
    val response = httpclient.execute(put)
    response.getStatusLine.getStatusCode
  }


  /**
    * close the job
    *
    * @param jobId             job id
    * @param connectionDetails connection details
    * @return
    */
  def closeOrAbortJob(jobId: String, connectionDetails: ConnectionDetails): String = {
    // Set JSON body
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val url: String = connectionDetails.instanceUrl + REST_URI + jobId
    val authorization: String = BEARER + " " + connectionDetails.accessToken
    val patch: HttpPatch = new HttpPatch(url)
    patch.setHeader("Authorization", authorization)
    patch.setHeader("Content-Type", CONTENT_TYPE)

    val httpclient = HttpClients.createDefault()
    val jsonBody = new StringEntity("{\"state\":" + "\"" + JobStateEnum.UploadComplete.toString + "\"}")
    patch.setEntity(jsonBody)
    val response = httpclient.execute(patch)
    val responseJson =
      mapper.readValue(response.getEntity.getContent, classOf[JsonNode])
    responseJson.asText()
  }

/**
    * is complated the job
    *
    * @param jobId             job id
    * @param connectionDetails connection details
    * @return
    */
  def isJobComplated(jobId: String, connectionDetails: ConnectionDetails): String = {
    // Set JSON body
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val url: String = connectionDetails.instanceUrl + REST_URI + jobId
    val authorization: String = BEARER + " " + connectionDetails.accessToken
    val patch: HttpGet = new HttpGet(url)
    patch.setHeader("Authorization", authorization)
    patch.setHeader("Content-Type", CONTENT_TYPE)

    val httpclient = HttpClients.createDefault()
    //val jsonBody = new StringEntity("{\"state\":" + "\"" + JobStateEnum.JobComplete.toString + "\"}")
    //patch.setEntity(jsonBody)
    val response = httpclient.execute(patch)
    val responseJson =
      mapper.readValue(response.getEntity.getContent, classOf[JsonNode])
     responseJson.get("state").toString
  }

  def addNameSpace(entityName: String): String ={
    val namespace = configProps.getProperty("NAMESPACE")
    namespace.toString+entityName.toString
  }
}
