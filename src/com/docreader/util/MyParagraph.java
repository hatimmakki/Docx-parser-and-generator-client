package com.docreader.util;

import java.util.HashMap;
import java.util.Map;

import com.docreader.updater.ParagraphRangeUpdater;

public class MyParagraph {
    
    protected Map<Object, ParagraphRangeUpdater> textSources = new HashMap<Object, ParagraphRangeUpdater>(); 
    
    public Map<Object, ParagraphRangeUpdater> getTextSources() {
        return textSources;
    }
    
    public void addTextSource(Object key, ParagraphRangeUpdater source) {
        textSources.put(key, source);
    }
    
    public boolean hasTextSource(Object key) {
        return textSources.containsKey(key);
    }
    
    public ParagraphRangeUpdater getRangeUpdater(Object key) {
        return textSources.get(key);
    }

}
