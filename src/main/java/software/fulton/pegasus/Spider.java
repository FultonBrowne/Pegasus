package software.fulton.pegasus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.ipfs.multihash.Multihash;
import java.io.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Spider {

    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    OutputStream outputStream;
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private final ArrayList<String> beenTo = new ArrayList<>();
    Gson gson = new Gson();
    JsonWriter writer;
    File temp;
    public int limit;
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");

    public Spider() throws IOException {
        createTempDirectory();
        writer = new JsonWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.setIndent("  ");
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
        if(limit <= beenTo.size()) {
            return true;
        }
        if (beenTo.contains(url)){
            return false;
        }
        System.out.println(beenTo.size());
        beenTo.add(url);
        try{
            Elements linksOnPage = getLinks(url);
            for (Element link : linksOnPage) {
                if(limit <= beenTo.size()) {
                    return true;}
                String href = link.absUrl("href");
                if(!FILTERS.matcher(href).matches() && !href.contains("QmdA5WkDNALetBn4iFeSepHjdLGJdxPBwZyY47ir1bZGAK") && !href.contains("QmNoscE3kNc83dM5rZNUC5UDXChiTdDcgf16RVtFCRWYuU") && !href.contains("QmbsZEvJE8EU51HCUHQg2aem9JNFmFHdva3tGVYutdCXHp")){
                    if (href.startsWith("https://gateway.ipfs.io/ipns/") || href.startsWith("https://ipfs.io/ipfs/") ) {
                        Thread thread = new Thread(() -> crawl(href));
                        thread.start();
                        thread.join();
                    }
                }
            }
            return true;
        } catch (Exception ioe) {
            // We were not successful in our HTTP request
            return false;
        }
    }
    public void createTempDirectory()
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
        outputStream = new BufferedOutputStream(new FileOutputStream(temp));


    }
    public Elements getLinks(String url) throws IOException{
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT).timeout(2000).maxBodySize(900000);
            connection.execute();
            System.out.println(connection.response().contentType());
            if(!connection.response().contentType().contains("text/html")){
                return null;
            }
            Document htmlDocument = connection.get();
            Elements linksOnPage = htmlDocument.select("a[href]");
            try{
                String inputString = htmlDocument.text();
                int maxLength = Math.min(inputString.length(), 80);
                gson.toJson(new IndexedDb(htmlDocument.title(), inputString.substring(0, maxLength), url), IndexedDb.class, writer);}
            catch (Exception e){e.printStackTrace();}
        return linksOnPage;
            }

}
