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
            val name = " ${message.name} ";
            val description = " ${message.description} "
            val link = message.link
            searchWords.forEach { it1: String ->
                val it2 = " $it1 "
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