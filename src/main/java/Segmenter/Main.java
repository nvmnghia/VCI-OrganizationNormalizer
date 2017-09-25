package Segmenter;

import Organization.Organization;
import VCGate.VCGate;
import vn.edu.vnu.uet.nlp.segmenter.UETSegmenter;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static Config.AppConfig.*;

public class Main {

    private static ConcurrentHashMap<String, Integer> tokenizedWords = new ConcurrentHashMap<>();

    private static UETSegmenter segmenter = new UETSegmenter(UETSegmenter_MODELS_PATH);

    private static VCGate vcGate = new VCGate();

    public static void main(String[] args) {

        updateOrganizationsOrigin();

//        createListTokenWords();

    }

    private static void updateOrganizationsOrigin() {
        try {
            Future<?> future;
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            Organization[] organizations = vcGate.getOrganizations(null);
            for(Organization organization: organizations) {
                future = executorService.submit(new SegmenterProcessorOrg(vcGate, organization, segmenter));

                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createListTokenWords() {
        try {
            Future<?> future;
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            Organization[] organizations = vcGate.getOrganizations(2);
            for(Organization organization: organizations) {
                future = executorService.submit(new SegmenterProcessor(organization, tokenizedWords, segmenter));

                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Sort
        List<Map.Entry<String, Integer>> listTokenizedWord = new ArrayList<>(tokenizedWords.entrySet());
        Collections.sort(listTokenizedWord, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue(); //desc order
            }
        });

        //Write list word to file
        try {
            PrintWriter writer = new PrintWriter(ORG_RULE_PATH + "listTokenizedWord.txt", "UTF-8");
            for (Map.Entry<String, Integer> entry : listTokenizedWord) {
                writer.println(entry.getKey() + " - " + entry.getValue());
                //System.out.println(entry.getKey() + "    " + entry.getValue());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
