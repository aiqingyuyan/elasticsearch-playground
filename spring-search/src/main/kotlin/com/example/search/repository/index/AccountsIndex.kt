package com.example.search.repository.index

import com.example.search.repository.factory.IndexFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.apache.lucene.index.Term
import org.apache.lucene.search.FuzzyQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.RAMDirectory
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import javax.annotation.PostConstruct

@Repository
class AccountsIndex(
	private val indexFactory: IndexFactory
) {
	private val logger = LoggerFactory.getLogger(AccountsIndex::class.java)

	private lateinit var index: RAMDirectory
	private lateinit var searcher: IndexSearcher

	@ExperimentalCoroutinesApi
	@InternalCoroutinesApi
	@PostConstruct
	fun init() {
		val (index, searcher) = indexFactory.createIndexFromResource("classpath:accounts.json")
		this.index = index
		this.searcher = searcher

		logger.info(
			"Number of documents for ${AccountsIndex::class.java.simpleName}: ${searcher.indexReader.numDocs()}"
		)
	}

	fun search(field: String, queryStr: String, numOfDocs: Int): List<Map<String, String>> {
		val term = Term(field, queryStr)
		val query = FuzzyQuery(term)
//		val query = PrefixQuery(term)
		val topDocs = searcher.search(query, numOfDocs)
		val results = mutableListOf<Map<String, String>>()

		logger.info("Found: ${topDocs.scoreDocs.size}")

		topDocs.scoreDocs.forEach {
			val map = mutableMapOf<String, String>()
			val document = searcher.doc(it.doc)
			document.fields.forEach { field ->
				map[field.name()] = field.stringValue()
			}
			results.add(map)
		}

		return results
	}
}
