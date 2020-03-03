package com.example.search.repository.factory

import com.example.search.repository.resource.ResourceProducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.RAMDirectory
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class IndexFactory(
  private val resourceProducer: ResourceProducer
) {
  private val logger = LoggerFactory.getLogger(IndexFactory::class.java)

  @ExperimentalCoroutinesApi
  @InternalCoroutinesApi
  fun createIndexFromResource(resourceLocation: String) = runBlocking {
    withContext(Dispatchers.IO) {
      val memoryIndex = RAMDirectory()

      IndexWriter(memoryIndex, IndexWriterConfig(StandardAnalyzer()))
        .let { writer -> Pair(writer, resourceProducer.produceDataFlowFrom(resourceLocation)) }
        .also { (writer, dataFlow) ->
          dataFlow
            .collect { data ->
              withContext(Dispatchers.IO) {
                val doc = createDocument(data)
                writer.addDocument(doc)
              }
            }
            .also { logger.info("Finished adding documents") }

          writer.close()

          logger.info("Index space used: ${memoryIndex.ramBytesUsed()}")
        }
        .let { Pair(memoryIndex, IndexSearcher(DirectoryReader.open(memoryIndex))) }
    }
  }

  private fun createDocument(data: Map<String, Any>): Document =
    Document()
      .apply {
        data.keys.forEach { key ->
          if (
            data[key] is Short ||
            data[key] is Int ||
            data[key] is Long
          ) {
            this.add(StringField(key, data[key].toString(), Field.Store.YES))
          } else {
            this.add(TextField(key, data[key] as String, Field.Store.YES))
          }
        }
      }
}
