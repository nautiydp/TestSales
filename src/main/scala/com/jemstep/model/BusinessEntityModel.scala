package com.jemstep.model

object BusinessEntityModel{

  import net.liftweb.json._

  implicit val formats: DefaultFormats = DefaultFormats

  object EntityType extends Enumeration {

    type ENTITY_TYPE = Value

    val RTQ_PE_U: ENTITY_TYPE = Value("Investor_PE__e")
    val RTQ_PE_H: ENTITY_TYPE = Value("Investor_PE__e")
    val BACKTEST_MATRICES_PE: ENTITY_TYPE = Value("Backtest_Metrics_PE__e")
    val GOAL_PE_G: ENTITY_TYPE = Value("Goal_PE__e")
    val GOAL_PE_P: ENTITY_TYPE = Value("Goal_PE__e")
    val GOAL_PE_T: ENTITY_TYPE = Value("Goal_PE__e")
    val GOAL_DETAILS_PE: ENTITY_TYPE = Value("Goal_Detail_PE__e")
    val ACCOUNT_PE_P: ENTITY_TYPE = Value("Account_PE__e")
    val HOLDING_PE: ENTITY_TYPE = Value("Holding_PE__e")
    val CONTACT_PE_H: ENTITY_TYPE = Value("Contact_PE__e")
    val PROFILE_DETAIL_PE: ENTITY_TYPE = Value("Profile_Detail_PE__e")
  }

  object EntityName extends Enumeration {

    type ENTITY_NAME = Value

    val RTQ_PE_U: ENTITY_NAME = Value("UserIdentified_Investor")
    val RTQ_PE_H: ENTITY_NAME = Value("AccountHolderDetails_Investor")
    val BACKTEST_MATRICES_PE: ENTITY_NAME = Value("BacktestMetrics")
    val GOAL_PE_G: ENTITY_NAME = Value("Goal_goal")
    val GOAL_PE_P: ENTITY_NAME = Value("PortfolioForGoal_Goal")
    val GOAL_PE_T: ENTITY_NAME = Value("TargetForGoal_Goal")
    val GOAL_DETAILS_PE: ENTITY_NAME = Value("Goal_GoalDetails")
    val ACCOUNT_PE_P: ENTITY_NAME = Value("PortfolioForGoal_Account")
    val HOLDING_PE: ENTITY_NAME = Value("PortfolioForGoal_Holdings")
    val CONTACT_PE_H: ENTITY_NAME = Value("AccountHolderDetails_Contact")
    val PROFILE_DETAIL_PE: ENTITY_NAME = Value("ModelQuestionnaire_ProfileDetails")
  }


  import EntityType._

  trait EntityObject

  /**
    *
    * @param entityType
    * @param organization
    * @param jsonObj
    */
  case class EntityModel(entityType: ENTITY_TYPE,
                         organization: String,
                         jsonObj: List[EntityObject]) {
    override def toString: String =
      jsonObj.map(_.toString).fold("")((x, y) => x + "\n" + y)
  }

  /**
    *
    * @param Account_Name__c
    * @param Account_Number__c
    * @param Account_Status__c
    * @param Account_Type__c
    * @param Account_Value__c
    * @param Contact__c
    * @param Date_Updated__c
    * @param Institution__c
    * @param Parent_Jemstep_Id__c
    * @param Jemstep_Id__c
    * Constructing the Object from PortfolioForGoal Json
    */
  case class AccountPeP(Account_Name__c: String, Account_Number__c: String,
                        Account_Status__c: String, Account_Type__c: String,
                        Account_Value__c: Double, Contact__c: String,
                        Date_Updated__c: String, Institution__c: String,
                        Parent_Jemstep_Id__c: String, Jemstep_Id__c: String,
                        Ownership__c: String, Balance__c: String,
                        Held_away_Assets__c: Boolean, Parent_Goal_Id__c: String) extends EntityObject {
    override def toString: String =
      "\""+Account_Name__c + "\",\"" + Account_Number__c + "\",\"" +
        Account_Status__c + "\",\"" + Account_Type__c + "\",\"" +
        Account_Value__c.toString + "\",\"" + Contact__c + "\",\"" +
        Date_Updated__c + "\",\"" + Institution__c + "\",\"" +
        Parent_Jemstep_Id__c + "\",\"" + Jemstep_Id__c + "\",\"" +
        Ownership__c + "\",\"" + Balance__c + "\",\"" +
        Held_away_Assets__c.toString + "\",\"" + Parent_Goal_Id__c + "\""
  }

