package com.jemstep.model

import com.jemstep.actor.RestConfig
import com.jemstep.model.BusinessEntityModel._

object CustomModel extends RestConfig{

  import EntityType._

  trait ActorMessage

  case object CacheRefresh extends ActorMessage

  /**
    *
    * @param org
    */
  case class CacheClean(org: String) extends ActorMessage

  /**
    *
    * @param org
    * @param offSet
    * @param listOfObj
    */
  case class CacheMessage(org: String, offSet: Long,
                          listOfObj: List[EntityObject]) extends ActorMessage

  /**
    *
    * @param offSet
    * @param holder
    */
  case class EntityHolder(offSet: Long, holder: EntityModel) extends ActorMessage

  /**
    *
    * @param entity
    * @param org
    * @param csvObject
    * @param offsets
    * @param noOfRows
    */
  case class CsvData(entity: String, org: String,
                     csvObject: String, offsets: List[Long], noOfRows: Int)


  /**
    *
    * @param rtqPeU
    * @param rtqPeH
    * @param backtestMatricsPe
    * @param goalPeG
    * @param goalPeP
    * @param goalPeT
    * @param goalDetailsPeG
    * @param accPeP
    * @param hdPeP
    * @param contactPeH
    * @param profileDetailsPeMQ
    */
  case class CsvDataList(rtqPeU: CsvData, rtqPeH: CsvData,
                         backtestMatricsPe: CsvData, goalPeG: CsvData,
                         goalPeP: CsvData, goalPeT: CsvData,
                         goalDetailsPeG: CsvData, accPeP: CsvData, hdPeP: CsvData,
                         contactPeH: CsvData, profileDetailsPeMQ: CsvData) {
    override def toString: String =
      rtqPeU.toString + rtqPeH.toString +
        backtestMatricsPe.toString + goalPeG.toString +
        goalPeP.toString + goalPeT.toString +
        goalDetailsPeG.toString + accPeP.toString +
        hdPeP.toString + contactPeH.toString +
        profileDetailsPeMQ.toString
  }

  /**
    *
    * @param offsetRange
    * @param csvDataList
    */
  case class MinMaxOffsetCsvData(offsetRange: (Long, Long), csvDataList: CsvDataList)

  /**
    *
    * @param offset
    * @param csvDataList
    */
  case class OffsetCsvStore(offset: Long, csvDataList: CsvDataList)

  /**
    *
    * @param lastBulkApiCall
    * @param lastCacheUpdate
    * @param listOfObj
    */
  case class CacheStore(lastBulkApiCall: Long, lastCacheUpdate: Long,
                        listOfObj: List[(Long, EntityObject)])

  /**
    *
    * @param accessToken
    * @param instanceUrl
    */
  case class ConnectionDetails(accessToken: String, instanceUrl: String)

  /**
    *
    * @param entity
    * @param offSet
    * @param csvData
    * @param noOfRecords
    */
  case class EntityInfo(entity: ENTITY_TYPE, offSet: Long, csvData: String, noOfRecords: Int)

