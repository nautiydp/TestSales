package com.jemstep.model

import com.jemstep.model.BusinessEntityModel.{EntityModel, EntityType, BacktestMetricsPe}

object BacktestMetricsModel {

  /**
    *
    * @param actualFeeSavingAnnual
    * @param currentAnnualizedReturn
    * @param currentBestYear
    * @param currentBestYearPercentage
    * @param costOfFeesOnCurrentPortfolio
    * @param currentExpenseRatio
    * @param currentWorstYear
    * @param currentWorstYearPercentage
    * @param currentYearsWithLosses
    * @param opportunityFeeSaving20Yrs
    * @param targetAnnualizedReturn
    * @param targetBestYear
    * @param targetBestYearPercentage
    * @param costOfFeesOnTargetPortfolio
    * @param targetExpenseRatio
    * @param targetWorstYear
    * @param targetWorstYearPercentage
    * @param targetYearsWithLosses
    */
  case class BacktestMetrics(actualFeeSavingAnnual: Double, currentAnnualizedReturn: Double,
                             currentBestYear: Int, currentBestYearPercentage: Double,
                             costOfFeesOnCurrentPortfolio: Double, currentExpenseRatio: Double,
                             currentWorstYear: Int, currentWorstYearPercentage: Double,
                             currentYearsWithLosses: Int, opportunityFeeSaving20Yrs: Double,
                             targetAnnualizedReturn: Double, targetBestYear: Int,
                             targetBestYearPercentage: Double, costOfFeesOnTargetPortfolio: Double,
                             targetExpenseRatio: Double, targetWorstYear: Int,
                             targetWorstYearPercentage: Double, targetYearsWithLosses: Int) extends BusinessModel {
    import EntityType._

    /**
      *
      * @param userId
      * @param org
      * @param operation
      * @return
      */
    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = {

      val backtestMetricsPe: BacktestMetricsPe =
        BacktestMetricsPe(Jemstep_Id__c = userId,
          Annual_Savings__c = actualFeeSavingAnnual.toDouble, Current_Annualized_Return__c = currentAnnualizedReturn.toDouble,
          Current_Best_Year__c = currentBestYear, Current_Best_Year_Return__c = currentBestYearPercentage.toDouble,
          Current_Cost_Of_Fees__c = costOfFeesOnCurrentPortfolio.toDouble, Current_Expense_Ratio__c = currentExpenseRatio.toDouble,
          Current_Worst_Year__c = currentWorstYear, Current_Worst_Year_Return__c = currentWorstYearPercentage.toDouble,
          Current_Year_With_Loss__c = currentYearsWithLosses.toDouble, Fee_Savings__c = opportunityFeeSaving20Yrs,
          Target_Annualized_Return__c = targetAnnualizedReturn.toDouble, Target_Best_Year__c = targetBestYear,
          Target_Best_Year_Return__c = targetBestYearPercentage.toDouble, Target_Cost_of_Fees__c = costOfFeesOnTargetPortfolio.toDouble,
          Target_Expense_Ratio__c = targetExpenseRatio.toDouble, Target_Worst_Year__c = targetWorstYear,
          Target_Worst_Year_Return__c = targetWorstYearPercentage.toDouble, Target_Years_With_Loss__c = targetYearsWithLosses.toDouble)

      EntityModel(BACKTEST_MATRICES_PE, org, backtestMetricsPe :: Nil) :: Nil
    }
  }

}
