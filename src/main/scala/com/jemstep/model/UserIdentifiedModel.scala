package com.jemstep.model

import com.jemstep.model.BusinessEntityModel.{EntityModel, EntityType, RtqPe, RtqPeU}

object UserIdentifiedModel {

  /**
    *
    * @param clientAddr
    */
  case class AccessMeta(clientAddr: String, proto: String, host: String)

  /**
    *
    * @param plainValue
    */
  case class FirstName(plainValue: String)

  /**
    *
    * @param plainValue
    */
  case class LastName(plainValue: String)

  /**
    *
    * @param plainValue
    */
  case class EmailAddress(plainValue: String)

  /**
    *
    * @param accessMeta
    * @param emailAddress
    * @param lastName
    * @param firstName
    * @param externalId
    * @param lastLogin
    */
  case class UserIdentified(accessMeta: AccessMeta,
                            emailAddress: EmailAddress,
                            lastName: LastName,
                            firstName: FirstName,
                            externalId: Option[String],
                            lastLogin: String) extends BusinessModel {

    import EntityType._

    /**
      *
      * @param userId
      * @param org
      * @param operation
      * @return
      */
    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = {

      val rtqPe: RtqPe =
        RtqPeU(Jemstep_Id__c = userId,
          Address__c = accessMeta.clientAddr, Email__c = emailAddress.plainValue, Last_Name__c = lastName.plainValue,
          First_Name__c = firstName.plainValue, Digital_Instance_URL__c = getDigitalInstanceURL(accessMeta),
          Unique_Client_ID__c = externalId.fold("")(e => e), Name__c = firstName.plainValue.toString + " " + lastName.plainValue.toString,
          Investor_Last_Login_into_Jemstep__c = lastLogin)

      EntityModel(RTQ_PE_U, org, rtqPe :: Nil) :: Nil
    }

    /**
      *
      * @param accessMeta
      * @return
      */
    def getDigitalInstanceURL(accessMeta: AccessMeta): String = {
      accessMeta.proto.toString + "://" + accessMeta.host.toString
    }
  }

}
