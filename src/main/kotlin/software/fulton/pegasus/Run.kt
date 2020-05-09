package software.fulton.pegasus

import com.google.gson.JsonParser
import com.mashape.unirest.http.Unirest
import fi.iki.elonen.NanoHTTPD
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
        spider.limit = 20000
        spider.crawl("https://gateway.ipfs.io/ipns/awesome.ipfs.io/")
        while (!spider.executorService.isShutdown);
        spider.executorService.awaitTermination(60, TimeUnit.SECONDS)
        spider.writer.endArray()
        spider.writer.close()
        spider.outputStream.close()
        Unirest.setTimeouts(0, 0)
        val response =
            Unirest.post("http://ipfs:5001/api/v0/add?chunker=size-262144&hash=sha2-256&inline-limit=32")
                .field("the-internet-as-of${System.currentTimeMillis()}",spider.temp)
                .asString()
        println(response.body)
        hash = JsonParser.parseString(response.body).asJsonObject.get("Hash").asString
        println(hash)
        val http = Http()
        http.start()
        println(http.isAlive)
        val timer =  object : TimerTask() {
            override fun run() {
                spider.crawl("https://gateway.ipfs.io/ipns/awesome.ipfs.io/")
                while (!spider.executorService.isShutdown);
                spider.executorService.awaitTermination(60, TimeUnit.SECONDS)
                spider.writer.endArray()
                spider.writer.close()
                spider.outputStream.close()
                Unirest.setTimeouts(0, 0)
                val response =
                    Unirest.post("http://ipfs:5001/api/v0/add?chunker=size-262144&hash=sha2-256&inline-limit=32")
                        .field("the-internet-as-of${System.currentTimeMillis()}",spider.temp)
                        .asString()
                println(response.body)
                hash = JsonParser.parseString(response.body).asJsonObject.get("Hash").asString
            }
        }
        Timer().schedule(timer,1000* 60 * 60 *24, 1000* 60 * 60 *24)
        while (http.isAlive);
    }
    internal  class Http:NanoHTTPD(8000){
        override fun serve(session: IHTTPSession?): Response {
            super.serve(session)
            val search = Search()
            println(session?.headers)
            val response = session?.uri?.replace("/search/","")?.let { search.searchForResult(it, hash) }!!
            val newFixedLengthResponse = newFixedLengthResponse(response)
            newFixedLengthResponse.addHeader("Access-Control-Allow-Origin", session.headers["origin"])
            newFixedLengthResponse.addHeader("Access-Control-Allow-Credentials", "true")
            newFixedLengthResponse.addHeader("Access-Control-Allow-Headers", " Origin, Content-Type, X-Auth-Token")
            newFixedLengthResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS")
            return newFixedLengthResponse

        }
    }
}