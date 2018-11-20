package com.jemstep.model

import com.jemstep.model.BusinessEntityModel.{EntityModel, EntityObject, EntityType, ProfileDetailPe}
import com.jemstep.model.GoalModel._

object QuestionnaireModel {


  /**
    *
    * @param questions
    */
  case class ModelQuestionnaire(questions: List[Questions]) extends BusinessModel {
    import EntityType._



    /**
      *
      * @param userId
      * @param org
      * @param operation
      * @return
      */
    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = {

        val listOfQuestionnaireDetails: List[EntityObject] =
          questions.map(x =>
            ProfileDetailPe(Answer__c = x.answer.fold("")(a => a), Full_Question__c = getFullQuestion(org.toUpperCase, x.questionId).fold("Unknown Question")(q => q),
              Jemstep_Id__c = userId + "_" + x.questionId, Parent_Jemstep_Id__c = userId,
              Question__c = x.questionId, Questionnaire_Type__c = "PROFILE",
              Last_Reviewed_Modified__c = ""))

        val listOfEntityModel2: EntityModel = EntityModel(PROFILE_DETAIL_PE, org, listOfQuestionnaireDetails)

        listOfEntityModel2 :: Nil
    }
  }
}
