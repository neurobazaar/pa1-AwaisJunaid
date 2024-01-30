package csc435.app;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class SortWords {
    public long num_words = 0;
    public double execution_time = 0.0;

    public void sort_words(String inputDirStr, String outputDirStr) {
        long startTime = System.currentTimeMillis();

        Path inputDir = Paths.get(inputDirStr);
        
        Path outputDir = Paths.get(outputDirStr);

        try {
            Files.walk(inputDir)
                 .filter(Files::isRegularFile)
                 .forEach(file -> processFile(file, inputDir, outputDir));
        } catch (IOException e) {
            System.err.println("Error processing directory: " + inputDir);
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        execution_time = endTime - startTime;
    }

    private void processFile(Path inputFile, Path inputDir, Path outputDir) {
        Path relativePath = inputDir.relativize(inputFile);
        Path outputFile = outputDir.resolve(relativePath);

        try {
            Map<String, Integer> wordCounts = readWordCounts(inputFile);
            num_words += wordCounts.size();
            List<Map.Entry<String, Integer>> sortedWords = SortWordsByFrequency(wordCounts);
            writeOutputFile(sortedWords, outputFile);
        } catch (IOException e) {
            System.err.println("Error processing file: " + inputFile);
            e.printStackTrace();
        }
    }

    private Map<String, Integer> readWordCounts(Path file) throws IOException {
        Map<String, Integer> wordCounts = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    wordCounts.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        }
        return wordCounts;
    }

    private List<Map.Entry<String, Integer>> SortWordsByFrequency(Map<String, Integer> wordCounts) {
        return wordCounts.entrySet()
                         .stream()
                         .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                         .collect(Collectors.toList());

    }

    private void writeOutputFile(List<Map.Entry<String, Integer>> sortedWords, Path outputFile) throws IOException {
        Files.createDirectories(outputFile.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            for (Map.Entry<String,  Integer> entry : sortedWords) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("improper number of arguments");
            System.exit(1);
        }

        SortWords SortWords = new SortWords();

        SortWords.sort_words(args[0], args[1]);
        
        System.out.print("Finished sorting " + SortWords.num_words + " words");
        System.out.println(" in " + SortWords.execution_time + " milliseconds");
    }
}
