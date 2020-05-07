package software.fulton.pegasus

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import io.ipfs.api.IPFS
import io.ipfs.multihash.Multihash
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.search.FuzzyQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import java.io.InputStreamReader


class Search() {
    private val ipfs = IPFS("/ip4/127.0.0.1/tcp/5001")
    fun searchForResult(string: String, fileName:String): String? {
        val fromBase58 = Multihash.fromBase58(fileName)
        val catStream = ipfs.catStream(fromBase58)
        val searchData = arrayListOf<SearchData>()
        val searchWords = Utils().parseWord(string)
        val gson = Gson()
        val reader = JsonReader(InputStreamReader(catStream, "UTF-8"))
        reader.beginArray()
        while (reader.hasNext()) {
            var weight = 0
            val message:IndexedDb = gson.fromJson(reader, IndexedDb::class.java)
            val name = " ${message.name} ";
            val description = " ${message.description} "
            val link = message.link
            searchWords.forEach { it1: String ->
                val it2 = " $it1"
                println(it2)
                println(name)
                if ( name.contains(it2,true)) weight = weight + 10
                if (description.contains(it2, true )) weight = weight +  7
                if (link.contains(it2, true)) weight = weight + 2
            }
            if(weight != 0)searchData.add(SearchData(name, description, link, weight))
        }
        reader.endArray()
        reader.close()
        searchData.sortWith(Comparator { o1, o2 -> o1.weight.compareTo(o2.weight) })
        searchData.reverse()

        return Gson().toJson(searchData)
    }
    fun searchv2(string: String, fileName:String): String? {
        val fromBase58 = Multihash.fromBase58(fileName)
        val catStream = ipfs.catStream(fromBase58)
        val searchData = arrayListOf<SearchData>()
        val searchWords = Utils().parseWord(string)
        val gson = Gson()
        val reader = JsonReader(InputStreamReader(catStream, "UTF-8"))
        reader.beginArray()
        val memoryIndex: Directory = RAMDirectory()
        val analyzer = StandardAnalyzer()
        val indexWriterConfig = IndexWriterConfig(analyzer)
        val writter = IndexWriter(memoryIndex, indexWriterConfig)
        while (reader.hasNext()) {
            var weight = 0
            val message:IndexedDb = gson.fromJson(reader, IndexedDb::class.java)
            val name = " ${message.name} ";
            val description = " ${message.description} "
            val link = message.link
            val document = Document()
            document.add(TextField("title", name, Field.Store.YES))
            document.add(TextField("description", description, Field.Store.YES))
            document.add(TextField("link", link, Field.Store.YES))
            writter.addDocument(document)

        }
        writter.close()
        val indexReader = DirectoryReader.open(memoryIndex);
        val indexSearcher = IndexSearcher(indexReader)
        val search = indexSearcher.search(FuzzyQuery(Term("description", string)), 10)
        search.scoreDocs.forEach {
            val doc = indexSearcher.doc(it.doc)
            searchData.add(SearchData(doc["title"], doc["description"], doc["link"], 0))
        }
        reader.endArray()
        reader.close()
        return gson.toJson(searchData)
    }

}