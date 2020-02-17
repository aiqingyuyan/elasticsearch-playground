package com.example.search.controller

import com.example.search.config.AccountsResource
import com.example.search.model.Account
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Search(
	private val accountsResource: AccountsResource
) {

	@GetMapping("/accounts")
	fun searchAccounts(@RequestParam(name = "first_name") firstName: String): List<Account> =
		accountsResource
			.loadedAccounts
			.filter {
				it.firstname.toLowerCase().contains(firstName.toLowerCase())
			}
}
