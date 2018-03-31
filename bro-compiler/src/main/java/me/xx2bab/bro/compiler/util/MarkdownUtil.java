package me.xx2bab.bro.compiler.util;


import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.TreeMap;

import me.xx2bab.bro.common.BroProperties;

/**
 * Created by 2bab on 2017/2/4.
 * <p>
 * Help to generate Markdown document.
 */

public class MarkdownUtil {

    public static String makeH1(String content) {
        return "# " + content + "\n";
    }

    public static String makeH2(String content) {
        return "## " + content + "\n";
    }

    public static String makeH3(String content) {
        return "### " + content + "\n";
    }

    public static String makeUnorderedList(List<String> originList) {
        return makeUnorderedList(originList, 0);
    }

    public static String makeUnorderedList(List<String> originList, int padding) {
        StringBuilder builder = new StringBuilder();
        String paddingString = "";
        for (int i = 0; i < padding; i++) {
            paddingString += " ";
        }
        for (String line : originList) {
            builder.append(paddingString).append("- ").append(line).append("\n");
        }
        return builder.toString();
    }

    public static String makeOrderedList(List<String> originList) {
        return makeOrderedList(originList, 0);
    }

    public static String makeOrderedList(List<String> originList, int padding) {
        StringBuilder builder = new StringBuilder();
        String paddingString = "";
        for (int i = 0; i < padding; i++) {
            paddingString += " ";
        }
        for (int j = 0; j < originList.size(); j++) {
            String line = originList.get(j);
            builder.append(paddingString).append(j).append(" ").append(line).append("\n");
        }
        return builder.toString();
    }

    public static String makeBlockquote(String content) {
        return "> " + content + "\n";
    }

    public static String makeCodeBlock(String content) {
        return makeCodeBlock(content, "java");
    }

    public static String makeCodeBlock(String content, String lang) {
        return "``` " + lang + "\n" + content + "\n```" + "\n";
    }

    public static String makeTableForTwo(Map<String, BroProperties> contentMap, String tableTitle1, String tableTitle2) {
        StringBuilder builder = new StringBuilder();

        builder.append("|").append(tableTitle1).append("|").append(tableTitle2).append("|").append("\n");
        builder.append("|:---:|:---:|").append("\n");

        Set<Map.Entry<String, BroProperties>> set = new TreeMap<>(contentMap).entrySet();
        for (Map.Entry<String, BroProperties> entry : set) {
            builder.append("|").append(entry.getKey()).append("|").append(entry.getValue().toJsonString()).append("|").append("\n");
        }
        return builder.toString();
    }

}
