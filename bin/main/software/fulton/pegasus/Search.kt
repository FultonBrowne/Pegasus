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
import java.net.URL


class Search() {
    fun searchForResult(string: String, fileName:String): String? {
        val fromBase58 = Multihash.fromBase58(fileName)
        val catStream = URL("https://ipfs.io/ipfs/$fromBase58").openConnection().getInputStream()
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

}