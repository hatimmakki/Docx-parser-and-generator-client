package com.docreader;

import java.io.FileInputStream;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;

@EActivity(R.layout.activity_document)
public class DocumentActivity extends Activity {

    public static final String EXTRA_FILENAME = "filename";
    
    @ViewById LinearLayout layoutParagraphs;

    @AfterViews
    void afterViews() {
        String filename = getIntent().getStringExtra(EXTRA_FILENAME);
        
        try {
            loadDocument(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void loadDocument(String filename) throws Exception {
        POIFSFileSystem fis = new POIFSFileSystem(new FileInputStream(filename));
        HWPFDocument doc = new HWPFDocument(fis);
        
        Range r = doc.getRange();

        WordExtractor we = new WordExtractor(doc);
        String[] paragraphs = we.getParagraphText();
        we.close();
        
        for (int i = 0; i < paragraphs.length; i++) {
            Paragraph pr = r.getParagraph(i);
            
            System.out.println(pr.getEndOffset());
            int j = 0;
            while (true) {
                CharacterRun run = pr.getCharacterRun(j++);
                
                if (6 == run.getHighlightedColor()) {
                    run.replaceText("<b>Gotcha</b>", false);
                }
                
                if (4 == run.getHighlightedColor()) {
                    run.replaceText("<b>Green one</b>", false);
                }
                
                if (run.getEndOffset() == pr.getEndOffset()) {
                    break;
                }
            }
            
            TextView parView = new TextView(this);
            parView.setText(pr.text());
            layoutParagraphs.addView(parView);
        }
    }
    
    
    static public Intent getStartIntent(Context context, String filename) {
        Intent intent = new Intent(context, DocumentActivity_.class);
        intent.putExtra(EXTRA_FILENAME, filename);
        
        return intent;
    }
    
}
