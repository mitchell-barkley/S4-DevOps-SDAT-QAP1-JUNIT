package com.barkley;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SuggestionEngine {
    private SuggestionsDatabase wordSuggestionDB;

    /**
     *   Based on algorithm from norvig.com/spell-correct.html
     *   Specifically the second part which describes how to create a Candidate Model in order to determine a list of
     *   words that the user may be trying to input.
     */

    private Stream<String> wordEdits(final String word) {
        Stream<String> deletes    = IntStream.range(0, word.length())  .mapToObj((i) -> word.substring(0, i) + word.substring(i + 1));
        Stream<String> replaces   = IntStream.range(0, word.length())  .boxed().flatMap( (i) -> "abcdefghijklmnopqrstuvwxyz".chars().mapToObj( (c) ->
                word.substring(0,i) + (char)c + word.substring(i+1) )  );
        Stream<String> inserts    = IntStream.range(0, word.length()+1).boxed().flatMap( (i) -> "abcdefghijklmnopqrstuvwxyz".chars().mapToObj( (c) ->
                word.substring(0,i) + (char)c + word.substring(i) )  );
        Stream<String> transposes = IntStream.range(0, word.length()-1).mapToObj((i)-> word.substring(0,i) + word.charAt(i+1) + word.charAt(i) + word.substring(i+2) );
        return Stream.of( deletes,replaces,inserts,transposes ).flatMap((x)->x);
    }

    // Look for keyword from resources

    private Stream<String> known(Stream<String> words) {
        return words.filter( (word) -> getWordSuggestionDB().containsKey(word) );
    }

    /**
     * Load a list of words into memory from the given Path, converting all words to lower case and file is assumed to
     * be delimited by '\n'.
     * @param dictionaryFile the Path to the file to be loaded
     * @throws IOException an any file loading problems
     */

    public void loadDictionaryData(Path dictionaryFile) throws IOException {
        Stream.of(new String(Files.readAllBytes( dictionaryFile )).toLowerCase().split("\n")).forEach( (word) ->{
            getWordSuggestionDB().compute( word, (k, v) -> v == null ? 1 : v + 1  );
        });
    }

    public String generateSuggestions(String word) {
        if (getWordSuggestionDB().containsKey(word)) {
            return "";
        }

        Stream<String> e1 = known(wordEdits(word));
        Stream<String> e2 = known(wordEdits(word).flatMap(this::wordEdits));

        Stream<String> suggestions = Stream.concat(e1, e2);

        Map<String, Long> collectedSuggestions = suggestions
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return  collectedSuggestions.keySet().stream()
                .sorted(Comparator
                        .comparing(collectedSuggestions::get)
                        .reversed()
                        .thenComparing(Collator.getInstance()))
                .limit(10) // limit to top 10 suggestions to keep list consumable
                .collect(Collectors.joining("\n"));
    }

    public Map<String, Integer> getWordSuggestionDB() {
        if (wordSuggestionDB == null) {
            wordSuggestionDB = new SuggestionsDatabase();
        }

        return wordSuggestionDB.getWordMap();
    }

    public void setWordSuggestionDB(SuggestionsDatabase wordSuggestionDB) {
        this.wordSuggestionDB = wordSuggestionDB;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("USAGE: " + SuggestionEngine.class.getName() + "<word to generateSuggestions>");
            return;
        }
        String word = args[0];
        if (word.equalsIgnoreCase("?")) {
            System.out.println("USAGE: " + SuggestionEngine.class.getName() + " <word to generate suggestions>");
            System.out.println("Output: A list of suggestions OR empty string if word is correctly spelled.");
            return;
        }

        SuggestionEngine suggestionEngine = new SuggestionEngine();
        try {
            suggestionEngine.loadDictionaryData(Paths.get(ClassLoader.getSystemResource("words.txt").toURI()));
        } catch (Exception e) {
            System.err.println("Error loading dictionary file: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println(suggestionEngine.generateSuggestions(word));
    }
}
