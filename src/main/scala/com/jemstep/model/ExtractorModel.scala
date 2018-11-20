package com.jemstep.model

import com.jemstep.model.BacktestMetricsModel.BacktestMetrics
import com.jemstep.model.BusinessEntityModel.EntityModel
import com.jemstep.model.GoalModel.Goal
import com.jemstep.model.PortfolioByBrokerModel.PortfolioForGoal
import com.jemstep.model.TargetForGoalModel.TargetForGoal
import com.jemstep.model.UserIdentifiedModel.UserIdentified
import com.jemstep.model.QuestionnaireModel.ModelQuestionnaire
import com.jemstep.model.AccountHoldingDetailsModel.AccountHoldingDetails

object ExtractorModel {

  import net.liftweb.json._

  implicit val formats: DefaultFormats = DefaultFormats

  /**
    *
    * @param recordSchemaFullName
    * @param jsonString
    * @param userId
    * @param organizationId
    * @param operation
    * @param offSet
    */
  case class IncomingData(recordSchemaFullName: String,
                          jsonString: String,
                          userId: String,
                          organizationId: String,
                          operation: String,
                          offSet: Long)

  /**
    * default unknown model
    */
  case object UnknownSchema extends BusinessModel {
    /**
      *
      * @param userId
      * @param org
      * @param operation
      * @return
      */
    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = List.empty[EntityModel]
  }

  /**
    * given schema name and json string extract the message and give business model
    *
    * @param incomingData schema name and json string
    * @return
    */
  def parser(incomingData: IncomingData): BusinessModel =
    incomingData.recordSchemaFullName match {
      case "com.jemstep.model.goal.PortfolioForGoal" =>
        parse(incomingData.jsonString).extract[PortfolioForGoal]
      case "com.jemstep.model.goal.Goal" =>
        parse(incomingData.jsonString).extract[Goal]
      case "com.jemstep.model.goal.TargetForGoal" =>
        parse(incomingData.jsonString).extract[TargetForGoal]
      case "com.jemstep.model.questionnaire.ModelQuestionnaire" =>
	parse(incomingData.jsonString).extract[ModelQuestionnaire]
      case "com.jemstep.model.assetliability.BacktestMetrics" =>
        parse(incomingData.jsonString).extract[BacktestMetrics]
      case "com.jemstep.model.events.shared.UserIdentified" =>
        parse(incomingData.jsonString).extract[UserIdentified]
      case "com.jemstep.model.enrollment.AccountHolderDetails" =>
        parse(incomingData.jsonString).extract[AccountHoldingDetails]
      case _ => UnknownSchema
    }

  /**
    *
    * @param schemaName
    * @return
    */
  def validSchema(schemaName: String): Option[String] =
    List("com.jemstep.model.goal.PortfolioForGoal",
      "com.jemstep.model.goal.Goal",
      "com.jemstep.model.goal.TargetForGoal",
      "com.jemstep.model.questionnaire.ModelQuestionnaire",
      "com.jemstep.model.assetliability.BacktestMetrics",
      "com.jemstep.model.events.shared.UserIdentified",
      "com.jemstep.model.enrollment.AccountHolderDetails")
      .find(_ == schemaName)
}
