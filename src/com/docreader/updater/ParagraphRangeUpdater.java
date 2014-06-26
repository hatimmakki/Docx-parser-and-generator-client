package com.docreader.updater;

import org.apache.poi.hwpf.usermodel.CharacterRun;

/**
 * This objects used to change placeholders with user inputs
 */
abstract public class ParagraphRangeUpdater {

    public abstract CharacterRun updateRange(CharacterRun range);
    
}