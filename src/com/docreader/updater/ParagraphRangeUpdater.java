package com.docreader.updater;

import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * This objects used to change placeholders with user inputs
 */
abstract public class ParagraphRangeUpdater {

    public abstract XWPFRun updateRange(XWPFRun range);
    
}