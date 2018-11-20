package com.jemstep.model

import com.jemstep.model.BusinessEntityModel._

object TargetForGoalModel {

  import EntityType._

  /**
    * 
    * @param score
    */
  case class RiskScore(score: String)

  /**
    *
    * @param id
    */
  case class TargetModel(id: String)

  /**
    * 
    * @param goalId
    * @param targetValue
    * @param targetDate
    * @param riskScore
    */
  case class TargetForGoal(goalId: String, targetValue: Long, targetDate: String, riskScore: RiskScore, targetModel: TargetModel) extends BusinessModel {

    /**
      * 
      * @param userId
      * @param org
      * @param operation
      * @return
      */
    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] ={

      val listOfTargetGoals: List[GoalPe] =
        List(GoalPeT(Jemstep_Id__c = goalId, Parent_Goal_Id__c = userId,
          Target_Value__c = targetValue, Target_Date__c = targetDate,
          Model_Risk_Score_Range__c = "",
          Goal_Risk_Score__c = riskScore.score.toDouble,
          Mapped_Target_Model__c = targetModel.id))
      
      val listOfEntityModel1: EntityModel = EntityModel(GOAL_PE_T, org, listOfTargetGoals)

      List(listOfEntityModel1)

    }

  }

}
