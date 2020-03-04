package com.example.search.controller

import com.example.search.model.Allergy
import com.example.search.repository.index.AccountsIndex
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Search(
	private val accountsIndex: AccountsIndex
) {

	@GetMapping("/accounts")
	fun searchAccounts(
		@RequestParam field: String,
		@RequestParam query: String,
		@RequestParam(required = false) numOfDocs: String?
	): List<Map<String, String>> {
		return this.accountsIndex.search(field, query, numOfDocs?.toInt() ?: 10)
	}

//	@GetMapping("/allergies")
//	fun searchAllergies(@RequestParam(name = "description") description: String): List<Allergy> =
//		allergiesResource
//			.loadedAllergies
//			.filter {
//				val s1 = it.description.toLowerCase()
//				val s2 = description.toLowerCase()
//				s1.contains(s2) || jw.similarity(s1, s2) > 0.8
//			}
//			.sortedBy { it.description }
}
