package GoogleAPI;

import Organization.Organization;
import Utl.CheckEmail;
import Utl.ComparisonAlgorithm;
import Utl.StringNormalizer;
import com.google.gson.Gson;
import javafx.util.Pair;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoogleSearchScraper {
    public static Gson gson = new Gson();

    public static void scrap(File JSONFolder) throws IOException, InterruptedException {
//        File listProcessedFile = new File("D:\\VCI\\listProcessedFile.txt");
//        HashSet<String> processedFile = loadProcessedFile(listProcessedFile);
//
//        FileWriter writer = new FileWriter(listProcessedFile, true);
//
//        int counter = 0;
//        for (File JSONFile : JSONFolder.listFiles()) {
//
//            if (JSONFile.getName().endsWith(".json") && ! processedFile.contains(JSONFile.getName())) {    // File wasn't searched
//                writer.write(JSONFile.getName() + '\n');
//                writer.flush();
//
//                String fileContent = new String(Files.readAllBytes(JSONFile.toPath()));
//                Organization organization = gson.fromJson(fileContent, Organization.class);
//                System.out.println(++counter + "    " + organization.getName());
//
//                if (organization.getUrl() == null) {
//                    ArrayList<String> results = search(organization.getName());
//
//                    organization.setUrl(bestResult(results, organization.getName()));
//                    System.out.println("Processed " + JSONFile.getName() + ": " + organization.getName() + "  :  " + organization.getUrl());
//                }
//            }
//        }
//
//        writer.close();

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        for (File JSONFile : JSONFolder.listFiles()) {
            if (JSONFile.getName().endsWith(".json")) {
                executorService.execute(new MultithreadScrapper(JSONFile));
            }
        }

        executorService.shutdown();

//        String query = "Daklak Pedagogical College";
//        ArrayList<Pair<String, String>> results = search(query);
//        System.out.println(bestResult(results, query));
    }

    /**
     * Given a query, search & return an array of results (link, title)
     *
     * @param query
     * @return search results
     */
    public static ArrayList<Pair<String, String>> search(String query) throws IOException, InterruptedException {
        ArrayList<Pair<String, String>> results = new ArrayList<>();    // URL, Title

        if (query == null) {
            return results;
        }
        query.replace(CheckEmail.EMAIL_REGEX, "");

        if (query.length() < 3) {
            return results;
        }

        String fullQuery = AppConfig.GOOGLE_SEARCH_QUERY + URLEncoder.encode(query, "UTF-8");

        Document resultPage = getDocument(fullQuery);
        if (resultPage == null) {
            return null;
        }

        org.jsoup.select.Elements elements = resultPage.getElementsByClass("r");

        for (Element element : elements) {
            Element a = element.select("a").first();
            String url = a.attr("href");

            if (url.startsWith("/url?q=")) {
                url = url.substring(7, url.indexOf("&sa=U&ved="));
                results.add(new Pair<>(normalize(url), a.text()));
            }
        }

        return results;
    }

    /**
     * Given an array of the results (link, title) and the query, returns the most suitable main page website link for that query
     * There are 3 rules used. If one rule fails, switch to the next one.
     *
     * The first rule of Fight Club is: first main page
     *
     * The second rule of Fight Club is: page
     *
     * @param results an array of (link, title)
     * @param query
     * @return the most suitable link for that query
     */
    private static String bestResult(ArrayList<Pair<String, String>> results, String query) throws InterruptedException, IOException {
        if (results == null || results.size() == 0) {
            return null;
        }

        cleanResults(results);
        ArrayList<URL> resultsUrl = new ArrayList<>(results.size());
        for (Pair<String, String> pair : results) {
            resultsUrl.add(new URL(pair.getKey()));
        }

        // First rule: main page
        for (URL url : resultsUrl) {
            if (url.getPath().length() == 0 || url.getPath().length() == 1) {
                return url.toString();
            }
        }

        // Second rule: count the occurences of the website
        HashMap<String, Integer> counter = new HashMap<>();
        for (URL url : resultsUrl) {
            String host = url.getHost();

            if (counter.containsKey(host)) {
                counter.replace(host, counter.get(host) + 1);
            } else {
                counter.put(host, 0);
            }
        }

        int max = 0;
        String bestHost = "";
        for (Map.Entry<String, Integer> entry : counter.entrySet()) {
            if (max < entry.getValue()) {
                max = entry.getValue();
                bestHost = entry.getKey();
            }
        }

        if (bestHost.length() > 0) {
            return (new URL(normalize(bestHost))).toString();
        }

        // Third rule: similarity (Jaccard index) between the title and the query
        String bestUrl = "";
        double minJaccard = Double.MAX_VALUE;

        for (Pair<String, String> pair : results) {
            double jaccard = ComparisonAlgorithm.JaccardIndexStandard(pair.getValue(), query);

            if (minJaccard > jaccard) {
                minJaccard = jaccard;
                bestUrl = pair.getKey();
            }
        }
        bestHost = (new URL(bestUrl)).getHost();

        return (new URL(normalize(bestHost))).toString();
    }

    private static String[] ignore = {"facebook", "wiki", "vcgate", "youtube"};

    /**
     * Remove results whose links have some irrelevant keyword
     * List of that keywords is ignoreList
     *
     * @param results the array of results to be cleaned
     */
    private static void cleanResults(ArrayList<Pair<String, String>> results) {
        for (int i = 0; i < results.size(); ++i) {
            String url = results.get(i).getKey();

            for (String string : ignore) {
                if (url.contains(string)) {
                    results.remove(i);
                    --i;
                    break;
                }
            }
        }
    }

    /**
     * Given a link, returns the HTML document of the link
     *
     */
    private static Document getDocument(String link) throws IOException, InterruptedException {
        return getDocument(new URL(link));
    }

    /**
     * Given a URL, returns the HTML document of the URL
     *
     */
    private static Document getDocument(URL url) throws IOException, InterruptedException {

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("User-Agent", AppConfig.USER_AGENT);
        connection.connect();

        int failCounter = 0;
        int statusCode = connection.getResponseCode();
        while (statusCode != 200 || failCounter < 4) {    // Banned
            ++failCounter;
            // Reset connection
//            Runtime runtime = Runtime.getRuntime();
//            runtime.exec("rasdial /disconnect");
//
//            Thread.sleep(1234);
//            runtime.exec("rasdial viettel");

            System.out.println("YOLO " + url.toString());
            Toolkit.getDefaultToolkit().beep();
            Thread.sleep(678);

            connection = (HttpsURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", AppConfig.USER_AGENT);
            connection.connect();

            statusCode = connection.getResponseCode();
        }

        if (statusCode != 200) {
            return null;
        }

        InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
        BufferedReader reader = new BufferedReader(inputStreamReader);

        StringBuilder html = new StringBuilder("");
        String temp;
        while ((temp = reader.readLine()) != null) {
            html.append(temp).append('\n');
        }

        return Jsoup.parse(html.toString());
    }

    private static HashSet<String> loadProcessedFile(File listProcessedFile) throws IOException {
        HashSet<String> processed = new HashSet<>();

        BufferedReader reader = new BufferedReader(new FileReader(listProcessedFile));
        String temp;
        while ((temp = reader.readLine()) != null) {
            processed.add(temp);
        }

        return processed;
    }

    /**
     * Add http// and remove www.
     * The output string is guaranteed to have this format:
     * https://nvmnghia.com
     *
     */
    private static String normalize(String url) {
        String normalizedUrl = url;

        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            if (url.startsWith("www.")) {
                normalizedUrl = normalizedUrl.substring(4);
            }

            normalizedUrl = "http://" + normalizedUrl;
        } else {
            if (url.startsWith("http://www.")) {
                normalizedUrl = normalizedUrl.substring(0, 7) + normalizedUrl.substring(11);
            }

            if (url.startsWith("https://www.")) {
                normalizedUrl = normalizedUrl.substring(0, 8) + normalizedUrl.substring(12);
            }
        }

        if (normalizedUrl.endsWith("/")) {
            normalizedUrl.substring(0, normalizedUrl.length() - 1);
        }

        return normalizedUrl;
    }

    /**
     * Given a string, converts it to English
     *
     */
    public static String toEnglish(String query) throws URISyntaxException, IOException {

        URIBuilder builder = new URIBuilder(AppConfig.GOOGLE_TRANSLATE_QUERY + AppConfig.GOOGLE_KEY);
        builder.addParameter("q", query);
        builder.addParameter("target", "en");

        HttpsURLConnection connection = (HttpsURLConnection) builder.build().toURL().openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder stringBuilder = new StringBuilder("");
        String tmp;
        while ((tmp = reader.readLine()) != null) {
            stringBuilder.append(tmp).append('\n');
        }

        GTranslateResponse response = gson.fromJson(stringBuilder.toString(), GTranslateResponse.class);
        return response.getData().getTranslations()[0].getTranlatedText();
    }

    // Inner worker class
    public static class MultithreadScrapper implements Runnable {
        private File JSONFile;

        public MultithreadScrapper(File JSONFile) {
            this.JSONFile = JSONFile;
        }

        @Override
        public void run() {
            try {
                String fileContent = new String(Files.readAllBytes(JSONFile.toPath()));
                Organization organization = gson.fromJson(fileContent, Organization.class);

                if (organization.getUrl() == null) {
                    String query = organization.getName();
                    ArrayList<Pair<String, String>> results = search(query);

                    organization.setUrl(bestResult(results, query));

                    // No URL was selected. Translate the query to English and search for it again
                    if (organization.getUrl() == null) {
                        query = toEnglish(query);

                        results = search(query);
                        organization.setUrl(bestResult(results, query));    // Null here? Booo
                    }

                    System.out.println("Processed " + JSONFile.getName() + ": " + organization.getName() + "  :  " + organization.getUrl());
                    if (organization.getUrl() != null && !(organization.getUrl().startsWith("http://") || organization.getUrl().startsWith("https://"))) {
                        System.out.println("!!!!!!!!!!!!!!!!!!!");
                    }

                    BufferedWriter writer = new BufferedWriter(new FileWriter(JSONFile));
                    writer.write(gson.toJson(organization));
                    writer.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
