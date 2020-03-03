package com.example.search.repository.resource

import com.example.search.utils.JacksonMapper
import com.fasterxml.jackson.core.type.TypeReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Repository
import java.lang.Exception
import java.util.*

@Repository
class ResourceProducer(
  private val resourceLoader: ResourceLoader,
  private val jacksonMapper: JacksonMapper
) {

  private val logger = LoggerFactory.getLogger(ResourceProducer::class.java)

  @ExperimentalCoroutinesApi
  fun produceDataFlowFrom(resourceLocation: String): Flow<Map<String, Any>> = channelFlow {
    launch(Dispatchers.IO) {
      resourceLoader
        .getResource(resourceLocation)
        .run { Scanner(this.inputStream) }
        .also { scanner ->
          while (scanner.hasNextLine()) {
            try {
              val obj = jacksonMapper.deserializeJSONToMap(
                scanner.nextLine(),
                object: TypeReference<Map<String, Any>>() {}
              )

              if (obj.containsKey("index")) {
                continue
              } else {
                send(obj)
              }
            } catch (e: Exception) {}
          }

          logger.info("Finished processing resource from: $resourceLocation")
        }
    }
  }
}
