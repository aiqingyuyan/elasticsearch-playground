package com.example.search.repository.index

import com.example.search.configuration.DispatcherContext
import com.example.search.model.Account
import com.example.search.repository.resource.AccountsResource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.FuzzyQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.PrefixQuery
import org.apache.lucene.store.RAMDirectory
import org.springframework.stereotype.Repository
import javax.annotation.PostConstruct

@Repository
class AccountsIndex(
	private val accountsResource: AccountsResource,
	private val dispatcherContext: DispatcherContext
) {
	private val analyzer = StandardAnalyzer()
	private val index: RAMDirectory = RAMDirectory()

	private lateinit var writerConfig: IndexWriterConfig
	private lateinit var indexWriter: IndexWriter
	private lateinit var indexReader: DirectoryReader
	private lateinit var searcher: IndexSearcher

	fun search(field: String, queryStr: String, numOfDocs: Int): List<Map<String, String>> {
		val term = Term(field, queryStr)
		val query = FuzzyQuery(term)
//		val query = PrefixQuery(term)
//		val query = QueryParser(field, analyzer).parse(queryStr)
		val topDocs = searcher.search(query, numOfDocs)
		val results = mutableListOf<Map<String, String>>()

		println("Found: ${topDocs.scoreDocs.size}")

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

	@ExperimentalCoroutinesApi
	@ObsoleteCoroutinesApi
	@PostConstruct
	@InternalCoroutinesApi
	fun createIndex() = runBlocking {
		withContext(Dispatchers.IO) {
			writerConfig = IndexWriterConfig(analyzer)
			indexWriter = IndexWriter(index, writerConfig)
		}

		val job = launch(dispatcherContext.workersContext) {
		  accountsResource
			.produceAccounts()
			.collect { account ->
			  withContext(Dispatchers.IO) {
					val doc = createDocument(account)
					indexWriter.addDocument(doc)
			  }
			}
		}

		job.join()

		withContext(Dispatchers.IO) {
		  indexWriter.close()
		  indexReader = DirectoryReader.open(index)
		  searcher = IndexSearcher(indexReader)
		  println("finished building searcher")
		}

		println("Index storage used: ${index.ramBytesUsed()}, num of docs: ${indexReader.numDocs()}")
	}

	private fun createDocument(account: Account): Document {
		val doc = Document()

		doc.add(StringField(
			"account_number",
			account.account_number.toString(),
			Field.Store.YES
		))
		doc.add(TextField(
			"firstname",
			account.firstname,
			Field.Store.YES
		))
		doc.add(TextField(
			"lastname",
			account.lastname,
			Field.Store.YES
		))
		doc.add(TextField(
			"address",
			account.address,
			Field.Store.YES
		))

		return doc
	}
}
