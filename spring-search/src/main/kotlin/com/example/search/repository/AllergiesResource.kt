package com.example.search.repository

import com.example.search.model.Allergy
import com.example.search.utils.JacksonMapper
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Repository
import java.lang.Exception
import java.util.*

@Repository
class AllergiesResource(resourceLoader: ResourceLoader, jacksonMapper: JacksonMapper) {
  final val loadedAllergies: MutableList<Allergy> = mutableListOf()

  init {
    val file = resourceLoader.getResource("classpath:allergies.json")
    val sc = Scanner(file.inputStream)

    while (sc.hasNextLine()) {
      try {
        val allergy = jacksonMapper.deserializeJSONToObject(sc.nextLine(), Allergy::class)
        loadedAllergies.add(allergy)
      } catch (e: Exception) {
      }
    }
  }
}
