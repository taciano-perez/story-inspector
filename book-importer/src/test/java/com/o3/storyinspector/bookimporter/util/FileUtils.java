package com.o3.storyinspector.bookimporter.util;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    /**
     * Converts an URI into a file path.
     *
     * @param uri the URI returned by resource.getPath()
     * @return the file path
     */
    public static String getPathFromUri(final String uri) throws IOException {
        return new File(URLDecoder.decode(uri, "utf-8")).getPath();
    }

    /**
     * Reads an entire file to a string.
     *
     * @param uri the URI returned by resource.getPath()
     * @return the file contents
     */
    public static String readStringFromUri(final String uri) throws IOException {
        return readString(Paths.get(getPathFromUri(uri)));
    }

}
