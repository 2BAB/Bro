package me.xx2bab.bro.compiler.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by 2bab on 2017/2/4.
 */
public class FileUtil {

    public static String readFile(File file) {
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
            BroCompileLogger.e(e.getMessage());
            return null;
        }
    }

    public static void writeFile(String content, String filePath, String fileName) {
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                boolean result = folder.mkdirs();
                if (!result) {
                    return;
                }
            }

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
            BroCompileLogger.e(e.getMessage());
        }
    }

}
