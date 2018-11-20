package com.jemstep.model

import com.jemstep.model.BusinessEntityModel._
import com.jemstep.logging.failed.FailureLogging._

object PortfolioByBrokerModel {

  /**
    *
    * @param accountId
    */
  case class Orion(accountId: Int)

  /**
    *
    * @param currentLastUpdated
    */
  case class MI(currentLastUpdated: Option[String])

  /**
    *
    * @param mi
    */
  case class Pershing(mi: Option[MI])

  /**
    *
    * @param orion
    * @param pershing
    */
  case class AccountIntegrations(orion: Option[Orion], pershing: Option[Pershing])

  /**
    *
    * @param assetClass
    * @param costBasis
    * @param description
    * @param unitPrice
    * @param units
    * @param ticker
    * @param uuid
    * @param dollarValue
    */
  case class Positions(assetClass: Option[String], costBasis: Option[String],
                       description: String, unitPrice: Double,
                       units: Double, ticker: Option[String], uuid: Option[String], dollarValue: Double)

  /**
    *
    * @param plainValue
    */
  case class AccountNumber(plainValue: String)

  /**
    *
    * @param accountType
    * @param accountNumber
    * @param accountName
    * @param accountStatus
    * @param dollarValue
    * @param update
    * @param uuid
    * @param accountIntegrations
    * @param positions
    * @param ownership
    * @param goalIds
    */
  case class Accounts(accountType: String, accountNumber: AccountNumber,
                      accountName: String, accountStatus: String,
                      dollarValue: String, update: Option[String],
                      uuid: Option[String], accountIntegrations: AccountIntegrations,
                      positions: List[Positions], ownership: String, goalIds: List[String])

  /**
    *
    * @param accounts
    * @param uuid
    */
  case class Broker(accounts: List[Accounts], uuid: Option[String])

  /**
    *
    * @param brokers
    * @param dollarValue
    */
  case class PortfolioByBroker(brokers: List[Broker], dollarValue: Double)


    case class PortfolioForGoal(goalId: String, portfolioByBroker: PortfolioByBroker) extends BusinessModel {

    import EntityType._

    /**
      *
      * @param userId
      * @param org
      * @param operation
      * @return
      */
    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = {

      val listOfAccountPe: List[EntityObject] =
        portfolioByBroker.brokers.flatMap(x =>
          x.accounts.map(y => {
            AccountPeP(
              Account_Name__c = y.accountName,
              Account_Number__c = y.accountNumber.plainValue,
              Account_Status__c = y.accountStatus,
              Account_Type__c = y.accountType,
              Account_Value__c = y.dollarValue.toDouble,
              Contact__c = userId,
              Date_Updated__c = y.update.fold("")(d => d),
              Institution__c = x.uuid.fold("")(u => u),
              Parent_Jemstep_Id__c = userId,
              Jemstep_Id__c = y.uuid.fold("")(u => u),
              Ownership__c = y.ownership,
              Balance__c = y.dollarValue,
              Held_away_Assets__c = isHeldAwayAsset(y.accountStatus),
	      Parent_Goal_Id__c = goalId)
          }))

      val listOfEntityModel1: EntityModel = EntityModel(ACCOUNT_PE_P, org, listOfAccountPe)

      val listOfHoldingPe: List[HoldingPe] = portfolioByBroker.brokers.flatMap(x =>
        x.accounts.flatMap(y => {
          y.positions.map(p =>
            HoldingPe(Account_Id__c = y.accountIntegrations.orion.fold(0)(o => o.accountId), Asset_Class__c = p.assetClass.fold("")(x => x),
              Cost_Basis__c = p.costBasis.fold(0.0)(x => x.toDouble), Date_Updated__c = y.update.fold("")(d => d),
              Description__c = p.description, Parent_Jemstep_Id__c = y.uuid.fold("")(u => u),
              Jemstep_Id__c = p.uuid.fold("")(u => u), Price__c = p.unitPrice.toDouble,
              Quantity__c = p.units.toDouble, Symbol__c = p.ticker.fold("")(x => x),
              Value__c = p.dollarValue.toDouble,
              Account_Name__c = y.accountName, Holding_Name__c = p.description))
        })
      )

      val listOfHoldingEM = for { elem <- listOfHoldingPe if(elem.Jemstep_Id__c != "") } yield elem
      val JemstepIdNull = listOfHoldingPe diff listOfHoldingEM
      flogErrorMessage(s"UserID: $userId :: org: $org :: PortfolioByBrokerModel positions.uuid is null: $JemstepIdNull")

      val listOfEntityModel2: EntityModel = EntityModel(HOLDING_PE, org, listOfHoldingEM)

      val listOfGoalPortfolio: List[GoalPe] =
        List(GoalPeP(Jemstep_Id__c = goalId, Parent_Goal_Id__c = userId,
          Account_Value__c = portfolioByBroker.dollarValue))

      val listOfEntityModel4: EntityModel = EntityModel(GOAL_PE_P, org, listOfGoalPortfolio)

      List(listOfEntityModel1, listOfEntityModel2, listOfEntityModel4)
    }

      /**
        *
        * @param accountStatus
        * @return
        */
    def isHeldAwayAsset(accountStatus: String): Boolean ={
      accountStatus match{
        case "HeldAway" => true
        case _ => false
      }
    }
  }

}
