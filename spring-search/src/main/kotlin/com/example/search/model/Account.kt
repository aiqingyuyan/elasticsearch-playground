package com.example.search.model


data class Account(
	val account_number: Int,
	val balance: Int,
	val firstname: String,
	val lastname: String,
	val age: Short,
    val gender: String,
	val address: String,
	val employer: String,
	val email: String,
	val city: String,
	val state: String
)