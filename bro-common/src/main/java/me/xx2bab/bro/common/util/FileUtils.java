package me.xx2bab.bro.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    private static final FileUtils defaultInstance = new FileUtils();

    public static FileUtils getDefault() {
        return defaultInstance;
    }

    public String filterIllegalCharsForResFileName(String origin) {
        return origin.replace(":", "_")
                .replace(".", "_")
                .replace("-", "_");
    }

    public String readFile(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void writeFile(String content, String filePath, String fileName) {
        File folder = new File(filePath);
        if (!folder.exists()) {
            boolean result = folder.mkdirs();
            if (!result) {
                return;
            }
        }
        try {
            File file = new File(filePath + File.separator + fileName);
            if (!file.exists()) {
                boolean result = file.createNewFile();
                if (!result) {
                    return;
                }
            }

            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}