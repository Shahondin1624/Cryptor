package logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PathHandler {

    public static List<File> getAllFiles(File file) {
        List<File> result = new ArrayList<>();
        if (file.isFile()) {
            result.add(file);
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    if (file1.isFile()) {
                        result.add(file1);
                    } else if (file1.isDirectory()) {
                        result.addAll(getAllFiles(file1));
                    }
                }
            }
        }
        return result;
    }
}
