package software.fulton.pegasus

import com.google.gson.JsonParser
import com.mashape.unirest.http.Unirest
import java.util.*


object Run {
    var hash = ""
    @JvmStatic
    fun main(args: Array<String>){
        println("go")

        // Instantiate the controller for this crawl.

        // Instantiate the controller for this crawl.
        val spider = Spider()
        spider.limit = 10
        spider.crawl("https://gateway.ipfs.io/ipns/awesome.ipfs.io/")
        spider.writer.endArray()
        spider.writer.close()
        spider.outputStream.close()
        Unirest.setTimeouts(0, 0)
        val response =
            Unirest.post("http://127.0.0.1:5001/api/v0/add?chunker=size-262144&hash=sha2-256&inline-limit=32")
                .field("the-internet-as-of${System.currentTimeMillis()}",spider.temp)
                .asString()
        println(response.body)
        hash = JsonParser.parseString(response.body).asJsonObject.get("Hash").asString
        //val ipfs = IPFS("/ip4/209.94.90.1/tcp/5001")
        while (true) {
            val scanner = Scanner(System.`in`)
            val nextLine = scanner.nextLine()
            if (nextLine.equals("x")) return
            val search = Search()
            search.searchForResult(nextLine, "")
        }


    }
}