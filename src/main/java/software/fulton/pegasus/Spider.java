package software.fulton.pegasus;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Spider {

    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private final ArrayList<String> beenTo = new ArrayList<String>();
    Gson gson = new Gson();
    JsonWriter writer;
    public int limit;
    final ArrayList<IndexedDb> indexedDbs = new ArrayList<>();
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");

    public Spider() throws IOException {
        String url = ""; //TODO add the url
        URL UrlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) UrlObj.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        writer = new JsonWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setDoOutput(true);
        writer.beginArray();

    }


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
        if(limit <= beenTo.size()) return false;
        ArrayList<Thread> threads = new ArrayList<>();
        beenTo.add(url);
        try{
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            if (connection.response().statusCode() == 200) // 200 is the HTTP OK status code
            // indicating that everything is great.
            {

            }
            if (!connection.response().contentType().contains("text/html")) {
                return false;
            }
            Elements linksOnPage = htmlDocument.select("a[href]");
            gson.toJson(new IndexedDb(htmlDocument.title(), "", url), IndexedDb.class, writer);
            for (Element link : linksOnPage) {
                String href = link.absUrl("href");
                if(!FILTERS.matcher(href).matches()){
                    if (href.startsWith("https://gateway.ipfs.io/ipns/") || href.startsWith("https://ipfs.io/ipfs/") ) {
                        Thread thread = new Thread(() -> {
                            crawl(href);
                        });
                        threads.add(thread);
                        thread.start();
                    }


                    }


            }
            return true;
        } catch (IOException ioe) {
            // We were not successful in our HTTP request
            return false;
        }
    }

}
