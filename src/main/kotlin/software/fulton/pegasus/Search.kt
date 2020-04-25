package software.fulton.pegasus

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.InputStreamReader
import java.lang.System.`in`
import java.util.*


class Search() {
    fun searchForResult(string: String, indexedDb:ArrayList<IndexedDb>){
        val searchData = arrayListOf<SearchData>()
        val searchWords = Utils().parseWord(string)
        searchWords.forEach {
            println(it)
        }
        val gson = Gson()
        val reader = JsonReader(InputStreamReader(`in`, "UTF-8"))
        reader.beginArray()
        while (reader.hasNext()) {
            val message:IndexedDb = gson.fromJson(reader, IndexedDb::class.java)
            searchWords.forEach { it1: String? ->
                if (it1?.let { it2 -> message.name.contains(it2) }!!) println(message.name)
            }
        }
        reader.endArray()
        reader.close()
        indexedDb.forEach {

        }
    }

}