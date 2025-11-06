package com.helmes.task.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SectorExtractor {

    private static final Pattern OPTION =
            Pattern.compile("<option\\s+[^>]*value\\s*=\\s*\"(\\d+)\"[^>]*>(.*?)</option>",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public static void main(String[] args) {
        try {
            String html = readInputHtml();
            Path out = Paths.get("task/src/main/resources/data.sql");

            Path parent = out.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(out, StandardCharsets.UTF_8))) {
                Deque<Level> stack = new ArrayDeque<>();
                Matcher m = OPTION.matcher(html);

                while (m.find()) {
                    long id = Long.parseLong(m.group(1));
                    String inner = m.group(2);

                    int i = 0, indent = 0;
                    while (i < inner.length()) {
                        if (inner.startsWith("&nbsp;", i)) {
                            indent += 1;
                            i += 6;
                        }
                        else {
                            char c = inner.charAt(i);
                            if (c == ' ') {
                                indent += 1;
                                i++;
                            } else if (c == '\t') {
                                indent += 4;
                                i++;
                            }
                            else break;
                        }
                    }

                    String name = inner.substring(i)
                            .replace("&nbsp;", " ")
                            .replace('\u00A0', ' ')
                            .replaceAll("\\s+", " ")
                            .replace("&amp;", "&")
                            .trim();

                    while (!stack.isEmpty() && indent <= stack.peek().indent) {
                        stack.pop();
                    }
                    Long parentId = stack.isEmpty() ? null : stack.peek().id;

                    pw.printf("INSERT INTO sector(id, name, parent_id) VALUES (%d, '%s', %s);%n",
                            id, escapeSql(name), parentId == null ? "NULL" : parentId.toString());

                    stack.push(new Level(indent, id));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String readInputHtml() throws IOException {
        var is = SectorExtractor.class.getResourceAsStream("/templates/index.html");
        if (is != null) {
            try (is) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        throw new IllegalStateException("Could not find templates/index.html.\n" +
                "Place it at src/main/resources/templates/index.html.");
    }

    private record Level(int indent, long id) {}
    private static String escapeSql(String s) {
        return s.replace("'", "''");
    }
}
