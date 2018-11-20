package com.jemstep.model

import com.jemstep.model.BusinessEntityModel._

object AccountHoldingDetailsModel {

  /**
    *
    * @param plainValue
    */
  case class AddressLine1(plainValue: String)

  /**
    *
    * @param plainValue
    */
  case class AddressLine2(plainValue: String)

  /**
    *
    * @param plainValue
    */
  case class City(plainValue: String)

  /**
    *
    * @param plainValue
    */
  case class State(plainValue: String)

  /**
    *
    * @param plainValue
    */
  case class ZipCode(plainValue: String)

  /**
    *
    * @param plainValue
    */
  case class Country(plainValue: String)

  /**
    *
    * @param addressLine1
    * @param addressLine2
    * @param city
    * @param state
    * @param zipCode
    * @param country
    */
  case class MailingAddress(addressLine1: AddressLine1, addressLine2: Option[AddressLine2], city: City, state: State, zipCode: ZipCode, country: Option[Country])

  /**
    *
    * @param addressLine1
    * @param addressLine2
    * @param city
    * @param state
    * @param zipCode
    * @param country
    */
  case class HomeAddress(addressLine1: AddressLine1, addressLine2: Option[AddressLine2], city: City, state: State, zipCode: ZipCode, country: Option[Country])

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
  case class Email(plainValue: String)

  /**
    *
    * @param mailingAddress
    * @param firstName
    * @param lastName
    * @param daytimePhoneNumber
    * @param homeAddress
    * @param email
    * @param countryOfCitizenship
    */
  case class TrustedContacts(mailingAddress: Option[MailingAddress],firstName: FirstName,lastName: LastName,daytimePhoneNumber:String,homeAddress:HomeAddress,email: Email,countryOfCitizenship:String,acctUUID: List[String])

  /**
    *
    * @param trustedContacts
    */
  case class TrustedContactDetails(trustedContacts: List[TrustedContacts])

  case class DaytimePhoneNumber(plainValue: String)

  /**
    *
    * @param homeAddress
    * @param mailingAddress
    * @param daytimePhoneNumber
    */
  case class ContactInformation(homeAddress: HomeAddress, mailingAddress: Option[MailingAddress], daytimePhoneNumber: DaytimePhoneNumber)

  /**
    *
    * @param citizenshipStatus
    * @param countryOfCitizenship
    */
  case class CitizenshipInformation(citizenshipStatus: Option[String], countryOfCitizenship: String)

  /**
    *
    * @param annualIncome
    */
  case class FinancialAndTaxInformation(annualIncome: Option[String])

  /**
    *
    * @param addressLine1
    * @param addressLine2
    * @param city
    * @param state
    * @param zipCode
    * @param country
    */
  case class BusinessAddress(addressLine1: AddressLine1, addressLine2: Option[AddressLine2], city: City, state: State, zipCode: ZipCode, country: Option[Country])

  /**
    *
    * @param businessOrEmployerName
    * @param businessAddress
    * @param occupationOrTypeOfBusiness
    */
  case class EmploymentInformation(businessOrEmployerName: Option[String], businessAddress: Option[BusinessAddress], occupationOrTypeOfBusiness: Option[String])

  case class MiddleName(plainValue : String)

  case class DateOfBirth(plainValue: String)

  /**
    *
    * @param middleName
    * @param dateOfBirth
    * @param financialAndTaxInformation
    * @param citizenshipInformation
    * @param contactInformation
    * @param trustedContactDetails
    * @param employmentInformation
    */
  case class OptionalFields(middleName: Option[MiddleName], dateOfBirth: Option[DateOfBirth], financialAndTaxInformation: Option[FinancialAndTaxInformation],
                            citizenshipInformation: Option[CitizenshipInformation], contactInformation: Option[ContactInformation],
                            trustedContactDetails: Option[TrustedContactDetails], employmentInformation: Option[EmploymentInformation])


  case class Ssn(plainValue: String)

  /**
    *
    * @param ssn
    */
  case class SocialSecurity(ssn: Ssn)


