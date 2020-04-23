package software.fulton.pegasus

import edu.uci.ics.crawler4j.crawler.CrawlConfig
import edu.uci.ics.crawler4j.crawler.CrawlController
import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer


object Run {
    @JvmStatic
    fun main(args: Array<String>){
        println("go")
        val numberOfCrawlers = 7

        val config = CrawlConfig()
        // Instantiate the controller for this crawl.

        // Instantiate the controller for this crawl.
        val pageFetcher = PageFetcher(config)
        config.crawlStorageFolder = "/home/fulton/crawl"
        val robotstxtConfig = RobotstxtConfig()
        val robotstxtServer = RobotstxtServer(robotstxtConfig, pageFetcher)
        val controller = CrawlController(config, pageFetcher, robotstxtServer)
        controller.addSeed("fulton.eth")
        val factory: WebCrawlerFactory<Spider> = WebCrawlerFactory { Spider() }
        controller.start(factory, numberOfCrawlers);

    }
}