  /**
    *
    * @param Account_Id__c
    * @param Asset_Class__c
    * @param Cost_Basis__c
    * @param Date_Updated__c
    * @param Description__c
    * @param Parent_Jemstep_Id__c
    * @param Jemstep_Id__c
    * @param Price__c
    * @param Quantity__c
    * @param Symbol__c
    * @param Value__c
    * Constructing the Object from PortfolioForGoal Json
    */
  case class HoldingPe(Account_Id__c: Int, Asset_Class__c: String,
                       Cost_Basis__c: Double, Date_Updated__c: String,
                       Description__c: String, Parent_Jemstep_Id__c: String,
                       Jemstep_Id__c: String, Price__c: Double,
                       Quantity__c: Double, Symbol__c: String, Value__c: Double,
                       Account_Name__c: String, Holding_Name__c: String) extends EntityObject {
    override def toString: String =
      "\""+Account_Id__c.toString + "\",\"" + Asset_Class__c + "\",\"" +
        Cost_Basis__c.toString + "\",\"" + Date_Updated__c + "\",\"" +
        Description__c + "\",\"" + Parent_Jemstep_Id__c + "\",\"" +
        Jemstep_Id__c + "\",\"" + Price__c.toString + "\",\"" +
        Quantity__c.toString + "\",\"" + Symbol__c + "\",\"" +
        Value__c.toString + "\",\"" + Account_Name__c + "\",\"" +
        Holding_Name__c +"\""

  }

   /**
    *
    * @param Answer__c
    * @param Full_Question__c
    * @param Jemstep_Id__c
    * @param Question__c
    * @param Questionnaire_Type__c
    * @param Last_Reviewed_Modified__c
    * Constructing the Object from Goal Json
    */
  case class GoalDetailPe(Answer__c: String, Full_Question__c: String,
                          Jemstep_Id__c: String, Parent_Jemstep_Id__c: String,
                          Question__c: String, Questionnaire_Type__c: String,
                          Last_Reviewed_Modified__c: String, Goal_Creation_Date__c: String) extends EntityObject {
    override def toString: String =
      "\""+Answer__c + "\",\"" + Full_Question__c.replaceAll("\"", "\"\"") + "\",\"" +
        Jemstep_Id__c + "\",\"" + Parent_Jemstep_Id__c + "\",\"" +
        Question__c + "\",\"" + Questionnaire_Type__c + "\",\"" +
        Last_Reviewed_Modified__c + "\",\"" + Goal_Creation_Date__c + "\""
  }

  /**
    *
    * @param Answer__c
    * @param Full_Question__c
    * @param Jemstep_Id__c
    * @param Parent_Jemstep_Id__c
    * @param Question__c
    * @param Questionnaire_Type__c
    * @param Last_Reviewed_Modified__c
    * Constructing the Object from ModelQuestionnaire Json
    */
  case class ProfileDetailPe(Answer__c: String, Full_Question__c: String,
                             Jemstep_Id__c: String, Parent_Jemstep_Id__c: String,
                             Question__c: String, Questionnaire_Type__c: String,
                             Last_Reviewed_Modified__c: String) extends EntityObject {
    override def toString: String =
      "\""+Answer__c + "\",\"" + Full_Question__c.replaceAll("\"", "\"\"") + "\",\"" +
        Jemstep_Id__c + "\",\"" + Parent_Jemstep_Id__c + "\",\"" +
        Question__c + "\",\"" + Questionnaire_Type__c + "\",\"" +
        Last_Reviewed_Modified__c + "\""
  }


  abstract class GoalPe() extends EntityObject

