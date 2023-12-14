package net.aniby.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[10];
        while (reader.read(buffer) != -1) {
            stringBuilder.append(new String(buffer));
            buffer = new char[10];
        }
        reader.close();
        return stringBuilder.toString();
    }

    public static void createFile(File file) throws IOException {
        if (!file.exists())
            file.createNewFile();
    }

    public static void appendToFile(File file, String text) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));
        writer.append("\n");
        writer.append(text);

        writer.close();
    }
}
