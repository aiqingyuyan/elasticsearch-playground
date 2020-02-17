package com.example.search.config

import com.example.search.model.Account
import com.example.search.utils.JacksonMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import java.lang.Exception
import java.util.*

@Configuration
class AccountsResource {
  @Autowired
  private lateinit var resourceLoader: ResourceLoader

  @Autowired
  private lateinit var jacksonMapper: JacksonMapper

  val loadedAccounts: MutableList<Account> = mutableListOf()

  @Bean
  fun getAccounts() {
    val file = resourceLoader.getResource("classpath:accounts.json")
    val sc = Scanner(file.inputStream)

    while (sc.hasNextLine()) {
      try {
        val account = jacksonMapper.deserializeJSONToObject(sc.nextLine(), Account::class)
        loadedAccounts.add(account)
      } catch (e: Exception) {
//        logger.error(e.message)
      }
    }
  }

  private companion object {
    val logger = LoggerFactory.getLogger((AccountsResource.javaClass))
  }
}
