package com.docreader.updater;

import com.docreader.api.request.BuilderRequestItem;

/**
 * This objects used to change placeholders with user inputs
 */
abstract public class SegmentValueUpdater {

    public abstract BuilderRequestItem getRequestItem();
    
}