package me.xx2bab.bro.compiler.generator;

import java.util.Map;

import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.compiler.util.FileUtil;
import me.xx2bab.bro.compiler.util.MarkdownUtil;

public class DocGenerator implements IBroGenerator{

    public static void generateDoc(String rootProjectPath,
                                   Map<String, Map<String, BroProperties>> exposeMaps) {
        StringBuilder builder = new StringBuilder();

        builder.append(MarkdownUtil.makeH1("Bro-Doc")).append("\n");

        generate(builder, exposeMaps);

        FileUtil.writeFile(builder.toString(), rootProjectPath, "Bro-Doc.md");
    }

    private static void generate(StringBuilder builder,
                                 Map<String, Map<String, BroProperties>> exposeMaps) {
        builder.append("\nThis doc is Generated by Bro.\n");

        for (Map.Entry<String, Map<String, BroProperties>> entry : exposeMaps.entrySet()) {
            builder.append("\n");
            builder.append(MarkdownUtil.makeH3(entry.getKey()));
            builder.append("\n");
            builder.append(MarkdownUtil.makeTableForTwo(entry.getValue(), "Nick", "PackageName"));
            builder.append("\n");
        }
    }

    @Override
    public void onGenerate(String broBuildDirectory, Map<String, Map<String, BroProperties>> exposeMaps) {

    }

}
