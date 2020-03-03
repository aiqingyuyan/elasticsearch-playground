package com.example.search.repository.resource

import com.example.search.configuration.DispatcherContext
import com.example.search.model.Account
import com.example.search.utils.JacksonMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Repository
import java.lang.Exception
import java.util.*

@Repository
class AccountsResource(
  resourceLoader: ResourceLoader,
  private val jacksonMapper: JacksonMapper,
  private val dispatcherContext: DispatcherContext
) {
  private lateinit var scanner: Scanner

  init {
    val file = resourceLoader.getResource("classpath:accounts.json")
    scanner = Scanner(file.inputStream)
  }

  @ExperimentalCoroutinesApi
  fun produceAccounts(): Flow<Account> = channelFlow {
    launch(dispatcherContext.workersContext) {
      while (scanner.hasNextLine()) {
        try {
          send(
            jacksonMapper.deserializeJSONToObject(
              scanner.nextLine(),
              Account::class
            )
          )
        } catch (e: Exception) {}
      }
    }
  }
}
