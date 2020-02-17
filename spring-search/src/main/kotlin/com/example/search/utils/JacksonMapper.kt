package com.example.search.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class JacksonMapper {
	private val objMapper = Jackson2ObjectMapperBuilder()
		.build<ObjectMapper>()
		.registerModule(KotlinModule())

	fun <T: Any> deserializeJSONToObject(json: String, type: KClass<T>): T =
		objMapper.readValue(json, type.java)
}