  /**
    *
    * @param rtqPeU
    * @param rtqPeH
    * @param backtestMatricsPe
    * @param goalPeG
    * @param goalPeP
    * @param goalPeT
    * @param goalDePeG
    * @param accPeP
    * @param hdPeP
    * @param contactPeH
    * @param profileDePeMQ
    */
  case class CacheHolder(rtqPeU: List[EntityInfo], rtqPeH: List[EntityInfo],
                         backtestMatricsPe: List[EntityInfo], goalPeG: List[EntityInfo],
                         goalPeP: List[EntityInfo], goalPeT: List[EntityInfo],
                         goalDePeG: List[EntityInfo], accPeP: List[EntityInfo],
                         hdPeP: List[EntityInfo], contactPeH: List[EntityInfo],
                         profileDePeMQ: List[EntityInfo]) {
    def readyForUpload: Boolean =
      rtqPeU.nonEmpty & //rtqPeP.nonEmpty & rtqPeB.nonEmpty & rtqPeG.nonEmpty &
        accPeP.nonEmpty & hdPeP.nonEmpty & goalPeG.nonEmpty & goalDePeG.nonEmpty & profileDePeMQ.nonEmpty

    def isCacheHolderNonEmpty: Boolean =
      rtqPeU.nonEmpty | rtqPeH.nonEmpty | backtestMatricsPe.nonEmpty | goalPeG.nonEmpty |
        goalPeP.nonEmpty | goalPeT.nonEmpty | goalDePeG.nonEmpty | goalDePeG.nonEmpty | accPeP.nonEmpty |
        hdPeP.nonEmpty | contactPeH.nonEmpty | profileDePeMQ.nonEmpty

    /**
      *
      * @param cs
      * @return
      */
    def anyEntityReachedMaxSize(cs: Int): Boolean =
      if (readyForUpload)
        isLimitReached(rtqPeU, cs) |
          isLimitReached(rtqPeH, cs) |
          isLimitReached(backtestMatricsPe, cs) |
          isLimitReached(goalPeG, cs) |
          isLimitReached(goalPeP, cs) |
          isLimitReached(goalPeT, cs) |
          isLimitReached(goalDePeG, cs) |
          isLimitReached(accPeP, cs) |
          isLimitReached(hdPeP,  cs) |
          isLimitReached(contactPeH, cs) |
          isLimitReached(profileDePeMQ, cs)
      else false

    /**
      *
      * @param org
      * @return
      */
    def getCsvData(org: String): CsvDataList = {
      CsvDataList(
        CsvData(RTQ_PE_U.toString, org, getCsvRows(RTQ_PE_U, rtqPeU), getOffset(rtqPeU), getCsvNoOfRows(rtqPeU)),
        CsvData(RTQ_PE_H.toString, org, getCsvRows(RTQ_PE_H, rtqPeH), getOffset(rtqPeH), getCsvNoOfRows(rtqPeH)),
        CsvData(BACKTEST_MATRICES_PE.toString, org, getCsvRows(BACKTEST_MATRICES_PE, backtestMatricsPe), getOffset(backtestMatricsPe), getCsvNoOfRows(backtestMatricsPe)),
        CsvData(GOAL_PE_G.toString, org, getCsvRows(GOAL_PE_G, goalPeG), getOffset(goalPeG), getCsvNoOfRows(goalPeG)),
        CsvData(GOAL_PE_P.toString, org, getCsvRows(GOAL_PE_P, goalPeP), getOffset(goalPeP), getCsvNoOfRows(goalPeP)),
        CsvData(GOAL_PE_T.toString, org, getCsvRows(GOAL_PE_T, goalPeT), getOffset(goalPeT), getCsvNoOfRows(goalPeT)),
        CsvData(GOAL_DETAILS_PE.toString, org, getCsvRows(GOAL_DETAILS_PE, goalDePeG), getOffset(goalDePeG), getCsvNoOfRows(goalDePeG)),
        CsvData(ACCOUNT_PE_P.toString, org, getCsvRows(ACCOUNT_PE_P, accPeP), getOffset(accPeP), getCsvNoOfRows(accPeP)),
        CsvData(HOLDING_PE.toString, org, getCsvRows(HOLDING_PE, hdPeP), getOffset(hdPeP), getCsvNoOfRows(hdPeP)),
        CsvData(CONTACT_PE_H.toString, org, getCsvRows(CONTACT_PE_H, contactPeH), getOffset(contactPeH), getCsvNoOfRows(contactPeH)),
        CsvData(PROFILE_DETAIL_PE.toString, org, getCsvRows(PROFILE_DETAIL_PE, profileDePeMQ), getOffset(profileDePeMQ), getCsvNoOfRows(profileDePeMQ)))

    }

    /**
      * is reached limit
      *
      * @param le list of entity nfo
      * @param cs cache size
      * @return
      */
    private def isLimitReached(le: List[EntityInfo], cs: Int): Boolean =
      le.map(_.csvData.getBytes().length).sum >= cs

    /**
      *
      * @param en
      * @param le
      * @return
      */
    private def getCsvRows(en: ENTITY_TYPE, le: List[EntityInfo]): String =
      le.map(_.csvData).foldLeft(entityCsvHeader(en))(_ + _)

    /**
      *
      * @param le
      * @return
      */
    private def getCsvNoOfRows(le: List[EntityInfo]): Int = {
      val size = le.map(x => x.noOfRecords).reduceOption(_+_).getOrElse(0)
      size
    }

    /**
      *
      * @param le
      * @return
      */
    private def getOffset(le: List[EntityInfo]): List[Long] = le.map(_.offSet).distinct

    /**
      * entity specific header
      *
      * @param entity entity
      * @return
      */
    def entityCsvHeader(entity: ENTITY_TYPE): String = {
      val header: String = entity match {
        case RTQ_PE_U => "Jemstep_Id__c,Address__c,Email__c,Last_Name__c,First_Name__c,Digital_Instance_URL__c,Name__c,Unique_Client_ID__c,Investor_Last_Login_into_Jemstep__c\n"
        case RTQ_PE_H => "Jemstep_Id__c,Spouse__c,Spouse_Name__c,Citizenship_status__c,Country_of_Citizenship__c,Billing_Street__c,Billing_City__c,Billing_State__c,Billing_Postal_Code__c,Billing_Country__c,Shipping_Street__c,Shipping_City__c,Shipping_State__c,Shipping_Postal_Code__c,Shipping_Country__c,Primary_Owner__c,Joint_Owner__c\n"
        case GOAL_PE_G => "Jemstep_Id__c,Jemstep_Questionaire_Name__c,Questionnaire_Type__c,Parent_Jemstep_Id__c,Goal_Name__c,Goal_Type__c,Goal_Status__c\n"
        case GOAL_PE_P => "Jemstep_Id__c,Parent_Jemstep_Id__c,Actual_Value__c\n"
        case GOAL_PE_T => "Jemstep_Id__c,Parent_Jemstep_Id__c,Target_Value__c,Target_Date__c,Model_Risk_Score_Range__c,FSC_Goal_Risk_Score__c,Mapped_Target_Model__c\n"
        case GOAL_DETAILS_PE => "Answer__c,Full_Question__c,Jemstep_Id__c,Parent_Jemstep_Id__c,Question__c,Questionnaire_Type__c,Last_Reviewed_Modified__c,Goal_Creation_Date__c\n"
        case BACKTEST_MATRICES_PE => "Jemstep_Id__c,Annual_Savings__c,Current_Annualized_Return__c,Current_Best_Year__c,Current_Best_Year_Return__c,Current_Cost_Of_Fees__c,Current_Expense_Ratio__c,Current_Worst_Year__c,Current_Worst_Year_Return__c,Current_Year_With_Loss__c,Fee_Savings__c,Target_Annualized_Return__c,Target_Best_Year__c,Target_Best_Year_Return__c,Target_Cost_of_Fees__c,Target_Expense_Ratio__c,Target_Worst_Year__c,Target_Worst_Year_Return__c,Target_Years_With_Loss__c\n"
        case ACCOUNT_PE_P => "Account_Name__c,Account_Number__c,Account_Status__c,Account_Type__c,Account_Value__c,Contact__c,Date_Updated__c,Institution__c,Parent_Account_Id__c,Jemstep_Id__c,Ownership__c,Balance__c,Held_away_Assets__c,Parent_Goal_Id__c\n"
        case HOLDING_PE => "Account_Id__c,Asset_Class__c,Cost_Basis__c,Date_Updated__c,Description__c,Parent_Jemstep_Id__c,Jemstep_Id__c,Price__c,Quantity__c,Symbol__c,Value__c,Account_Name__c,Holding_Name__c\n"
        case CONTACT_PE_H => "Jemstep_Id__c,First_Name__c,Last_Name__c,Tax_Id__c,Birthdate__c,Age__c,Annual_Income__c,Home_Phone__c,Other_Phone__c,Email__c,Mailing_Street__c,Mailing_City__c,Mailing_State__c,Mailing_Postal_Code__c,Mailing_Country__c,Current_Employer__c,Employer_Business_Address__c,Employer_Business_Address_2__c,Employer_City__c,Employer_State__c,Employer_Zip__c,Employer_Country__c\n"
        case PROFILE_DETAIL_PE => "Answer__c,Full_Question__c,Jemstep_Id__c,Parent_Jemstep_Id__c,Question__c,Questionnaire_Type__c,Last_Reviewed_Modified__c\n"
        case _ => ""
      }
      addNameSpace(header)
    }
    val nameSpace = configProps.getProperty("NAMESPACE")

    /**
      *
      * @param head
      * @return
      */
    def addNameSpace(head: String): String = {
      head.split(",").map(x => nameSpace.toString+x).fold("")((x, y) => x+","+y).drop(1)
    }
  }
}
