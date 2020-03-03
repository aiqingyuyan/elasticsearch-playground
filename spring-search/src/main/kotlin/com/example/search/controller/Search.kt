package com.example.search.controller

import com.example.search.repository.AccountsResource
import com.example.search.repository.AllergiesResource
import com.example.search.repository.MedicalConditionsResource
import com.example.search.model.Account
import com.example.search.model.Allergy
import com.example.search.model.MedicalCondition
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import info.debatty.java.stringsimilarity.JaroWinkler

@RestController
class Search(
	private val accountsResource: AccountsResource,
	private val medicalConditionsResource: MedicalConditionsResource,
	private val allergiesResource: AllergiesResource
) {

	val jw = JaroWinkler()

	@GetMapping("/accounts")
	fun searchAccounts(@RequestParam(name = "first_name") firstName: String): List<Account> =
		accountsResource
			.loadedAccounts
			.filter {
				it.firstname.toLowerCase().contains(firstName.toLowerCase())
			}

	@GetMapping("/med")
	fun searchMedicalConditions(@RequestParam(name = "description") description: String): List<MedicalCondition> =
		medicalConditionsResource
			.loadedMedicalConditions
			.filter {
				it.description.toLowerCase().contains(description.toLowerCase())
			}

	@GetMapping("/allergies")
	fun searchAllergies(@RequestParam(name = "description") description: String): List<Allergy> =
		allergiesResource
			.loadedAllergies
			.filter {
				val s1 = it.description.toLowerCase()
				val s2 = description.toLowerCase()
				s1.contains(s2) || jw.similarity(s1, s2) > 0.8
			}
			.sortedBy { it.description }
}
