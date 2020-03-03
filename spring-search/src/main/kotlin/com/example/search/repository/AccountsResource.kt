package com.example.search.repository

import com.example.search.model.Account
import com.example.search.utils.JacksonMapper
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Repository
import java.lang.Exception
import java.util.*

@Repository
class AccountsResource(resourceLoader: ResourceLoader, jacksonMapper: JacksonMapper) {
  final val loadedAccounts: MutableList<Account> = mutableListOf()

  init {
    val file = resourceLoader.getResource("classpath:accounts.json")
    val sc = Scanner(file.inputStream)

    while (sc.hasNextLine()) {
      try {
        val account = jacksonMapper.deserializeJSONToObject(sc.nextLine(), Account::class)
        loadedAccounts.add(account)
      } catch (e: Exception) {
      }
    }
  }
}
