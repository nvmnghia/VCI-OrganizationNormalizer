package Segmenter;

import vn.edu.vnu.uet.nlp.segmenter.UETSegmenter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {
    private static ConcurrentHashMap<String, Integer> tokenizedWords = new ConcurrentHashMap<>();

    private static UETSegmenter segmenter = new UETSegmenter("D:\\VCI\\UETsegmenter-master\\models");

    public static void main(String[] args) {
        File JSONFolder = new File("D:\\VCI\\organization_json");

        Collection<Future<?>> tasks = new LinkedList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Process JSON
        Future<?> future;
        for (File JSONFile : JSONFolder.listFiles()) {
            if (JSONFile.getName().endsWith(".json")) {
                future = executorService.submit(new SegmenterProcessor(JSONFile, tokenizedWords, segmenter));

                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        executorService.shutdown();

        // Sort
        List<Map.Entry<String, Integer>> listTokenizedWord = new ArrayList<>(tokenizedWords.entrySet());
        Collections.sort(listTokenizedWord, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue() - o2.getValue();
            }
        });

        // Display
        for (Map.Entry<String, Integer> entry : listTokenizedWord) {
            System.out.println(entry.getKey() + "    " + entry.getValue());
        }
        System.out.println(listTokenizedWord.size());
    }


}
