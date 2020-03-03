package com.example.search.controller

import com.example.search.repository.resource.AllergiesResource
import com.example.search.model.Allergy
import com.example.search.repository.index.AccountsIndex
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import info.debatty.java.stringsimilarity.JaroWinkler
import org.apache.lucene.document.Document

@RestController
class Search(
	private val accountsIndex: AccountsIndex,
	private val allergiesResource: AllergiesResource
) {

	val jw = JaroWinkler()

	@GetMapping("/accounts")
	fun searchAccounts(@RequestParam(name = "first_name") firstNameQuery: String): List<Document> {
		return this.accountsIndex.search("firstname", firstNameQuery)
	}

//	@GetMapping("/med")
//	fun searchMedicalConditions(@RequestParam(name = "description") description: String): List<MedicalCondition> =
//		medicalConditionsResource
//			.loadedMedicalConditions
//			.filter {
//				it.description.toLowerCase().contains(description.toLowerCase())
//			}

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
