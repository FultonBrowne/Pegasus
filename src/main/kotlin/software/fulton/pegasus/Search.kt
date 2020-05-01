package software.fulton.pegasus

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import io.ipfs.api.IPFS
import io.ipfs.multihash.Multihash
import java.io.InputStreamReader
import java.lang.System.`in`
import java.util.*


class Search() {
    private val ipfs = IPFS("/ip4/127.0.0.1/tcp/5001")
    init {
        //ipfs.refs.local()
    }
    fun searchForResult(string: String, fileName:String){
        val fromBase58 = Multihash.fromBase58(fileName)
        val catStream = ipfs.catStream(fromBase58)
        val searchData = arrayListOf<SearchData>()
        val searchWords = Utils().parseWord(string)
        val gson = Gson()
        val reader = JsonReader(InputStreamReader(catStream, "UTF-8"))
        reader.beginArray()
        while (reader.hasNext()) {
            val message:IndexedDb = gson.fromJson(reader, IndexedDb::class.java)
            searchWords.forEach { it1: String? ->
                if (it1?.let { it2 -> message.name.contains(it2,true) }!!) println(message.name)
            }
        }
        reader.endArray()
        reader.close()
    }

}