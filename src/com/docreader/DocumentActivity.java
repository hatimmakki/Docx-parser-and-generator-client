package com.docreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.docreader.dropbox.DropboxFileUploader;
import com.docreader.dropbox.DropboxTaskListener;
import com.docreader.updater.DatepickerRangeUpdater;
import com.docreader.updater.EditTextRangeUpdater;
import com.docreader.updater.ImageRangeUpdater;
import com.docreader.updater.ParagraphRangeUpdater;
import com.docreader.util.MyParagraph;

@EActivity(R.layout.activity_document)
public class DocumentActivity extends BaseActivity {

    enum Types {
        NONE, TEXT, EDIT, BUTTON
    }

    public static final String EXTRA_FILENAME = "filename";

    protected static final int IMAGE_PICKER_SELECT = 1;

    @ViewById
    LinearLayout layoutParagraphs;

    private Vector<MyParagraph> parHolder = new Vector<MyParagraph>();
    private List<Object> ignored = new Vector<Object>();
    private Map<Object, ParagraphRangeUpdater> updaters = new HashMap<Object, ParagraphRangeUpdater>();

    private String lastFilename;

    protected ImageRangeUpdater forImageUpdater;

    @AfterViews
    void afterViews() {
        String filename = getIntent().getStringExtra(EXTRA_FILENAME);

        try {
            loadDocument(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load file, parse and show it
     * @param filename
     * @throws Exception
     */
    void loadDocument(String filename) throws Exception {
        lastFilename = filename;
        FileInputStream is = new FileInputStream(filename);
        XWPFDocument doc = new XWPFDocument(is);
        
        List<XWPFParagraph> paragraphs = doc.getParagraphs();
        
        parHolder.clear();
        updaters.clear();
        ignored.clear();

        MyParagraph myParagraph;
        for (int paragraphNumber = 0; paragraphNumber < paragraphs.size(); paragraphNumber++) {
            XWPFParagraph pr = paragraphs.get(paragraphNumber);

            layoutParagraphs.removeAllViews();
            
            myParagraph = new MyParagraph();
            parHolder.add(myParagraph);

            StringBuilder sbText = new StringBuilder();
            StringBuilder sbEditText = new StringBuilder();
            StringBuilder sbButton = new StringBuilder();

            Types prev = Types.NONE;
            Types current = Types.NONE;

            String prevKey = null;
            List<XWPFRun> runs = pr.getRuns();
            for (int characterRunNumber = 0; characterRunNumber<runs.size(); characterRunNumber++) {
                XWPFRun segment = runs.get(characterRunNumber);

                String runKey = paragraphNumber + ":" + characterRunNumber;

//                String highlight = segment.getCTR().getRPr().toString();
//                System.out.println("Text: " + text); // segment.getCTR().getRPr()
                int color = 0;
                
                // Dirty hack, but works. No possible to get background color in other way.
//                if (highlight.contains("w:fill=\"FF0000")) {
//                    color = 6;
//                } else if (highlight.contains("w:fill=\"00FF00")) {
//                    color = 4;
//                }
//                
                String text = segment.getText(segment.getTextPosition());
                switch (color) {
                    case 6:
                        current = Types.BUTTON;
                        ignored.add(runKey);
                        sbButton.append(text);
                        break;
                    case 4:
                        current = Types.EDIT;
                        ignored.add(runKey);
                        sbEditText.append(text);
                        break;
                    default:
                        current = Types.TEXT;
                        sbText.append(text);
                        break;
                }

                // An interesting part
                // One GREEN/RED block can be splitted by POI to several
                // segments
                // So to collect parts correctly I use something like FSM
                if ((prev != Types.NONE && current != prev) || characterRunNumber == runs.size() - 1) {
                    // For every kind of segment we add corresponding widget to
                    // layout

                    if (prev == Types.TEXT && 0 != sbText.length()) {
                        TextView parView = new TextView(this);
                        parView.setText(sbText.toString());
                        layoutParagraphs.addView(parView);
                        sbText = new StringBuilder();
                    }

                    // RED block is represented with "LOAD DATA" (for date) and
                    // "BROWSE..." for image
                    if (prev == Types.BUTTON && 0 != sbButton.length()) {
//                        myParagraph.addTextSource(prevKey, new ButtonRangeUpdater("BUTTTON"));
                        
                        String buttonsText = sbButton.toString();
                        Button button = new Button(this);
                        if ("LOAD DATA".equals(buttonsText)) {
                            button.setText("Set date");
                            myParagraph.addTextSource(prevKey, new DatepickerRangeUpdater(getSupportFragmentManager(), button));
                        } else if ("BROWSE…".equals(buttonsText)) {
                            final ImageRangeUpdater imageUpdater = new ImageRangeUpdater(button);
                            myParagraph.addTextSource(prevKey, imageUpdater);
                            button.setText("Browse image");
                            button.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    forImageUpdater = imageUpdater;
                                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, IMAGE_PICKER_SELECT);
                                }
                            });
//                            myParagraph.addTextSource(prevKey, new ImageRangeUpdater(button, "image.png"));
                        }
                        layoutParagraphs.addView(button);
                        sbButton = new StringBuilder();
                    }

                    // Edit texts for GREEN blocks
                    if (prev == Types.EDIT && 0 != sbEditText.length()) {
//                        myParagraph.addTextSource(prevKey, new EditRangeUpdater("EDIT"));
                        
                        EditText editText = new EditText(this);
                        editText.setHint(sbEditText.toString());
                        layoutParagraphs.addView(editText);
                        myParagraph.addTextSource(prevKey, new EditTextRangeUpdater(editText));
                        sbEditText = new StringBuilder();
                    }
                }

                prev = current;
                prevKey = runKey;
            }
        }
    }

    @Click
    void buttonGenerate() {
        showFilenameDialog();
    }

    /**
     * Show result filename prompt
     */
    void showFilenameDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Title");
        alert.setMessage("Message");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setText("Test1_result");
        alert.setView(input);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                saveDocument(value);
            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });

        alert.show();
    }

    /**
     * Saving file to Dropbox folder
     * @param dropboxFilename
     */
    private void saveDocument(String dropboxFilename) {
        try {
            FileInputStream is = new FileInputStream(lastFilename);
            XWPFDocument doc = new XWPFDocument(is);
            
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            
            for (int paragraphNumber = 0; paragraphNumber < paragraphs.size(); paragraphNumber++) {
                XWPFParagraph pr = paragraphs.get(paragraphNumber);
                MyParagraph myParagraph = parHolder.get(paragraphNumber);

                List<XWPFRun> runs = pr.getRuns();
                for (int characterRunNumber = 0; characterRunNumber<runs.size(); characterRunNumber++) {
                    XWPFRun segment = runs.get(characterRunNumber);

                    String runKey = paragraphNumber + ":" + characterRunNumber;
                    
                    if (myParagraph.hasTextSource(runKey)) {
                        ParagraphRangeUpdater rangeUpdater = myParagraph.getRangeUpdater(runKey);
                        rangeUpdater.updateRange(segment);
                    } else if (ignored.contains(runKey)) {
                        segment.setText("", 0);
                    }
                }
            }

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Saving file");
            dialog.show();

            // TODO: filename dialog
            String dropboxPath = "/" + dropboxFilename + ".doc";
            File file = new File(getCacheDir().getAbsolutePath() + dropboxPath);
            doc.write(new FileOutputStream(file));
            DropboxFileUploader uploader = new DropboxFileUploader(mApi, dropboxPath, file,
                    new DropboxTaskListener<Void>() {

                        @Override
                        public void onDownloadSuccess(Void data) {
                            makeToast("File uploaded", true);
                        }

                        @Override
                        public void onDownloadError() {
                            makeToast("Error while uploading file", true);
                        }

                        @Override
                        public void onDownloadComplete() {
                            dialog.dismiss();
                        }

                        @Override
                        public void onDownloadProgress(int percent) {
                        }
                    });
            uploader.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = getBitmapFromCameraData(data);
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            
            if (null != forImageUpdater) {
                forImageUpdater.setBitmap(bitmap);
                forImageUpdater = null;
            }
        }
    }

    public Bitmap getBitmapFromCameraData(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }
    /**
     * Helper for startActivity intent creation
     * @param context
     * @param filename
     * @return
     */
    static public Intent getStartIntent(Context context, String filename) {
        Intent intent = new Intent(context, DocumentActivity_.class);
        intent.putExtra(EXTRA_FILENAME, filename);

        return intent;
    }

}
