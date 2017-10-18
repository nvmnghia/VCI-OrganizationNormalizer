package GoogleAPI;

import Organization.Organization;
import Utl.ArrayUtl;
import Utl.ComparisonAlgorithm;
import Utl.StringNormalizer;
import com.google.gson.Gson;
import org.apache.http.client.utils.URIBuilder;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Utl.ComparisonAlgorithm.JaccardIndexQuery;

public class GoogleMapCrawler {
    /**
     *
     *
     * @param args
     * @throws URISyntaxException
     */
    public static void main(String[] args) throws URISyntaxException {
        File JSONFolder = new File("D:\\VCI\\organization_json");
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (File JSONFile : JSONFolder.listFiles()) {
            String name = JSONFile.getName();
            if (name.endsWith(".json") && ! name.endsWith("_response.json")) {
                executorService.execute(new CrawlerWorker(JSONFile));
            }
        }
        executorService.shutdown();
    }

    /**
     * Multiple comparison method is used. The procedure is as follow:
     * - Validate query: ambiguous query is ignored (return null)
     *     + Email query
     *     + Standalone organization: Dept without Univ, Colle, Insti
     *     + Name query (to be implemented)
     *
     * - If the query is not ignored: query the result from api
     *     + No result: translate the query into english and perform this checking again
     *     + At least 1 result: check the similarity between the result and the query by these algorithm. Result with highest score is selected.
     *       If one algorithm fail to reach the threshold, use the next algorithm
     *         + LCM: LCM / query
     *         + Jaccard (under consideration)
     *         + Edit distance (under consideration)
     *
     * @param query
     * @param listResults
     * @return
     */
    public static GoogleAPIResult bestResult(String query, GoogleAPIResult[] listResults, ComparisonAlgorithm.ALGORITHM algorithm) {
        String normQuery = StringNormalizer.normalize(query);
        int idxBestResult;

        switch (algorithm) {
            case JACCARD_QUERY:
                float[] jaccardQuerySimilarity = new float[listResults.length];

                for (int i = 0; i < listResults.length; ++i) {
                    String normResult = StringNormalizer.normalize(listResults[i].getName());
                    jaccardQuerySimilarity[i] = JaccardIndexQuery(normQuery, normResult);
                }

                idxBestResult = ArrayUtl.idxMax(jaccardQuerySimilarity);
                return idxBestResult == -1 ? null : listResults[idxBestResult];

            case JACCARD_STANDARD:
                double[] jaccardStandardSimilarity = new double[listResults.length];

                for (int i = 0; i < listResults.length; ++i) {
                    String normResult = StringNormalizer.normalize(listResults[i].getName());
                    jaccardStandardSimilarity[i] = JaccardIndexQuery(normQuery, normResult);
                }

                idxBestResult = ArrayUtl.idxMax(jaccardStandardSimilarity);
                return idxBestResult == -1 ? null : listResults[idxBestResult];
        }

        return null;
    }

    private static class CrawlerWorker implements Runnable {
        private File JSONFile;

        public CrawlerWorker(File JSONFile) {
            this.JSONFile = JSONFile;
        }

        @Override
        public void run() {
            try {
                StringBuilder fileOutputContent = new StringBuilder("");
                Gson gson = new Gson();
                String JSONFileContent = new String(Files.readAllBytes(JSONFile.toPath()));
                Organization organization = gson.fromJson(JSONFileContent, Organization.class);
                String query = organization.getName();

                fileOutputContent.append(query).append('\n');

                if ((query.contains("Phòng") || query.contains("Khoa")) && !(query.contains("Trường") || query.contains("Đại học")))
                    return;

                if ((query.contains("Dept") || query.contains("Department")) && !(query.contains("Univ")))
                    return;

                URIBuilder builder = new URIBuilder(AppConfig.GOOGLE_MAP_QUERY);
                builder.addParameter("query", organization.getName());
                builder.addParameter("key", AppConfig.GOOGLE_KEY);

                URLConnection connection = builder.build().toURL().openConnection();
                System.out.println("URL: " + builder.build().toURL().toString());

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine, content;
                StringBuilder stringBuilder = new StringBuilder("");

                while ((inputLine = in.readLine()) != null) {
                    stringBuilder.append(inputLine).append('\n');
                }
                in.close();
                content = stringBuilder.toString();

                GoogleAPIResponse response = gson.fromJson(content, GoogleAPIResponse.class);
                GoogleAPIResult[] results = response.getResults();

                GoogleAPIResult tmpResult1 = bestResult(query, results, ComparisonAlgorithm.ALGORITHM.JACCARD_QUERY);
                fileOutputContent.append("Jaccard Query:    ").append(tmpResult1 == null ? null : tmpResult1.getName()).append('\n');

                GoogleAPIResult tmpResult2 = bestResult(query, results, ComparisonAlgorithm.ALGORITHM.JACCARD_STANDARD);
                fileOutputContent.append("Jaccard Standard: ").append(tmpResult2 == null ? null : tmpResult2.getName()).append('\n');

                if (tmpResult1 != null && tmpResult2 != null && ! tmpResult1.getName().equals(tmpResult2.getName())) {
                    fileOutputContent.append("DIFFERENT");
                }
                fileOutputContent.append('\n');

                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(JSONFile.getPath().replace(".json", "_response.json"))));
                writer.write(fileOutputContent.toString());
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }        }
    }
}
