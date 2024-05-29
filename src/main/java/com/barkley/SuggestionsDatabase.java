package com.barkley;

import java.util.HashMap;
import java.util.Map;

public class SuggestionsDatabase {
    private Map<String,Integer> wordMap;
    public Map<String, Integer> getWordMap() {
        if (wordMap == null) {
            wordMap = new HashMap<>();
        }
        return wordMap;
    }
    public void setWordMap(Map<String, Integer> wordMap) {
        this.wordMap = wordMap;
    }
}