  /**
    *
    * @param Jemstep_Id__c
    * @param Questionnaire_Id__c
    * @param Questionnaire_Type__c
    * @param Parent_Jemstep_Id__c
    * Constructing the Object from Goal Json
    */
  case class GoalPeG(Jemstep_Id__c: String,
                     Questionnaire_Id__c: String, Questionnaire_Type__c: String,
                     Parent_Jemstep_Id__c: String, Goal_Name__c: String,
                     Goal_Type__c: String, Goal_Status__c: String) extends GoalPe {
    override def toString: String =
      "\""+Jemstep_Id__c + "\",\"" +
        Questionnaire_Id__c + "\",\"" + Questionnaire_Type__c + "\",\"" +
        Parent_Jemstep_Id__c + "\",\"" + Goal_Name__c + "\",\"" +
        Goal_Type__c + "\",\"" + Goal_Status__c + "\""
  }

  /**
    *
    * @param Jemstep_Id__c
    * @param Parent_Goal_Id__c
    * @param Account_Value__c
    * Constructing the Object from PortfolioForGoal Json
    */
  case class GoalPeP(Jemstep_Id__c: String,
                     Parent_Goal_Id__c: String, Account_Value__c: Double) extends GoalPe {
    override def toString: String =
      "\""+Jemstep_Id__c + "\",\"" + Parent_Goal_Id__c  + "\",\"" + Account_Value__c.toString + "\""
  }

  /**
    *
    * @param Jemstep_Id__c
    * @param Parent_Goal_Id__c
    * @param Target_Value__c
    * @param Target_Date__c
    * @param Model_Risk_Score_Range__c
    * @param Goal_Risk_Score__c
    * Constructing the Object from TargetForGoal Json
    */
  case class GoalPeT(Jemstep_Id__c: String,
                     Parent_Goal_Id__c: String, Target_Value__c: Long,
                     Target_Date__c: String, Model_Risk_Score_Range__c: String,
                     Goal_Risk_Score__c: Double, Mapped_Target_Model__c: String) extends GoalPe {
    override def toString: String = "\"" + Jemstep_Id__c + "\",\"" + Parent_Goal_Id__c  + "\",\"" +
      Target_Value__c.toString + "\",\"" + Target_Date__c + "\",\"" +
      Model_Risk_Score_Range__c + "\",\"" + Goal_Risk_Score__c.toString + "\",\"" +
      Mapped_Target_Model__c + "\""
  }


  abstract class RtqPe() extends EntityObject

  /**
    *
    * @param Jemstep_Id__c
    * @param Annual_Savings__c
    * @param Current_Annualized_Return__c
    * @param Current_Best_Year__c
    * @param Current_Best_Year_Return__c
    * @param Current_Cost_Of_Fees__c
    * @param Current_Expense_Ratio__c
    * @param Current_Worst_Year__c
    * @param Current_Worst_Year_Return__c
    * @param Current_Year_With_Loss__c
    * @param Fee_Savings__c
    * @param Target_Annualized_Return__c
    * @param Target_Best_Year__c
    * @param Target_Best_Year_Return__c
    * @param Target_Cost_of_Fees__c
    * @param Target_Expense_Ratio__c
    * @param Target_Worst_Year__c
    * @param Target_Worst_Year_Return__c
    * @param Target_Years_With_Loss__c
    * Constructing the Object from BacktestMetrics Json
    */
  case class BacktestMetricsPe(Jemstep_Id__c: String,
                               Annual_Savings__c: Double, Current_Annualized_Return__c: Double,
                               Current_Best_Year__c: Int, Current_Best_Year_Return__c: Double,
                               Current_Cost_Of_Fees__c: Double, Current_Expense_Ratio__c: Double,
                               Current_Worst_Year__c: Int, Current_Worst_Year_Return__c: Double,
                               Current_Year_With_Loss__c: Double, Fee_Savings__c: Double,
                               Target_Annualized_Return__c: Double, Target_Best_Year__c: Int,
                               Target_Best_Year_Return__c: Double, Target_Cost_of_Fees__c: Double,
                               Target_Expense_Ratio__c: Double, Target_Worst_Year__c: Int,
                               Target_Worst_Year_Return__c: Double, Target_Years_With_Loss__c: Double) extends EntityObject {
    override def toString: String =
      "\"" + Jemstep_Id__c + "\",\"" +
        Annual_Savings__c.toString + "\",\"" + Current_Annualized_Return__c.toString + "\",\"" +
        Current_Best_Year__c.toString + "\",\"" + Current_Best_Year_Return__c.toString + "\",\"" +
        Current_Cost_Of_Fees__c.toString + "\",\"" + Current_Expense_Ratio__c.toString + "\",\"" +
        Current_Worst_Year__c.toString + "\",\"" + Current_Worst_Year_Return__c.toString + "\",\"" +
        Current_Year_With_Loss__c.toString + "\",\"" + Fee_Savings__c.toString + "\",\"" +
        Target_Annualized_Return__c.toString + "\",\"" + Target_Best_Year__c.toString + "\",\"" +
        Target_Best_Year_Return__c.toString + "\",\"" + Target_Cost_of_Fees__c.toString + "\",\"" +
        Target_Expense_Ratio__c.toString + "\",\"" + Target_Worst_Year__c.toString + "\",\"" +
        Target_Worst_Year_Return__c.toString + "\",\"" + Target_Years_With_Loss__c.toString + "\""
  }

