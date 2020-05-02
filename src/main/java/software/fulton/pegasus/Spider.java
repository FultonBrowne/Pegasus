package software.fulton.pegasus;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import okhttp3.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Spider {

    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    OutputStream outputStream;
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private final ArrayList<String> beenTo = new ArrayList<String>();
    Gson gson = new Gson();
    JsonWriter writer;
   static ExecutorService executorService = Executors.newFixedThreadPool(10);
    File temp;
    public int limit;
    final ArrayList<IndexedDb> indexedDbs = new ArrayList<>();
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");

    public Spider() throws IOException, UnirestException {
        createTempDirectory();

        writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));

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
        System.out.println(beenTo.size());
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
                if(limit <= beenTo.size()) break;
                String href = link.absUrl("href");
                if(!FILTERS.matcher(href).matches()){
                    if (href.startsWith("https://gateway.ipfs.io/ipns/") || href.startsWith("https://ipfs.io/ipfs/") ) {
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    crawl(href);
                                }
                            });
                    }
                    }
            }
            return true;
        } catch (IOException ioe) {
            // We were not successful in our HTTP request
            return false;
        }
    }
    public File createTempDirectory()
            throws IOException
    {

        temp = File.createTempFile("temp", "file");


        if(!(temp.delete()))
        {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if(!(temp.createNewFile()))
        {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }
        System.out.println(temp.toURI());
        outputStream = new FileOutputStream(temp);


        return (temp);
    }

}
