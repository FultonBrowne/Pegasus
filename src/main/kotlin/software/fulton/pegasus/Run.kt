package software.fulton.pegasus

import com.google.gson.JsonParser
import com.mashape.unirest.http.Unirest
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import fi.iki.elonen.NanoHTTPD
import java.io.IOException
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.TimeUnit


object Run {
    private var hash = ""
    @JvmStatic
    fun main(args: Array<String>){
        println("go")

        // Instantiate the controller for this crawl.

        // Instantiate the controller for this crawl.
        val spider = Spider()
        spider.limit = 100
        spider.crawl("https://gateway.ipfs.io/ipns/awesome.ipfs.io/")
        while (!spider.executorService.isShutdown);
        spider.executorService.awaitTermination(60, TimeUnit.SECONDS)
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
        println(hash)
        val http = Http()
        http.start()
        println(http.isAlive)
        while (true) {
            val scanner = Scanner(System.`in`)
            val nextLine = scanner.nextLine()
            if (nextLine == "x") return
            val search = Search()
            println(search.searchForResult(nextLine, hash))
        }



    }
    internal  class Http:NanoHTTPD(8000){
        override fun serve(session: IHTTPSession?): Response {
            super.serve(session)
            val search = Search()
            val response = session?.uri?.replace("/search/","")?.let { search.searchForResult(it, hash) }!!
            return newFixedLengthResponse(response)

        }
    }
}