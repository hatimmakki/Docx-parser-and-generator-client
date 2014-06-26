package com.docreader.updater;

import org.apache.poi.hwpf.usermodel.CharacterRun;

abstract public class ParagraphRangeUpdater {

    public abstract CharacterRun updateRange(CharacterRun range);
    
}