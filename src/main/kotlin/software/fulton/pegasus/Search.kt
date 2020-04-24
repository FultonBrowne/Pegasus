package software.fulton.pegasus

class Search(private var indexedDb: ArrayList<IndexedDb>) {
    fun searchForResult(string: String){
        val searchData = arrayListOf<SearchData>()
        val searchWords = Utils().parseWord(string)
        searchWords.forEach {
            println(it)
        }
        indexedDb.forEach {
            searchWords.forEach { it1: String? ->
                if (it1?.let { it2 -> it.name.contains(it2) }!!) println(it.name)
            }
        }
    }

}