//package com.example.search.repository
//
//import com.example.search.model.MedicalCondition
//import com.example.search.utils.JacksonMapper
//import org.springframework.core.io.ResourceLoader
//import org.springframework.stereotype.Repository
//import java.lang.Exception
//import java.util.*
//
//@Repository
//class MedicalConditionsResource(resourceLoader: ResourceLoader, jacksonMapper: JacksonMapper) {
//  final val loadedMedicalConditions: MutableList<MedicalCondition> = mutableListOf()
//
//  init {
//    val file = resourceLoader.getResource("classpath:fdb_medcond.json")
//    val sc = Scanner(file.inputStream)
//
//    while (sc.hasNextLine()) {
//      try {
//        val medicalCondition = jacksonMapper.deserializeJSONToObject(sc.nextLine(), MedicalCondition::class)
//        loadedMedicalConditions.add(medicalCondition)
//      } catch (e: Exception) {
//      }
//    }
//  }
//}
