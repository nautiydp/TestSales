package com.jemstep.oauth

import java.io._
import java.nio.charset.StandardCharsets

import scala.collection.mutable.ListBuffer
import java.net.InetSocketAddress
import java.security._
import java.security.cert.CertificateException
import java.text.SimpleDateFormat
import java.util.Date
import java.util

import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import java.util.Properties
import java.util.concurrent.Executors

import javax.net.ssl.TrustManagerFactory
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpsConfigurator
import com.sun.net.httpserver.HttpsParameters
import com.sun.net.httpserver.HttpsServer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.jemstep.actor.RestConfig
import com.jemstep.logging.BulkStreamLogging.{logErrorMessage, logInformation}

import scala.collection.mutable._
import scala.util.{Failure, Success, Try}

object OAuthSSLHandler extends RestConfig{ // The base URL for every Connect API request
  val handshakingMap = Map.empty[String, String]

  private[oauth] class CallbackHandler extends HttpHandler {

    /**
      *
      * @param t
      */
    @throws[IOException]
    override def handle(t: HttpExchange): Unit = {
      logInformation("Request received")
      Try({
        if (!(t.getRequestMethod == "GET")) {
          t.sendResponseHeaders(405, 0)
          t.getResponseBody.close()
        }
        val requestUri = t.getRequestURI

        logInformation("Get Uri " + requestUri.toString)
        logInformation("Get Fragment " + requestUri.getFragment)
        //val out = ("Sample Response: URI-" + requestUri.toASCIIString + " FRagment: " + requestUri.getFragment).getBytes("UTF-8")
        val queryParameters = URLEncodedUtils.parse(requestUri, StandardCharsets.UTF_8)
        logInformation(queryParameters.toString)
        val responseMap = Map.empty[String, String]
        queryParameters.forEach(x => {
          x.getName match {
            case "code" => {
              val resMap = responseMap += (x.getName -> x.getValue)
              logInformation(resMap.toString())
            }
            case "state" => {
              val resMap = responseMap += (x.getName -> x.getValue)
              logInformation(resMap.toString())
            }
          }
        })
        logInformation(responseMap.toString())
        val url1: Try[List[String]] = getRefreshTokenFromCode(responseMap)
        url1 match {
          case Success(url) =>
            val listBuf = new ListBuffer[String]
            responseMap("state").toString.split(",").foreach(x => listBuf += x)
            val list = listBuf.toList
            logInformation(list(3).toString)
            t.getResponseHeaders.set("Location", list(3).concat("?" + url(1)))
            t.sendResponseHeaders(302, 0)
            t.getResponseBody.close()
          case Failure(e) =>
            logErrorMessage("Unable to get the instance URL and REFRESH_TOKEN ", e)
            //e.getStackTrace.foreach(x => logErrorMessage(x.toString))
      }
      }) match {
        case Success(done) => logInformation(done.toString)
        case Failure(e) =>
          logErrorMessage("Unable to get the instance URL and REFRESH_TOKEN1 ", e)
          //e.getStackTrace.foreach(x => logErrorMessage(x.toString))
      }
    }

    /**
      *
      * @param responseMap
      */
    private def getRefreshTokenFromCode(responseMap: Map[String, String]): Try[List[String]] = {
      Try{
      val listBuf = new ListBuffer[String]
      val responseStatusList = new ListBuffer[String]
      responseMap("state").toString.split(",").foreach(x => listBuf += x)
      val list = listBuf.toList
      logInformation(list.toString())
      val productionOrSandbox = list(1)
      val org = list(2)
      val tok_url = if (productionOrSandbox.equalsIgnoreCase("production")) PRODUCTION_TOKEN_URL else SANDBOX_TOKEN_URL
      val httpclient = HttpClients.createDefault
        val loginParams = new util.ArrayList[NameValuePair]
        val code = responseMap("code").toString
        logInformation(loginParams.add(new BasicNameValuePair("code", code)).toString)
        logInformation(loginParams.add(new BasicNameValuePair("grant_type", GRANT_TYPE_CODE)).toString)
        logInformation(loginParams.add(new BasicNameValuePair("client_id", CLIENT_ID)).toString)
        logInformation(loginParams.add(new BasicNameValuePair("client_secret", CLIENT_SECRET)).toString)
        logInformation(loginParams.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI)).toString)
        logInformation(s" tok_url : $tok_url")
        val post: HttpPost = new HttpPost(tok_url)
        post.setEntity(new UrlEncodedFormEntity(loginParams))
        val loginResponse: HttpResponse = httpclient.execute(post)

        val mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
        val loginResult = mapper.readValue(loginResponse.getEntity.getContent, classOf[JsonNode])
        val ack_url1: Try[String] = parseJson(loginResult, org)
        logInformation("loginResult -> " + loginResult.toString)
        ack_url1 match{
          case Failure(ex) =>
            logErrorMessage("Unable to get the acknoledgement URL ", ex)
            //ex.getStackTrace.foreach(x => logErrorMessage(x.toString))
            val tok_url1 = tok_url.substring(0, tok_url.indexOfSlice(".com/") + 5)
            val token_url = responseStatusList += tok_url1
            val failure = responseStatusList += "failure"
            logInformation("tokent_url: "+token_url.toString()+": failure: "+failure.toString())
            responseStatusList.toList
          case Success(ack_url) =>
            val ack = responseStatusList += ack_url
            val success = responseStatusList += "successful"
            logInformation("ack_url: "+ack.toString()+": success: "+success.toString())
            responseStatusList.toList
          }
      }
    }

    /**
      * This methos parses the json string and stores the "request_token" value to properties file
      *
      * @return
      * @throws Exception
      */
    def parseJson(JsonObject: JsonNode, org: String): Try[String] = {
      Try{val refresh_token = JsonObject.get("refresh_token").asText()
        val p = new Properties()
        p.load(new FileInputStream("config.properties"))
        val fOut = new FileOutputStream("config.properties")
        logInformation("Setting REFRESH_TOKEN:"+p.setProperty(org.toUpperCase + "_REFRESH_TOKEN", refresh_token).toString)
        p.store(fOut, "UTF-8");
        fOut.close();
        JsonObject.get("instance_url").asText()
      }

    }
  }

  /**
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {
    Try({
      val dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss")
      val setLogSuffix = System.setProperty("current.date", dateFormat.format(new Date()))
      val logsDir = new File("logs")
      println(logsDir.mkdir().toString)
      val setLogDir = System.setProperty("log.dir", "logs")
      val keystoreFilename = "keystore.jks"
      val storepass = "jemstep".toCharArray
      val keypass = "jemstep".toCharArray
      val alias = "jemstep"
      val fIn = new FileInputStream(keystoreFilename)
      val keystore = KeyStore.getInstance("JKS")
      keystore.load(fIn, storepass)
      val cert = keystore.getCertificate(alias)
      logInformation(cert.toString)
      val kmf = KeyManagerFactory.getInstance("SunX509")
      kmf.init(keystore, keypass)
      val tmf = TrustManagerFactory.getInstance("SunX509")
      tmf.init(keystore)
      val portNumber = PORT_NUMBER
      val server = HttpsServer.create(new InetSocketAddress(portNumber), 0)
      // create ssl context
      if(setLogSuffix == setLogDir){
        println("logger initialization")
      }
      val sslContext = SSLContext.getInstance("TLSv1.2")
      sslContext.init(kmf.getKeyManagers, tmf.getTrustManagers, new SecureRandom())
      server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
        override def configure(params: HttpsParameters): Unit = {
          Try({// initialise the SSL context
            val c = SSLContext.getDefault
            val engine = c.createSSLEngine
            params.setNeedClientAuth(false)
            params.setCipherSuites(engine.getEnabledCipherSuites)
            params.setProtocols(engine.getEnabledProtocols)
            // get the default parameters
            val defaultSSLParameters = c.getDefaultSSLParameters
            params.setSSLParameters(defaultSSLParameters)
          }) match {
            case Success(done) => logInformation(done.toString)
            case Failure(ex) =>
              logErrorMessage("Unable to create HTTPS server", ex)
              //ex.getStackTrace.foreach(x => logErrorMessage(x.toString))
              //logInformation("Failed to create HTTPS server")
          }
        }
      })
      val httpPath = server.createContext("/services/oauth2/success", new OAuthSSLHandler.CallbackHandler)
      logInformation("Creating Context:"+httpPath.toString)
      server.setExecutor(Executors.newCachedThreadPool())
      server.start()
      logInformation("Listening on port " + portNumber.toString)
    }) match{
      case Success(done) => logInformation(done.toString)
      case Failure(e) => e match {
        case ioe: IOException =>
          logErrorMessage ("Server startup failed. Exiting.", ioe)
          System.exit (1)
        case kse: KeyStoreException =>
          logErrorMessage ("KeyStoreException", kse)
        case nsae: NoSuchAlgorithmException =>
          logErrorMessage ("NoSuchAlgorithmException", nsae)
        case ce: CertificateException =>
          logErrorMessage ("CertificateException", ce)
        case uke: UnrecoverableKeyException =>
          logErrorMessage ("UnrecoverableKeyException", uke)
        case kme: KeyManagementException =>
          logErrorMessage ("KeyManagementException", kme)
        case ex: Exception =>
          logErrorMessage ("Exception while starting the OAuthSSLHandler", ex)
        case _ =>
          logErrorMessage("Unknow Exception while starting the OAuthSSLHandler")
      }
    }
  }

}
