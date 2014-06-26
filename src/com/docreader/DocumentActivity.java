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
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        lastFilename = filename;
        POIFSFileSystem fis = new POIFSFileSystem(new FileInputStream(filename));
        HWPFDocument doc = new HWPFDocument(fis);

        Range r = doc.getRange();

        WordExtractor we = new WordExtractor(doc);
        String[] paragraphs = we.getParagraphText();
        we.close();

        parHolder.clear();
        updaters.clear();
        ignored.clear();

        MyParagraph myParagraph;
        for (int paragraphNumber = 0; paragraphNumber < paragraphs.length; paragraphNumber++) {
            Paragraph pr = r.getParagraph(paragraphNumber);

            myParagraph = new MyParagraph(pr);
            parHolder.add(myParagraph);

            int characterRunNumber = 0;
            StringBuilder sbText = new StringBuilder();
            StringBuilder sbEditText = new StringBuilder();
            StringBuilder sbButton = new StringBuilder();

            Types prev = Types.NONE;
            Types current = Types.NONE;

            String prevKey = null;
            while (true) {
                CharacterRun run = pr.getCharacterRun(characterRunNumber++);

                String runKey = paragraphNumber + ":" + characterRunNumber;

                String text = run.text();
                switch (run.getHighlightedColor()) {
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

                // TODO: HERE!!!
                if ((prev != Types.NONE && current != prev) || run.getEndOffset() == pr.getEndOffset()) {
                    if (prev == Types.TEXT && 0 != sbText.length()) {
                        TextView parView = new TextView(this);
                        parView.setText(sbText.toString());
                        layoutParagraphs.addView(parView);
                        sbText = new StringBuilder();
                    }

                    if (prev == Types.BUTTON && 0 != sbButton.length()) {
                        String buttonsText = sbButton.toString();
                        Button button = new Button(this);
                        if ("LOAD DATA".equals(buttonsText)) {
                            button.setText("Set date");
                            myParagraph.addTextSource(prevKey, new DatepickerRangeUpdater(getSupportFragmentManager(),
                                    button));
                        } else if ("BROWSE…".equals(buttonsText)) {
                            button.setText("Browse image");
                            button.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, IMAGE_PICKER_SELECT);
                                }
                            });
                            myParagraph.addTextSource(prevKey, new ImageRangeUpdater(button, "image.png"));
                        }
                        layoutParagraphs.addView(button);
                        sbButton = new StringBuilder();
                    }

                    if (prev == Types.EDIT && 0 != sbEditText.length()) {
                        EditText editText = new EditText(this);
                        editText.setHint(sbEditText.toString());
                        layoutParagraphs.addView(editText);
                        myParagraph.addTextSource(prevKey, new EditTextRangeUpdater(editText));
                        sbEditText = new StringBuilder();
                    }
                }

                prev = current;
                prevKey = runKey;

                if (run.getEndOffset() == pr.getEndOffset()) {
                    break;
                }
            }
        }

    }

    @Click
    void buttonGenerate() {
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
                saveFile(value);
            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });

        alert.show();
    }

    private void saveFile(String filename) {
        try {
            HWPFDocument docWrite = new HWPFDocument(new FileInputStream(new File(lastFilename)));

            WordExtractor we = new WordExtractor(docWrite);
            String[] paragraphs = we.getParagraphText();
            we.close();

            Range r = docWrite.getRange();
            for (int i = 0; i < paragraphs.length; i++) {
                Paragraph pr = r.getParagraph(i);
                MyParagraph myParagraph = parHolder.get(i);

                int j = 0;
                while (true) {
                    CharacterRun run = pr.getCharacterRun(j++);
                    run.setHighlighted((byte) 0);
                    String runKey = i + ":" + j;

                    if (myParagraph.hasTextSource(runKey)) {
                        ParagraphRangeUpdater rangeUpdater = myParagraph.getRangeUpdater(runKey);
                        rangeUpdater.updateRange(run);
                    } else if (ignored.contains(runKey)) {
                        run.replaceText("", false);
                    }

                    if (run.getEndOffset() == pr.getEndOffset()) {
                        break;
                    }
                }
            }

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Saving file");
            dialog.show();

            // TODO: filename dialog
            String dropboxPath = "/" + filename + ".doc";
            File file = new File(getCacheDir().getAbsolutePath() + dropboxPath);
            docWrite.write(new FileOutputStream(file));
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
            makeToast("Image selected, but will not be processed.", true);
        }
    }

    static public Intent getStartIntent(Context context, String filename) {
        Intent intent = new Intent(context, DocumentActivity_.class);
        intent.putExtra(EXTRA_FILENAME, filename);

        return intent;
    }

}
