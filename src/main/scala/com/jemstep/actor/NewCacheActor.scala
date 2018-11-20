package com.jemstep.actor

import akka.actor._
import com.jemstep.logging.BulkStreamLogging.logInformation
import com.jemstep.model.BusinessEntityModel.EntityType
import com.jemstep.model.CustomModel.{CacheHolder, _}
import com.jemstep.model.MinMaxOffsetCsvDataList

/**
  *
  * @param bulkApiActor
  * NewCacheActor will be caching the data untill reach the configured size limit.
  * Once limit is reached sending the data to BulkApiActor to Upload data to Salesforce and
  * cleaning the Cache data.
  */
class NewCacheActor(bulkApiActor: ActorRef) extends Actor with RestConfig{

  import EntityType._

  val caheLimit = configProps.getProperty("CACHE_LIMIT_KB").toInt
  val waitTime = configProps.getProperty("CACHE_WAITING_TIME_MIN").toInt
  private val MAX_MESSAGE_SIZE: Int = caheLimit * 1024 // 2 * 1024 * 1024 // 2MB data in bytes
  private val MAX_CACHE_WAITING_TIME: Int = waitTime // in min

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def receive: Receive = active(Map.empty[String, CacheHolder], scala.collection.mutable.Map.empty[String, Long])

  /**
    * cache holder org specific CacheHolder object
    *
    * @param cache cache data
    * @return
    */
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def active(cache: Map[String, CacheHolder], inActiveCache: scala.collection.mutable.Map[String, Long]): Receive = {
    case eh: EntityHolder =>
      val newCacheHolder: CacheHolder =
        cache.get(eh.holder.organization)
          .map(existingData =>
            insertIntoCacheHolder(existingData, eh.holder.entityType,
              EntityInfo(eh.holder.entityType, eh.offSet, eh.holder.toString, eh.holder.jsonObj.size)))
          .getOrElse(insertIntoCacheHolder(getEmptyCacheHolder, eh.holder.entityType,
            EntityInfo(eh.holder.entityType, eh.offSet, eh.holder.toString, eh.holder.jsonObj.size)))

      logInformation(s"Cache ready for upload Status: ${newCacheHolder.readyForUpload} and " +
        s"rtqPeU ${newCacheHolder.rtqPeU.nonEmpty} " +
        s"rtqPeH ${newCacheHolder.rtqPeH.nonEmpty} " +
        s"backtestMatrics ${newCacheHolder.backtestMatricsPe.nonEmpty} " +
        s"goalPeG ${newCacheHolder.goalPeG.nonEmpty} " +
        s"goalPeP ${newCacheHolder.goalPeP.nonEmpty} " +
        s"goalPeT ${newCacheHolder.goalPeT.nonEmpty} " +
        s"goalDePEG ${newCacheHolder.goalDePeG.nonEmpty} " +
        s"accPe ${newCacheHolder.accPeP.nonEmpty} " +
        s"holdPE ${newCacheHolder.hdPeP.nonEmpty} " +
        s"contactPEH ${newCacheHolder.contactPeH.nonEmpty} " +
        s"profileDePEMQ ${newCacheHolder.profileDePeMQ.nonEmpty} and" +
        s"Cache Reached the limit: ${newCacheHolder.anyEntityReachedMaxSize(MAX_MESSAGE_SIZE)}")

      val newCache: Map[String, CacheHolder] =
        if (newCacheHolder.anyEntityReachedMaxSize(MAX_MESSAGE_SIZE)) {
        val cache1 =  cache.updated(key = eh.holder.organization, value = newCacheHolder)
          val csvDataElemList = for( elem <- cache1.map(x => x._2.getCsvData(x._1)) if(elem.toString.getBytes.size > 0)) yield elem
          val minMaxCsvDataList = csvDataElemList.map(x => {
            val offsetList = getOffsetList(x)
            MinMaxOffsetCsvDataList(offsetList.min, offsetList.max, x)})
         val finalCsvDataList = prepareCsvDataList(minMaxCsvDataList.toList).map(x => bulkApiActor ! OffsetCsvStore(x._1,x._2))
         logInformation(s"finalCsvDataList: ${finalCsvDataList}")
          Map.empty[String, CacheHolder]
        } else cache.updated(key = eh.holder.organization, value = newCacheHolder)


      logInformation("Data stored into cache for " +
        s"Entity: `${eh.holder.entityType.toString}` " +
        s"Organization: `${eh.holder.organization}`")
      val inActiveCache1 = inActiveCache.updated(key = eh.holder.organization, value = getTimestamp)
      context become active(newCache, inActiveCache1)

    case CacheRefresh =>
      logInformation(s"Cache clear activity started for org")
      val csvDataElemList = for( elem <- cache.map(x => x) if(elem._2.getCsvData(elem._1).toString.getBytes.size > 0)) yield elem

      val minMaxCsvDataList = csvDataElemList.map(x => {
        val csvDataList = x._2.getCsvData(x._1)
        val offsetList = getOffsetList(csvDataList)
        MinMaxOffsetCsvDataList(offsetList.min, offsetList.max, csvDataList)}).toList

      val waitingTimeFlag = for{flag <- csvDataElemList.map(keyValue =>
        if(isLongWaitingTiming(inActiveCache(keyValue._1)) && checkCacheNonEmpty(cache, keyValue._1)) true else false)   if(flag) } yield flag

      if(waitingTimeFlag.toList.contains(true)){
        val finalCsvDataList = prepareCsvDataList(minMaxCsvDataList).map(x => bulkApiActor ! OffsetCsvStore(x._1,x._2) )
           logInformation(s"finalCsvDataList: ${finalCsvDataList}")

        context become active(Map.empty[String, CacheHolder], scala.collection.mutable.Map.empty[String, Long])

      }
  }

