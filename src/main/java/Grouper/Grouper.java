package Grouper;

import Organization.Organization;
import Utl.StringNormalizer;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import javafx.util.Pair;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class Grouper {
    private static Gson gson = new Gson();
    private static ArrayList<Pair<String, String>> mapper = new ArrayList<>();
    private static HashSet<String> URLSet = new HashSet<>();

    public static void group(File JSONFolder, File output) throws IOException, ExecutionException, InterruptedException {

//        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//
//        Future<?> future;
//        for (File JSONFile : JSONFolder.listFiles()) {
//            if (JSONFile.getName().endsWith(".json")) {
//                future = executorService.submit(new Worker(JSONFile));
//                future.get();
//            }
//        }
//        executorService.shutdown();
//
//        StringBuilder builder = new StringBuilder("");
//        for (String value : mapper.values()) {
//            builder.append(value).append(":\n");
//
//            for (Map.Entry<String, String> entry : mapper.entrySet()) {
//                if (entry.getValue().equals(value)) {
//                    builder.append(entry.getKey()).append('\n');
//                }
//            }
//            builder.append('\n');
//        }
//        builder.append(mapper.entrySet().size()).append(" entries in ").append(mapper.values().size()).append(" groups");
//
//        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
//        writer.write(builder.toString());
//        writer.close();

        for (File JSONFile : JSONFolder.listFiles()) {
            if (JSONFile.getName().endsWith(".json")) {
                String JSONContent = new String(Files.readAllBytes(JSONFile.toPath()));
                Organization organization = gson.fromJson(JSONContent, Organization.class);

                if (organization.getUrl() != null) {
                    mapper.add(new Pair<>(organization.getName(), organization.getUrl()));
                    URLSet.add(organization.getUrl());
                }
            }
        }

        StringBuilder builder = new StringBuilder("");
        Iterator<String> i = URLSet.iterator();
        while (i.hasNext()) {
            String url = i.next();
            builder.append(url).append('\n');

            for (Pair<String, String> pair : mapper) {
                if (pair.getValue().equals(url)) {
                    builder.append(pair.getKey()).append('\n');
                }
            }
            builder.append('\n');
        }
        builder.append(mapper.size()).append(" entries in ").append(URLSet.size()).append(" groups");

        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        writer.write(builder.toString());
        writer.close();
    }

    public static class Worker implements Runnable {
        private File JSONFile;

        public Worker(File JSONFile) {
            this.JSONFile = JSONFile;
        }

        @Override
        public void run() {
            try {
                String JSONContent = new String(Files.readAllBytes(JSONFile.toPath()));
                Organization organization = gson.fromJson(JSONContent, Organization.class);

                if (organization.getUrl() != null) {
//                    mapper.put(organization.getName(), organization.getUrl());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