  /**
    *
    * @param socialSecurity
    * @param optionalFields
    * @param email
    * @param firstName
    * @param lastName
    */
  case class Person(socialSecurity: SocialSecurity, optionalFields: OptionalFields, email: Email, firstName: FirstName, lastName: LastName)

  /**
    *
    * @param primary
    * @param spouse
    */
  case class AccountHoldingDetails(primary: Option[Person], spouse: Option[Person]) extends BusinessModel {

    import EntityType._

    /**
      *
      * @param userId
      * @param org
      * @param operation
      * @return
      */
    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = {

      val emptyRtqPe =List(RtqPeH(Jemstep_Id__c = "", Spouse__c = false, Spouse_Name__c = "", Citizenship_Status__c = "", Country_of_Citizenship__c = "",
        Billing_Street__c = "",
        //Billing_Address_AddressLine2__c = "",
        Billing_City__c = "",
        Billing_State__c = "",
        Billing_Postal_Code__c = "",
        Billing_Contry__c = "",
        Shipping_Street__c = "",
        //Shipping_Address_AddressLine2__c = "",
        Shipping_City__c = "",
        Shipping_State__c = "",
        Shipping_Postal_Code__c = "",
        Shipping_Contry__c = "",
        Primary_Owner__c = "",
        Joint_Owner__c = false))

      val mailingAddress1 = MailingAddress(addressLine1 = AddressLine1(""), addressLine2 = Some(AddressLine2("")), city = City(""), state = State(""), zipCode = ZipCode(""), country = Some(Country("")))

      val homeAddress1 = HomeAddress(addressLine1 = AddressLine1(""), addressLine2 = Some(AddressLine2("")), city = City(""), state = State(""), zipCode = ZipCode(""), country = Some(Country("")))

      val businessAddress = BusinessAddress(addressLine1 = AddressLine1(""), addressLine2 = Some(AddressLine2("")), city = City(""), state = State(""), zipCode = ZipCode("0.0"), country = Some(Country("")))

      val emptyTrustedContact = List(TrustedContacts(mailingAddress=Some(mailingAddress1),firstName= FirstName(""),lastName= LastName(""),daytimePhoneNumber="",homeAddress=homeAddress1,email= Email(""),countryOfCitizenship="", acctUUID = List("")))

      val emptyTrustedContactDetails = TrustedContactDetails(trustedContacts = emptyTrustedContact)

      val trustedContactsFromSpouse1: TrustedContactDetails = spouse.fold(emptyTrustedContactDetails)(eachSpouse =>
        eachSpouse.optionalFields.trustedContactDetails.fold(emptyTrustedContactDetails) (eachTrustedContactsInfo => eachTrustedContactsInfo))


      val trustedContactDetailsPrimaryOrSpouse : TrustedContactDetails = primary.fold(trustedContactsFromSpouse1)(eachPrimary =>
        eachPrimary.optionalFields.trustedContactDetails.fold(trustedContactsFromSpouse1)(eachTrustedContactsInfo => eachTrustedContactsInfo))

      val trustedContactDetailsPrimaryOrSpouse1 = if(trustedContactDetailsPrimaryOrSpouse.trustedContacts.isDefinedAt(0))
        trustedContactDetailsPrimaryOrSpouse.trustedContacts(0) else emptyTrustedContact(0)

      val emptyContactInformation = ContactInformation(homeAddress=homeAddress1, mailingAddress=Some(mailingAddress1), daytimePhoneNumber = DaytimePhoneNumber(""))

      val contactInformationSpouse: ContactInformation = spouse.fold(emptyContactInformation)(eachSpouse =>
        eachSpouse.optionalFields.contactInformation.fold(emptyContactInformation)(eachContactInformation => eachContactInformation))


      val contactInformationPrimaryOrSpouse : ContactInformation = primary.fold(contactInformationSpouse)(eachPrimary =>
        eachPrimary.optionalFields.contactInformation.fold(contactInformationSpouse)(eachContactInformation => eachContactInformation))


      val emptyCitizenshipInformation = CitizenshipInformation(citizenshipStatus = Some(""), countryOfCitizenship = "")

      val citizenshipInformationSpouse: CitizenshipInformation = spouse.fold(emptyCitizenshipInformation)(eachSpouse =>
        eachSpouse.optionalFields.citizenshipInformation.fold(emptyCitizenshipInformation)(x => x))


      val citizenshipInformationPrimaryOrSpouse : CitizenshipInformation = primary.fold(citizenshipInformationSpouse)(eachPrimary =>
        eachPrimary.optionalFields.citizenshipInformation.fold(citizenshipInformationSpouse)(eachCitizenInformation => eachCitizenInformation))

      val emptyEmploymentInformation = EmploymentInformation(businessOrEmployerName = Some(""), businessAddress = Some(businessAddress), occupationOrTypeOfBusiness = Some(""))

      val employmentInformationSpouse: EmploymentInformation = spouse.fold(emptyEmploymentInformation)(eachSpouse =>
        eachSpouse.optionalFields.employmentInformation.fold(emptyEmploymentInformation)(x => x))


      val employmentInformationSpousePrimaryOrSpouse : EmploymentInformation = primary.fold(employmentInformationSpouse)(eachPrimary =>
        eachPrimary.optionalFields.employmentInformation.fold(employmentInformationSpouse)(eachemploymentInformationSpouse => eachemploymentInformationSpouse))


      val accountHoldingDetailsInvestorPe1: List[RtqPeH] =
        spouse.fold(emptyRtqPe)(eachSpouse =>
            List(RtqPeH(Jemstep_Id__c = userId, Spouse__c = isSpouseExists(spouse), Spouse_Name__c = getSpouseName(spouse),
              Citizenship_Status__c = getcitizenshipInformation(Some(citizenshipInformationPrimaryOrSpouse), "citizenshipStatus"),
              Country_of_Citizenship__c = getcitizenshipInformation(Some(citizenshipInformationPrimaryOrSpouse), "countryOfCitizenship"),
              Billing_Street__c = getContactInformationHomeAddress(Some(contactInformationPrimaryOrSpouse), "addressLine1"),
              //Billing_Address_AddressLine2__c = contactInformation(eachSpouse.optionalFields.contactinformation, "addressLine2"),
              Billing_City__c = getContactInformationHomeAddress(Some(contactInformationPrimaryOrSpouse), "city"),
              Billing_State__c = getContactInformationHomeAddress(Some(contactInformationPrimaryOrSpouse), "state"),
              Billing_Postal_Code__c = getContactInformationHomeAddress(Some(contactInformationPrimaryOrSpouse), "zip"),
              Billing_Contry__c = getContactInformationHomeAddress(Some(contactInformationPrimaryOrSpouse), "country"),
              Shipping_Street__c = getContactInformationMailingAddress(Some(contactInformationPrimaryOrSpouse), "addressLine1"),
              //Shipping_Address_AddressLine2__c = eachTrustedContacts.mailingAddress.fold("")(x => getMailingAddress(x, "addressLine2")),
              Shipping_City__c = getContactInformationMailingAddress(Some(contactInformationPrimaryOrSpouse), "city"),
              Shipping_State__c = getContactInformationMailingAddress(Some(contactInformationPrimaryOrSpouse), "state"),
              Shipping_Postal_Code__c = getContactInformationMailingAddress(Some(contactInformationPrimaryOrSpouse), "zip"),
              Shipping_Contry__c = getContactInformationMailingAddress(Some(contactInformationPrimaryOrSpouse), "country"),
              Primary_Owner__c = eachSpouse.firstName.plainValue.toString + " " + eachSpouse.lastName.plainValue.toString,
              Joint_Owner__c = isJointOwner(primary, spouse)
            )))

      /*Trusted_Contact_Name__c = trustedContactDetailsPrimaryOrSpouse1.firstName.plainValue+" "+trustedContactDetailsPrimaryOrSpouse1.lastName.plainValue,
      Trusted_Contact_Phone__c = trustedContactDetailsPrimaryOrSpouse1.daytimePhoneNumber,
      Trusted_Contact_Address__c = getTrustedContactAddress(trustedContactDetailsPrimaryOrSpouse1.homeAddress),
      Trusted_Contact_Mailing_Address__c = getTrustedContactMailingAddress(trustedContactDetailsPrimaryOrSpouse1.mailingAddress),
      Trusted_Contact_Email_address__c = trustedContactDetailsPrimaryOrSpouse1.email.plainValue,
      Trusted_Contact_Country_of_Citizenship__c = trustedContactDetailsPrimaryOrSpouse1.countryOfCitizenship
      */

      val accountHoldingDetailsInvestorPe: List[RtqPeH] =
        primary.fold(accountHoldingDetailsInvestorPe1)(eachPrimary =>
            List(RtqPeH(Jemstep_Id__c = userId, Spouse__c = isSpouseExists(spouse), Spouse_Name__c = getSpouseName(spouse),
              Citizenship_Status__c = getcitizenshipInformation(Some(citizenshipInformationPrimaryOrSpouse), "citizenshipStatus"),
              Country_of_Citizenship__c = getcitizenshipInformation(Some(citizenshipInformationPrimaryOrSpouse), "countryOfCitizenship"),
              Billing_Street__c = getContactInformationHomeAddress(Some(contactInformationPrimaryOrSpouse), "addressLine1"),
              //Billing_Address_AddressLine2__c = contactInformation(eachPrimary.optionalFields.contactinformation, "addressLine2"),
              Billing_City__c = getContactInformationHomeAddress(Some(contactInformationPrimaryOrSpouse), "city"),
              Billing_State__c = getContactInformationHomeAddress(Some(contactInformationPrimaryOrSpouse), "state"),
              Billing_Postal_Code__c = getContactInformationHomeAddress(Some(contactInformationPrimaryOrSpouse), "zip"),
              Billing_Contry__c = getContactInformationHomeAddress(Some(contactInformationPrimaryOrSpouse), "country"),
              Shipping_Street__c = getContactInformationMailingAddress(Some(contactInformationPrimaryOrSpouse), "addressLine1"),
              //Shipping_Address_AddressLine2__c = eachTrustedContacts.mailingAddress.fold("")(x => getMailingAddress(x, "addressLine2")),
              Shipping_City__c = getContactInformationMailingAddress(Some(contactInformationPrimaryOrSpouse), "city"),
              Shipping_State__c = getContactInformationMailingAddress(Some(contactInformationPrimaryOrSpouse), "state"),
              Shipping_Postal_Code__c = getContactInformationMailingAddress(Some(contactInformationPrimaryOrSpouse), "zip"),
              Shipping_Contry__c = getContactInformationMailingAddress(Some(contactInformationPrimaryOrSpouse), "country"),
              Primary_Owner__c = eachPrimary.firstName.plainValue.toString + " " + eachPrimary.lastName.plainValue.toString,
              Joint_Owner__c = isJointOwner(primary, spouse)
              )))

      val listOfIPEObjects = for{ elem <- accountHoldingDetailsInvestorPe if(elem.Jemstep_Id__c != "")} yield elem

      val listOfEntityModel1: EntityModel = EntityModel(RTQ_PE_H, org, listOfIPEObjects)


      val emptyContactPe =List(ContactPeH(Jemstep_Id__c = "", First_Name__c = "",
        Last_Name__c = "", Tax_Id__c = "", Birthdate__c = "",
        Age__c = 0.0, Annual_Income__c = 0.0, Home_Phone__c = "", Other_Phone__c = "",
        Email__c = "", Mailing_Street__c = "",
        //Mailing_Address_AddressLine2__c = "",
        Mailing_City__c = "",
        Mailing_State__c = "", Mailing_Postal_Code__c = "",
        Mailing_Country__c = "", Current_Employer__c = "", Employer_Business_Address__c = "",
        Employer_Business_Address_2__c = "", Employer_City__c = "", Employer_State__c = "",
        Employer_Zip__c = 0.0, Employer_Country__c = ""))

      val accountHoldingDetailsContactPe1: List[ContactPeH] =
        spouse.fold(emptyContactPe)(eachSpouse =>
              List(ContactPeH(Jemstep_Id__c = userId,
                First_Name__c = eachSpouse.firstName.plainValue,
                Last_Name__c = eachSpouse.lastName.plainValue,
                Tax_Id__c = eachSpouse.socialSecurity.ssn.plainValue,
                Birthdate__c = eachSpouse.optionalFields.dateOfBirth.fold("")(d => d.plainValue),
                Age__c = 0.0,
                Annual_Income__c = eachSpouse.optionalFields.financialAndTaxInformation.fold(0.0)(x => x.annualIncome.fold(0.0)(x => x.toDouble)),
                Home_Phone__c = eachSpouse.optionalFields.contactInformation.fold("")(x => x.daytimePhoneNumber.plainValue),
                Other_Phone__c = "",
                Email__c = eachSpouse.email.plainValue,
                Mailing_Street__c = trustedContactDetailsPrimaryOrSpouse1.mailingAddress.fold("")(x => getMailingAddress(x, "addressLine1")),
                //Mailing_Address_AddressLine2__c = eachTrustedContacts.mailingAddress.fold("")(x => getMailingAddress(x, "addressLine2")),
                Mailing_City__c = trustedContactDetailsPrimaryOrSpouse1.mailingAddress.fold("")(x => getMailingAddress(x, "city")),
                Mailing_State__c = trustedContactDetailsPrimaryOrSpouse1.mailingAddress.fold("")(x => getMailingAddress(x, "state")),
                Mailing_Postal_Code__c = trustedContactDetailsPrimaryOrSpouse1.mailingAddress.fold("")(x => getMailingAddress(x, "zip")),
                Mailing_Country__c = trustedContactDetailsPrimaryOrSpouse1.mailingAddress.fold("")(x => getMailingAddress(x, "country")),
                Current_Employer__c = employmentInformationSpousePrimaryOrSpouse.businessOrEmployerName.fold("")(x => x),
                Employer_Business_Address__c = getEmployerAddress(Some(employmentInformationSpousePrimaryOrSpouse), "address1"),
                Employer_Business_Address_2__c = getEmployerAddress(Some(employmentInformationSpousePrimaryOrSpouse), "address2"),
                Employer_City__c = getEmployerAddress(Some(employmentInformationSpousePrimaryOrSpouse), "city"),
                Employer_State__c = getEmployerAddress(Some(employmentInformationSpousePrimaryOrSpouse), "state"),
                Employer_Zip__c = getEmployerAddress(Some(employmentInformationSpousePrimaryOrSpouse), "zip").toDouble,
                Employer_Country__c = getEmployerAddress(Some(employmentInformationSpousePrimaryOrSpouse), "country"))))

      val accountHoldingDetailsContactPe:List[ContactPeH] =
        primary.fold(accountHoldingDetailsContactPe1)(eachPrimary =>
              List(ContactPeH(Jemstep_Id__c = userId,
                First_Name__c = eachPrimary.firstName.plainValue,
                Last_Name__c = eachPrimary.lastName.plainValue,
                Tax_Id__c = eachPrimary.socialSecurity.ssn.plainValue,
                Birthdate__c = eachPrimary.optionalFields.dateOfBirth.fold("")(d => d.plainValue),
                Age__c = 0.0,
                Annual_Income__c = eachPrimary.optionalFields.financialAndTaxInformation.fold(0.0)(x => x.annualIncome.fold(0.0)(x => x.toDouble)),
                Home_Phone__c = eachPrimary.optionalFields.contactInformation.fold("")(x => x.daytimePhoneNumber.plainValue),
                Other_Phone__c = "",
                Email__c = eachPrimary.email.plainValue,
                Mailing_Street__c = trustedContactDetailsPrimaryOrSpouse1.mailingAddress.fold("")(x => getMailingAddress(x, "addressLine1")),
                //Mailing_Address_AddressLine2__c = eachTrustedContacts.mailingAddress.fold("")(x => getMailingAddress(x, "addressLine2")),
                Mailing_City__c = trustedContactDetailsPrimaryOrSpouse1.mailingAddress.fold("")(x => getMailingAddress(x, "city")),
                Mailing_State__c = trustedContactDetailsPrimaryOrSpouse1.mailingAddress.fold("")(x => getMailingAddress(x, "state")),
                Mailing_Postal_Code__c = trustedContactDetailsPrimaryOrSpouse1.mailingAddress.fold("")(x => getMailingAddress(x, "zip")),
                Mailing_Country__c = trustedContactDetailsPrimaryOrSpouse1.mailingAddress.fold("")(x => getMailingAddress(x, "country")),
                Current_Employer__c = employmentInformationSpousePrimaryOrSpouse.businessOrEmployerName.fold("")(x => x),
                Employer_Business_Address__c = getEmployerAddress(Some(employmentInformationSpousePrimaryOrSpouse), "address1"),
                Employer_Business_Address_2__c = getEmployerAddress(Some(employmentInformationSpousePrimaryOrSpouse), "address2"),
                Employer_City__c = getEmployerAddress(Some(employmentInformationSpousePrimaryOrSpouse), "city"),
                Employer_State__c = getEmployerAddress(Some(employmentInformationSpousePrimaryOrSpouse), "state"),
                Employer_Zip__c = getEmployerAddress(Some(employmentInformationSpousePrimaryOrSpouse), "zip").toDouble,
                Employer_Country__c = getEmployerAddress(Some(employmentInformationSpousePrimaryOrSpouse), "country"))))

      val listOfObj2 = for( elem <- accountHoldingDetailsContactPe if(elem.Jemstep_Id__c != "")) yield elem

      val listOfEntityModel2: EntityModel = EntityModel(CONTACT_PE_H, org, listOfObj2)

      List(listOfEntityModel1, listOfEntityModel2)
    }

   /* /**
      *
      * @param datofbirth
      * @return
      */
    def getAgeFromDateOfBirth(datofbirth:String): Double ={
      val smp_dt_frmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
      val d = smp_dt_frmt.parse(datofbirth)
      val formatted_dateTime = d.getTime
      val current_time = Calendar.getInstance().getTimeInMillis
      val diffInMillies = Math.abs(current_time - formatted_dateTime)
      val c = Calendar.getInstance
      c.setTimeInMillis(diffInMillies)
      val mYear = c.get(Calendar.YEAR)-1970
      //val  mMonth = c.get(Calendar.MONTH);
      //val  mDay = c.get(Calendar.DAY_OF_MONTH);
      mYear.toDouble

    }*/

    def getMailingAddress(x: MailingAddress, addressType: String): String ={
      addressType match{
        case "addressLine1" => x.addressLine1.plainValue
        case "addressLine2" => x.addressLine2.fold("")(x =>x.plainValue)
        case "city" => x.city.plainValue
        case "state" => x.state.plainValue
        case "zip" => x.zipCode.plainValue
        case "country" => x.country.fold("")(x => x.plainValue)
      }
    }

    /**
      *
      * @param citizenshipInformation
      * @param citizenship_information
      * @return
      */
    def getcitizenshipInformation(citizenshipInformation: Option[CitizenshipInformation], citizenship_information: String): String = {
      citizenshipInformation.fold("")(x=> citizenship_information match {
        case "citizenshipStatus" => x.citizenshipStatus.fold("")(c => c)
        case "countryOfCitizenship" => x.countryOfCitizenship
      })

    }

    /**
      *
      * @param contactinformation
      * @param field
      * @return
      */
    def getContactInformationHomeAddress(contactinformation: Option[ContactInformation], field: String): String = {
      contactinformation.fold("")(x => field match {
        case "addressLine1" => x.homeAddress.addressLine1.plainValue
        case "addressLine2" => x.homeAddress.addressLine2.fold("")(x => x.plainValue)
        case "city" => x.homeAddress.city.plainValue
        case "state" => x.homeAddress.state.plainValue
        case "zip" => x.homeAddress.zipCode.plainValue
        case "country" => x.homeAddress.country.fold("")(y => y.plainValue)
        case "daytimephonenumber" => x.daytimePhoneNumber.plainValue
      })

    }

    /**
      *
      * @param contactinformation
      * @param field
      * @return
      */
    def getContactInformationMailingAddress(contactinformation: Option[ContactInformation], field: String): String = {
          contactinformation.fold("")(x => field match {
            case "addressLine1" => x.mailingAddress.fold("")(y => y.addressLine1.plainValue)
            case "addressLine2" => x.mailingAddress.fold("")(y => y.addressLine2.fold("")(z => z.plainValue))
            case "city" => x.mailingAddress.fold("")(y => y.city.plainValue)
            case "state" => x.mailingAddress.fold("")(y => y.state.plainValue)
            case "zip" => x.mailingAddress.fold("")(y => y.zipCode.plainValue)
            case "country" => x.mailingAddress.fold("")(y => y.country.fold("")(z => z.plainValue))
          })
        }
      }

