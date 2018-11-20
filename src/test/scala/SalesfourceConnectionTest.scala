
import com.jemstep.actor.RestClient
import com.jemstep.model.CustomModel
import org.specs2.mutable.Specification

import scala.util.{Failure, Success, Try}

class SalesfourceConnectionTest extends Specification with  RestClient{
  println("************************************Start of connection test***************************************************************************************")
  val conDetails: Try[CustomModel.ConnectionDetails] = createFromRefreshToken("nextgenbank")

  conDetails match {
    case Success(connectionDetails) => {
      if (!conDetails.get.accessToken.isEmpty && !conDetails.get.instanceUrl.isEmpty) {
        println(s"Request login for nextgenabnk  organization  for Instance URL : ${connectionDetails.instanceUrl} \n connection happened successfully")

        val job: Try[String] = Try(createJob("jemstep1__Investor_PE__e", connectionDetails))

        job match {
          case Success(jobid) =>
            if (!jobid.equals("NOJOBID")) {
              println("************************The job created Successfully********************")
              println(s"The job id is :: $jobid")
            }
            else println("Found NOJOBID  Check the  configuration details")
          case Failure(ex) => println(s"Error is : $ex \n***************Not able to create job. Check the configuration details******************")
        }
      }

      else {
       println(s"***************Connection is not happening. Check the configuration details******************")
      }
  }
    case Failure(ex) => println(s"Error is : $ex \n***************Connection is not happening. Check the configuration details******************")
  }
}
