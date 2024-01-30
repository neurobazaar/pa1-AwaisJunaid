package csc435.app;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CountWords {
    public long dataset_size = 0;
    public double execution_time = 0.0;

    public void count_words(String inputDirStr, String outputDirStr) {
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
            Map<String, Integer> wordCounts = countWordsInFile(inputFile);  // Corrected method name here
            writeOutputFile(wordCounts, outputFile);
        } catch (IOException e) {
            System.err.println("Error processing file: " + inputFile);
            e.printStackTrace();
        }
    }

    private Map<String, Integer> countWordsInFile(Path file) throws IOException {  // Renamed method for clarity
        Map<String, Integer> wordCounts = new HashMap<>();
        long bytesRead = 0;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                bytesRead += line.getBytes().length;
                String[] words = line.split("[\\s,.;:?!()]+");
                for (String word : words) {
                    word = word.toLowerCase();
                    wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
                }
            }
        }

        dataset_size += bytesRead;  // Update total bytes read
        return wordCounts;
    }

    private void writeOutputFile(Map<String, Integer> wordCounts, Path outputFile) throws IOException {
        Files.createDirectories(outputFile.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments");
            System.exit(1);
        }

        CountWords countWords = new CountWords();  // Changed variable name for clarity

        countWords.count_words(args[0], args[1]);
        
        System.out.println("Finished counting " + countWords.dataset_size + " bytes of words in " + countWords.execution_time + " milliseconds");
    }
}
