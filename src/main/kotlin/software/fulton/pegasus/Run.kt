package software.fulton.pegasus

import io.ipfs.api.IPFS
import io.ipfs.api.NamedStreamable
import java.util.*


object Run {
    @JvmStatic
    fun main(args: Array<String>){
        println("go")

        // Instantiate the controller for this crawl.

        // Instantiate the controller for this crawl.
        val spider = Spider()
        spider.crawl("https://gateway.ipfs.io/ipns/awesome.ipfs.io/")
        val ipfs = IPFS("/ip4/209.94.90.1/tcp/5001")
        while (true) {
            val scanner = Scanner(System.`in`)
            val nextLine = scanner.nextLine()
            if (nextLine.equals("x")) return
            val search = Search()
            search.searchForResult(nextLine, "")
        }


    }
}