package Segmenter;

import vn.edu.vnu.uet.nlp.segmenter.UETSegmenter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Test {
    private static ArrayList<String> vnDictArray = new ArrayList<>();
    private static ArrayList<String> foreignDictArray = new ArrayList<>();

    private static HashSet<String> vnDictHash = new HashSet<>();
    private static HashSet<String> foreignDictHash = new HashSet<>();

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        File JSONFolder = new File("D:\\VCI\\organization_json");
        UETSegmenter segmenter = new UETSegmenter("D:\\VCI\\UETsegmenter-master\\models");

        BufferedReader reader;
        String tempLine;

        File vnDictFile = new File("D:\\VCI\\VCI-OrganizationNormalizer\\vn_org_dict.txt");
        File foreignDictFile = new File("D:\\VCI\\VCI-OrganizationNormalizer\\foreign_org_dict.txt");

        reader = new BufferedReader(new FileReader(vnDictFile));
        while ((tempLine = reader.readLine()) != null) {
//            vnDictArray.add(tempLine);
            vnDictHash.add(tempLine);
        }

        reader = new BufferedReader(new FileReader(foreignDictFile));
        while ((tempLine = reader.readLine()) != null) {
//            foreignDictArray.add(tempLine);
            foreignDictHash.add(tempLine);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);
        for (File JSONFile : JSONFolder.listFiles()) {
//            executorService.execute(new LoopClassifierTest(JSONFile, segmenter, vnDictArray, foreignDictArray));
        }
        executorService.shutdown();

        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        System.out.println("Result: " + (end - start));
    }
}