  /**
    *
    * @param Jemstep_Id__c
    * @param Address__c
    * @param Email__c
    * @param Last_Name__c
    * @param First_Name__c
    * Constructing the Object from UserIdentified Json
    */
  case class RtqPeU(Jemstep_Id__c: String, Address__c: String,
                    Email__c: String, Last_Name__c: String,
                    First_Name__c: String, Digital_Instance_URL__c: String,
                    Name__c: String, Unique_Client_ID__c: String,
                    Investor_Last_Login_into_Jemstep__c: String) extends RtqPe {
    override def toString: String =
      "\"" + Jemstep_Id__c + "\",\"" + Address__c + "\",\"" +
        Email__c + "\",\"" + Last_Name__c + "\",\"" +
        First_Name__c + "\",\"" + Digital_Instance_URL__c + "\",\"" +
        Name__c + "\",\"" + Unique_Client_ID__c + "\",\"" + Investor_Last_Login_into_Jemstep__c + "\""
  }

  /**
    *
    * @param Jemstep_Id__c
    * @param Spouse__c
    * @param Spouse_Name__c
    * @param Citizenship_Status__c
    * @param Country_of_Citizenship__c
    * @param Billing_Street__c
    * @param Billing_City__c
    * @param Billing_State__c
    * @param Billing_Postal_Code__c
    * @param Billing_Contry__c
    * @param Shipping_Street__c
    * @param Shipping_City__c
    * @param Shipping_State__c
    * @param Shipping_Postal_Code__c
    * @param Shipping_Contry__c
    * Constructing the Object from AccountHolderDetails Json
    */
  case class RtqPeH(Jemstep_Id__c: String, Spouse__c: Boolean, Spouse_Name__c: String,
                    Citizenship_Status__c: String, Country_of_Citizenship__c: String,
                    Billing_Street__c: String,
                    //Billing_Address_AddressLine2__c: String,
                    Billing_City__c: String,
                    Billing_State__c: String,
                    Billing_Postal_Code__c: String,
                    Billing_Contry__c: String,
                    Shipping_Street__c: String,
                    //Shipping_Address_AddressLine2__c: String,
                    Shipping_City__c: String,
                    Shipping_State__c: String,
                    Shipping_Postal_Code__c: String,
                    Shipping_Contry__c: String,
                    Primary_Owner__c: String,
                    Joint_Owner__c: Boolean) extends RtqPe {
    override def toString: String =
      "\"" + Jemstep_Id__c + "\",\"" + Spouse__c.toString + "\",\"" +  Spouse_Name__c + "\",\"" +
        Citizenship_Status__c + "\",\"" +  Country_of_Citizenship__c + "\",\"" + Billing_Street__c + "\",\"" +
        //Billing_Address_AddressLine2__c + "," +
        Billing_City__c + "\",\"" +
        Billing_State__c + "\",\"" + Billing_Postal_Code__c + "\",\"" +
        Billing_Contry__c + "\",\"" + Shipping_Street__c + "\",\"" +
        //Shipping_Address_AddressLine2__c + "," +
        Shipping_City__c + "\",\"" +
        Shipping_State__c + "\",\"" + Shipping_Postal_Code__c + "\",\"" +
        Shipping_Contry__c + "\",\"" + Primary_Owner__c + "\",\"" +
        Joint_Owner__c.toString + "\""
  }

