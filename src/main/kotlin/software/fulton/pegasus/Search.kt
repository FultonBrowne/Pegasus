package software.fulton.pegasus

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import io.ipfs.api.IPFS
import io.ipfs.multihash.Multihash
import java.io.InputStreamReader
import java.util.*
import kotlin.Comparator


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
            searchWords.forEach { it1: String? ->
                if (it1?.let { it2 -> message.name.contains(it2,true) }!!) weight = weight + 10
                if (message.description.contains(it1, true )) weight = weight +  7
                if (message.link.contains(it1, true)) weight = weight + 2
            }
            if(weight != 0)searchData.add(SearchData(message.name, message.description, message.link, weight))
        }
        reader.endArray()
        reader.close()
        searchData.sortWith(Comparator { o1, o2 -> o1.weight.compareTo(o2.weight) })
        searchData.reverse()

        return Gson().toJson(searchData)
    }

}