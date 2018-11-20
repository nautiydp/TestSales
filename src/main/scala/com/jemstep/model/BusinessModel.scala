package com.jemstep.model

import com.jemstep.model.BusinessEntityModel.EntityModel

trait BusinessModel {

  /**
    *
    * @param userId
    * @param org
    * @param operation
    * @return
    */
  def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel]

}
