package BingAPI;

import GoogleAPI.AppConfig;
import com.google.gson.Gson;
import org.apache.http.client.utils.URIBuilder;

import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class BingSearchCrawler {
    /**
     * Get the result of a query
     *
     * @param query to be queried
     * @return the array of BingResult
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public static BingResult[] getBingResult(String query) throws URISyntaxException, IOException {
        // Build the URL & open the connection
        URIBuilder builder = new URIBuilder(AppConfig.BING_QUERY);
        builder.addParameter("q", query);

        HttpURLConnection con = (HttpURLConnection) builder.build().toURL().openConnection();
        con.setRequestProperty(AppConfig.BING_KEY_HEADER, AppConfig.BING_KEY);    // Key header

        // Read the response out
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String tmpLine;
        StringBuilder responseStringBuilder = new StringBuilder("");

        while ((tmpLine = reader.readLine()) != null) {
            responseStringBuilder.append(tmpLine).append('\n');
        }
        reader.close();

        // Create BingResponse and return its BingResult[]
        Gson gson = new Gson();
        BingResponse response = gson.fromJson(responseStringBuilder.toString(), BingResponse.class);
        return response.getWebPages().value;
    }

    /**
     * Work just like the other getBingResult function, but this function save the response to file
     *
     * @param query
     * @param outputFile
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static BingResult[] getBingResult(String query, File outputFile) throws URISyntaxException, IOException {
        BingResponse response = new BingResponse();

        response.setQuery(query);
        response.setWebPages(new BingResult.BingResultList());
        response.getWebPages().value = getBingResult(query);

        Gson gson = new Gson();
        String outputContent = gson.toJson(response);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(outputContent);
        writer.close();

        return response.getWebPages().value;
    }
}
