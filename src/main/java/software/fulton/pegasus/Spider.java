package software.fulton.pegasus;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Spider {

    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private final ArrayList<String> beenTo = new ArrayList<String>();
    private final ArrayList<IndexedDb> indexedDbs = new ArrayList<>();


    /**
     * This performs all the work. It makes an HTTP request, checks the response, and then gathers
     * up all the links on the page. Perform a searchForWord after the successful crawl
     *
     * @param url - The URL to visit
     * @return whether or not the crawl was successful
     */
    public boolean crawl(String url) {
        if (beenTo.contains(url)){
            return false;
        }
        beenTo.add(url);
        try{

            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            if (connection.response().statusCode() == 200) // 200 is the HTTP OK status code
            // indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            indexedDbs.add(new IndexedDb(htmlDocument.title(), "", url));
            for (Element link : linksOnPage) {
                String href = link.absUrl("href");
                System.out.println(href);
                    if (href.startsWith("https://gateway.ipfs.io/ipns/") || href.startsWith("https://ipfs.io/ipfs/") ) {
                        new Thread(() -> {
                            System.out.println(href);
                            crawl(href);
                        }).start();

                    }


            }
            return true;
        } catch (IOException ioe) {
            // We were not successful in our HTTP request
            return false;
        }
    }

}
