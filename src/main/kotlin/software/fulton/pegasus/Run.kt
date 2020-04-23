package software.fulton.pegasus


object Run {
    @JvmStatic
    fun main(args: Array<String>){
        println("go")

        // Instantiate the controller for this crawl.

        // Instantiate the controller for this crawl.
        val spider = Spider()
        spider.crawl("https://gateway.ipfs.io/ipns/awesome.ipfs.io/")
        println("done")
        println(spider.indexedDbs)

    }
}