  /**
    *
    * @param startTimestamp
    * @return
    */
  private def isLongWaitingTiming(startTimestamp: Long): Boolean =
    ((System.currentTimeMillis() - startTimestamp) / (1000 * 60)).toInt >= MAX_CACHE_WAITING_TIME

  private def getTimestamp: Long = System.currentTimeMillis()

  /**
    *
    * @param ch
    * @param entity
    * @param ei
    * @return
    */
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


  private def getEmptyCacheHolder: CacheHolder =
    CacheHolder(rtqPeU = List.empty[EntityInfo], rtqPeH = List.empty[EntityInfo],
      backtestMatricsPe = List.empty[EntityInfo], goalPeG = List.empty[EntityInfo],
      goalPeP = List.empty[EntityInfo], goalPeT = List.empty[EntityInfo],
      goalDePeG = List.empty[EntityInfo], accPeP = List.empty[EntityInfo],
      hdPeP = List.empty[EntityInfo], contactPeH = List.empty[EntityInfo],
      profileDePeMQ = List.empty[EntityInfo])

  private def checkCacheNonEmpty(cache: Map[String, CacheHolder], org: String): Boolean =
    cache(org) != getEmptyCacheHolder

  /**
    *
    * @param csvDataList
    * @return
    */
  def getOffsetList(csvDataList: CsvDataList): List[Long]={
    val offsetList = csvDataList.rtqPeU.offsets ::: csvDataList.rtqPeH.offsets ::: csvDataList.backtestMatricsPe.offsets :::
      csvDataList.goalPeG.offsets ::: csvDataList.goalPeP.offsets ::: csvDataList.goalPeT.offsets :::
      csvDataList.goalDetailsPeG.offsets ::: csvDataList.accPeP.offsets ::: csvDataList.hdPeP.offsets :::
      csvDataList.contactPeH.offsets ::: csvDataList.profileDetailsPeMQ.offsets
    offsetList
  }

  /**
    *
    * @param csvDataList
    * @return
    */
  def prepareCsvDataList(csvDataList: List[MinMaxOffsetCsvDataList]): List[(Long, CsvDataList)] ={
    val sortList = csvDataList.sortBy(_.min)
    val maxOff = sortList.lastOption.fold(0l)(x=>x.max)
    val t1 = for{a <- sortList} yield a.min
    val x1 = t1.drop(1) :: List(maxOff) :: Nil
    val t2 = for{a <- sortList} yield a.csvDataList
    x1.flatten.zip(t2)
  }
}


