package com.example.search.repository.index

import com.example.search.model.Account
import com.example.search.repository.dispatcher_context.workersContext
import com.example.search.repository.resource.AccountsResource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.RAMDirectory
import org.springframework.stereotype.Repository
import javax.annotation.PostConstruct

@Repository
class AccountsIndex(
	private val accountsResource: AccountsResource
) {
	private lateinit var analyzer: StandardAnalyzer
	private lateinit var writerConfig: IndexWriterConfig
	private lateinit var indexWriter: IndexWriter
	private lateinit var index: RAMDirectory
	private lateinit var indexReader: DirectoryReader
	private lateinit var searcher: IndexSearcher

	init {
		analyzer = StandardAnalyzer()
		writerConfig = IndexWriterConfig(analyzer)
		index = RAMDirectory()
		indexWriter = IndexWriter(index, writerConfig)
	}

	fun search(field: String, queryStr: String): List<Document> {
		val query = QueryParser(field, analyzer).parse(queryStr)
		val topDocs = searcher.search(query, 5)
		val documents = mutableListOf<Document>()

		println("Found: ${topDocs.scoreDocs.size}")

		topDocs.scoreDocs.forEach {
			documents.add(searcher.doc(it.doc))
		}

		return documents
	}

	@ObsoleteCoroutinesApi
	@PostConstruct
	@InternalCoroutinesApi
	fun createIndex() = runBlocking {
		val job = launch(workersContext) {
			accountsResource
				.produceAccounts()
				.collect { account ->
					val doc = createDocument(account)
					indexWriter.addDocument(doc)
				}
		}

		job.join()
		indexWriter.close()
		indexReader = DirectoryReader.open(index)
		searcher = IndexSearcher(indexReader)

		println("Index: ${index.ramBytesUsed()}, num of docs: ${indexReader.numDocs()}")
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
