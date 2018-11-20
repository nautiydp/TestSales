package com.jemstep.model

import com.jemstep.model.BusinessEntityModel._
import com.jemstep.model.QuestionnaireModel.ModelQuestionnaire
import com.jemstep.util.FullQuestionConfig

import scala.util.Try

object GoalModel extends FullQuestionConfig{

  /**
    *
    * @param questionId
    * @param answer
    */
  case class Questions(questionId: String, answer: Option[String])

  /**
    *
    * @param goalName
    * @param created
    */
  case class GoalProperties(goalName: String, created: String)

  /**
    *
    * @param questionnaire
    * @param goalType
    * @param id
    * @param goalObjective
    */
  case class Goal(questionnaire: Option[ModelQuestionnaire], goalType: String,
                  id: String, goalObjective: String, properties: GoalProperties) extends BusinessModel {

    import EntityType._

    /**
      *
      * @param userId
      * @param org
      * @param operation
      * @return
      */
    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = {

      val listOfQuestionnaires: List[GoalPe] =
         List(GoalPeG(Jemstep_Id__c = id,
           Questionnaire_Id__c = getQuestionnaireName(goalObjective.toUpperCase),
           Questionnaire_Type__c = "GOAL", Parent_Jemstep_Id__c = userId,
           Goal_Name__c = properties.goalName, Goal_Type__c = goalObjective,
           Goal_Status__c = getGoalStatus(goalType)))

      val listOfEntityModel1: EntityModel = EntityModel(GOAL_PE_G, org, listOfQuestionnaires)

      val goalDetailPe = GoalDetailPe("","","","","","","","")
      val listOfQuestionnaireDetails: List[GoalDetailPe] =
        questionnaire.fold(List(goalDetailPe))(q => q.questions.map(x =>
          GoalDetailPe(Answer__c = x.answer.fold("")(a => a), Full_Question__c = getFullQuestion(org.toUpperCase+goalObjective.toUpperCase, x.questionId).fold("Unknown Question")(x => x),
            Jemstep_Id__c = id+"_"+x.questionId, Parent_Jemstep_Id__c = id,
            Question__c = x.questionId, Questionnaire_Type__c = "GOAL",
            Last_Reviewed_Modified__c = "", Goal_Creation_Date__c = properties.created)))

      val listOfEntityModel2: EntityModel = EntityModel(GOAL_DETAILS_PE, org, listOfQuestionnaireDetails)

      List(listOfEntityModel1, listOfEntityModel2)
    }

    def getGoalStatus(goal: String): String ={
      goal match{
        case "MAIN" => "In Progress"
        case "SANDBOX" => "Not Started"
        case _ => "Unknown"
      }
    }
  }

  /**
    *
    * @param goalObj
    * @return
    */
  def getQuestionnaireName(goalObj : String) : String = {
    goalObj match{
      case "EMERGENCY" => "Emergency Fund"
      case "EDUCATION" => "Education Fund"
      case "RETIREMENT" => "Retirement Fund"
      case "MAJOR_PURCHASE" => "Major Purchase"
      case "GROW_YOUR_WEALTH" => "Grow your Wealth"
      case "PROPOSAL" => "Proposal"
      case "ADVISOR_CREATED" => "Advisor Created"
      case _ => "Unknown Goal"
    }
  }

  /**
    *
    * @param partnerWithQuestionType
    * @param questionId
    * @return
    */
  def getFullQuestion(partnerWithQuestionType: String, questionId: String): Option[String] ={
    Try(map(partnerWithQuestionType).getProperty(questionId)).toOption
  }

}