    /**
      *
      * @param spouse
      * @return
      */
    def isSpouseExists(spouse: Option[Person]): Boolean = {
      spouse.fold(false)(x=> true)
    }

    /**
      *
      * @param spouse
      * @return
      */
    def getSpouseName(spouse: Option[Person]): String = {
      spouse.fold("")(x=> x.firstName.plainValue + " " + x.lastName.plainValue)
    }

    /**
      *
      * @param employmentInformation
      * @param addressType
      * @return
      */
    def getEmployerAddress(employmentInformation: Option[EmploymentInformation], addressType: String): String = {
      employmentInformation.fold("")(x=> addressType match {
        case "address1" => x.businessAddress.fold("")(y => y.addressLine1.plainValue)
        case "address2" => x.businessAddress.fold("")(y => y.addressLine2.fold("")(z => z.plainValue))
        case "city" => x.businessAddress.fold("")(y => y.city.plainValue)
        case "state" => x.businessAddress.fold("")(y => y.state.plainValue)
        case "zip" => x.businessAddress.fold("0.0")(y => y.zipCode.plainValue)
        case "country" => x.businessAddress.fold("")(y => y.country.fold("")(z => z.plainValue))
      })

    }

    /**
      *
      * @param primary
      * @param spouse
      * @return
      */
    def getPrimary_Owner(primary: Option[Person], spouse: Option[Person]): String = {
      primary.fold(spouse.fold("")(x=>x.firstName.plainValue+x.lastName.plainValue))(x=>x.firstName.plainValue+x.lastName.plainValue)
    }

