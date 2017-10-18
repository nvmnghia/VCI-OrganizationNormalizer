package Grouper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Test {
    public static void main(String[] args) {
        File JSONFolder = new File("D:\\VCI\\organization_json");
        File output = new File("D:\\VCI\\listGrouped.txt");

        try {
            Grouper.group(JSONFolder, output);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
