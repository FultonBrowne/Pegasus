package software.fulton.pegasus

import java.util.*


object Run {
    @JvmStatic
    fun main(args: Array<String>){
        println("go")

        // Instantiate the controller for this crawl.

        // Instantiate the controller for this crawl.
        val spider = Spider()
        spider.crawl("https://gateway.ipfs.io/ipns/awesome.ipfs.io/")
        while (true) {
            val scanner = Scanner(System.`in`)
            val nextLine = scanner.nextLine()
            if (nextLine.equals("x")) return
            val search = Search(spider.indexedDbs)
            search.searchForResult(nextLine)
        }


    }
}