  /*Trusted_Contact_Name__c: String,
                    Trusted_Contact_Phone__c: String,
                    Trusted_Contact_Address__c: String,
                    Trusted_Contact_Mailing_Address__c: String,
                    Trusted_Contact_Email_address__c: String,
                    Trusted_Contact_Country_of_Citizenship__c: String*/

  /**
    *
    * @param Jemstep_Id__c
    * @param First_Name__c
    * @param Last_Name__c
    * @param Tax_Id__c
    * @param Birthdate__c
    * @param Age__c
    * @param Annual_Income__c
    * @param Home_Phone__c
    * @param Other_Phone__c
    * @param Email__c
    * @param Mailing_Street__c
    * @param Mailing_City__c
    * @param Mailing_State__c
    * @param Mailing_Postal_Code__c
    * @param Mailing_Country__c
    * @param Current_Employer__c
    * @param Employer_Business_Address__c
    * @param Employer_Business_Address_2__c
    * @param Employer_City__c
    * @param Employer_State__c
    * @param Employer_Zip__c
    * @param Employer_Country__c
    * Constructing the Object from AccountHolderDetails Json
    */
  case class ContactPeH(Jemstep_Id__c: String, First_Name__c: String,
                        Last_Name__c: String, Tax_Id__c: String,
                        Birthdate__c: String, Age__c: Double,
                        Annual_Income__c: Double, Home_Phone__c: String,
                        Other_Phone__c: String, Email__c: String,
                        Mailing_Street__c: String,
                        //Mailing_Address_AddressLine2__c: String,
                        Mailing_City__c: String,
                        Mailing_State__c: String,
                        Mailing_Postal_Code__c: String,
                        Mailing_Country__c: String,
                        Current_Employer__c: String,
                        Employer_Business_Address__c: String, Employer_Business_Address_2__c: String,
                        Employer_City__c: String, Employer_State__c: String,
                        Employer_Zip__c: Double, Employer_Country__c: String) extends EntityObject {
    override def toString: String = "\""+ Jemstep_Id__c +"\",\""+ First_Name__c +"\",\""+
      Last_Name__c +"\",\""+ Tax_Id__c +"\",\""+
      Birthdate__c +"\",\""+ Age__c.toString +"\",\""+
      Annual_Income__c.toString +"\",\""+ Home_Phone__c +"\",\""+
      Other_Phone__c +"\",\""+ Email__c +"\",\""+ Mailing_Street__c +"\",\""+
      //Mailing_Address_AddressLine2__c +"\",\""+
      Mailing_City__c +"\",\""+
      Mailing_State__c +"\",\""+ Mailing_Postal_Code__c +"\",\""+
      Mailing_Country__c +"\",\""+ Current_Employer__c +"\",\""+
      Employer_Business_Address__c +"\",\""+ Employer_Business_Address_2__c +"\",\""+
      Employer_City__c +"\",\""+ Employer_State__c +"\",\""+
      Employer_Zip__c.toString +"\",\""+ Employer_Country__c+"\""
  }

  /**
    *
    * @param lm
    * @return
    */
  def getJsonArray(lm: List[EntityObject]): String = {
    import net.liftweb.json.Extraction._
    import net.liftweb.json.JsonAST._
    implicit val formats: DefaultFormats = net.liftweb.json.DefaultFormats
    prettyRender(render(JArray(lm.map(x => decompose(x)))).value)
  }
}
