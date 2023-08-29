package com.hackclub.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A bunch of generic utilities used commonly throughout the project
 */
public class Utils {
    public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> sortDescendingByValue() {
        return (Comparator<Map.Entry<K, V>> & Serializable)
                (c1, c2) -> c2.getValue().compareTo(c1.getValue());
    }

    public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> sortAscendingByValue() {
        return (Comparator<Map.Entry<K, V>> & Serializable)
                (c1, c2) -> c1.getValue().compareTo(c2.getValue());
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Reads given resource file as a string.
     *
     * @param fileName path to the resource file
     * @return the file's contents
     * @throws IOException if read fails for any reason
     */
    public static String getResourceFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(fileName)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    public static String getPrettyJson(String rawJson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(rawJson));
    }

    public static int levenshtein(String str1, String str2) {
        return levenshtein(str1, str2, 5);
    }

    public static int levenshtein(String str1, String str2, int threshold) {
        return new LevenshteinDistance(threshold).apply(str1, str2);
    }

    public static String getLastPathInUrl(String github) {
        if (StringUtils.isEmpty(github))
            return null;

        try {
            URL url = new URL(github);
            String path = url.getPath().replace("/", "");
            return path;
        } catch (Throwable t) {
            return null;
        }
    }

    public static double normalizedLevenshtein(String fromStr, String toStr, int maxDelta) {
        double levDist = Utils.levenshtein(fromStr, toStr, maxDelta);
        if (levDist == -1)
            levDist = maxDelta;
        return (maxDelta - levDist) / (float)maxDelta;
    }
}
