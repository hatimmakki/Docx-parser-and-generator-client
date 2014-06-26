package com.docreader.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hwpf.usermodel.Paragraph;

import com.docreader.updater.ParagraphRangeUpdater;

public class MyParagraph {
    
    protected Paragraph paragraph; 
    protected Map<Object, ParagraphRangeUpdater> textSources = new HashMap<Object, ParagraphRangeUpdater>(); 
    
    public MyParagraph(Paragraph paragraph) {
        this.paragraph = paragraph;
    }

    public Paragraph getParagraph() {
        return paragraph;
    }

    public void setParagraph(Paragraph paragraph) {
        this.paragraph = paragraph;
    }
    
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
