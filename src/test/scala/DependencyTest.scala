
import com.jemstep.model.BusinessEntityModel.EntityType._
import com.jemstep.model.CustomModel.{CacheHolder, EntityHolder, EntityInfo}

trait   DependencyTest {


  def NewCacheActor_actor(cache: scala.collection.mutable.Map[String, CacheHolder], x: EntityHolder): Unit = {
    val newCacheHolder: CacheHolder = cache.get("nextgenbank")
      .map(existingData =>
        insertIntoCacheHolder(existingData, x.holder.entityType,
          EntityInfo(x.holder.entityType, x.offSet, x.holder.toString, x.holder.jsonObj.size)))
      .getOrElse(insertIntoCacheHolder(getEmptyCacheHolder, x.holder.entityType,
        EntityInfo(x.holder.entityType, x.offSet, x.holder.toString, x.holder.jsonObj.size)))
    cache("nextgenbank") = newCacheHolder
  }


  //getHierarchy(cache,organizationId1)


  def getHierarchy(cahehedData : scala.collection.mutable.Map[String, CacheHolder],organization: String) : Unit ={

    val csvDataList = cahehedData.get("nextgenbank").get.getCsvData(organization)
    // s"org: `${csvData.org}`, entity: `${csvData.entity}`, noOfRecords: `${csvData.noOfRows}`, actulData: `${csvData.csvObject}` \n\n
    //println("size of the csvDatalist : "+csvDataList.toString.size)
    println(s"entity :: ${csvDataList.rtqPeU.entity},noOfRows :: ${csvDataList.rtqPeU.noOfRows},actulData :: ${csvDataList.rtqPeU.csvObject}\n\n")
    println(s"entity :: ${csvDataList.rtqPeH.entity},noOfRows :: ${csvDataList.rtqPeH.noOfRows},actulData :: ${csvDataList.rtqPeH.csvObject}\n\n")
    println(s"entity :: ${csvDataList.backtestMatricsPe.entity},noOfRows :: ${csvDataList.backtestMatricsPe.noOfRows},actulData :: ${csvDataList.backtestMatricsPe.csvObject}\n\n")
    println(s"entity :: ${csvDataList.goalPeG.entity},noOfRows :: ${csvDataList.goalPeG.noOfRows},actulData :: ${csvDataList.goalPeG.csvObject}\n\n")
    println(s"entity :: ${csvDataList.goalPeP.entity},noOfRows :: ${csvDataList.goalPeP.noOfRows},actulData :: ${csvDataList.goalPeP.csvObject}\n\n")
    println(s"entity :: ${csvDataList.goalPeT.entity},noOfRows :: ${csvDataList.goalPeT.noOfRows},actulData :: ${csvDataList.goalPeT.csvObject}\n\n")
    println(s"entity :: ${csvDataList.goalDetailsPeG.entity},noOfRows :: ${csvDataList.goalDetailsPeG.noOfRows},actulData :: ${csvDataList.goalDetailsPeG.csvObject}\n\n")
    println(s"entity :: ${csvDataList.accPeP.entity},noOfRows :: ${csvDataList.accPeP.noOfRows},actulData :: ${csvDataList.accPeP.csvObject}\n\n")
    println(s"entity :: ${csvDataList.hdPeP.entity},noOfRows :: ${csvDataList.hdPeP.noOfRows},actulData :: ${csvDataList.hdPeP.csvObject}\n\n")
    println(s"entity :: ${csvDataList.contactPeH.entity},noOfRows :: ${csvDataList.contactPeH.noOfRows},actulData :: ${csvDataList.contactPeH.csvObject}\n\n")
    println(s"entity :: ${csvDataList.profileDetailsPeMQ.entity},noOfRows :: ${csvDataList.profileDetailsPeMQ.noOfRows},actulData :: ${csvDataList.profileDetailsPeMQ.csvObject}\n\n")
  }


  private def insertIntoCacheHolder(ch: CacheHolder, entity: ENTITY_TYPE,
                                    ei: EntityInfo): CacheHolder =
    entity match {
      case RTQ_PE_U => ch.copy(rtqPeU = ch.rtqPeU :+ ei)
      case RTQ_PE_H => ch.copy(rtqPeH = ch.rtqPeH :+ ei)
      case BACKTEST_MATRICES_PE => ch.copy(backtestMatricsPe = ch.backtestMatricsPe :+ ei)
      case GOAL_PE_G => ch.copy(goalPeG = ch.goalPeG :+ ei)
      case GOAL_PE_P => ch.copy(goalPeP = ch.goalPeP :+ ei)
      case GOAL_PE_T => ch.copy(goalPeT = ch.goalPeT :+ ei)
      case GOAL_DETAILS_PE => ch.copy(goalDePeG = ch.goalDePeG :+ ei)
      case ACCOUNT_PE_P => ch.copy(accPeP = ch.accPeP :+ ei)
      case HOLDING_PE => ch.copy(hdPeP = ch.hdPeP :+ ei)
      case CONTACT_PE_H => ch.copy(contactPeH = ch.contactPeH :+ ei)
      case PROFILE_DETAIL_PE => ch.copy(profileDePeMQ = ch.profileDePeMQ :+ ei)
    }


  private def getEmptyCacheHolder: CacheHolder ={
    CacheHolder(rtqPeU = List.empty[EntityInfo], rtqPeH = List.empty[EntityInfo],
      backtestMatricsPe = List.empty[EntityInfo], goalPeG = List.empty[EntityInfo],
      goalPeP = List.empty[EntityInfo], goalPeT = List.empty[EntityInfo],
      goalDePeG = List.empty[EntityInfo], accPeP = List.empty[EntityInfo],
      hdPeP = List.empty[EntityInfo], contactPeH = List.empty[EntityInfo],
      profileDePeMQ = List.empty[EntityInfo])}

}

