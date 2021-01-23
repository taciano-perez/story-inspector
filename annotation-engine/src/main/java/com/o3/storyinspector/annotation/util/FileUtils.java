package com.o3.storyinspector.annotation.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileUtils {

    /**
     * Reads an entire file to a string.
     *
     * @param path the file path
     * @return the file contents
     */
    public static String readString(final Path path) throws IOException {
        final StringBuilder contentBuilder = new StringBuilder();

        final Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8);
        stream.forEach(s -> contentBuilder.append(s).append("\n"));

        return contentBuilder.toString();
    }

}