    /**
      *
      * @param primary
      * @param spouse
      * @return
      */
    def isJointOwner(primary: Option[Person], spouse: Option[Person]): Boolean = {
      primary.fold(false)(x => spouse.fold(false)(x => true))
    }

  /**
    *
    * @param homeAddress
    * @return
    */
    def getTrustedContactAddress(homeAddress: HomeAddress):String ={
     val address = homeAddress.addressLine1.plainValue+"|"+homeAddress.addressLine2.fold("")(x => x.plainValue)+"|"+homeAddress.city.plainValue+"|"+
        homeAddress.state.plainValue+"|"+homeAddress.zipCode.plainValue+"|"+homeAddress.country.fold("")(y => y.plainValue)
      if(address.getBytes().length > 5) address else ""
    }

  /**
    *
    * @param mailingAddress
    * @return
    */
    def getTrustedContactMailingAddress(mailingAddress: Option[MailingAddress]):String = {
      val address = mailingAddress.fold("")(x => x.addressLine1.plainValue+"|"+x.addressLine2.fold("")(y => y.plainValue)+"|"+x.city.plainValue+"|"+
        x.state.plainValue+"|"+x.zipCode.plainValue+"|"+x.country.fold("")(z => z.plainValue))
      if(address.getBytes().length > 5) address else ""
    }

}
