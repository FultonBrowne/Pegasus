package software.fulton.pegasus

class Search() {
    fun searchForResult(string: String, indexedDb:ArrayList<IndexedDb>){
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