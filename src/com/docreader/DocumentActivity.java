package com.docreader;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.docreader.api.RestCommand;
import com.docreader.api.RestManager;
import com.docreader.api.request.DefaultRequestListener;
import com.docreader.api.request.ParseCommand;
import com.docreader.api.request.RequestCommand;
import com.docreader.api.response.ParserResponse;
import com.docreader.api.response.ParserResponse.ParserResponseItem;
import com.docreader.updater.DatepickerSegmentUpdater;
import com.docreader.updater.EditSegmentUpdater;
import com.docreader.updater.ImageSegmentUpdater;
import com.docreader.updater.SegmentValueUpdater;

@EActivity(R.layout.activity_document)
public class DocumentActivity extends BaseActivity {

    enum Types {
        NONE, TEXT, EDIT, BUTTON
    }

    public static final String EXTRA_FILENAME = "filename";
    protected static final int IMAGE_PICKER_SELECT = 1;

    @Bean
    RestManager restManager;
    @ViewById
    LinearLayout layoutParagraphs;

    private String lastFilename;
    private ImageSegmentUpdater forImageUpdater;

    List<SegmentValueUpdater> updaters = new Vector<SegmentValueUpdater>();
    List<Bitmap> requestImages = new Vector<Bitmap>();
    
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
     * 
     * @param filename
     * @throws Exception
     */
    void loadDocument(String filename) throws Exception {
        lastFilename = filename;

        RestCommand<?> command = restManager.createCommand(new ParseCommand(new File(filename)),
                new DefaultRequestListener<ParserResponse>() {

                    @Override
                    public void onResponse(ParserResponse response, RequestCommand<ParserResponse> request) {
                        updaters.clear();
                        requestImages.clear();

                        for (ParserResponseItem item : response.getData()) {
                            View view = null;
                            switch (item.getType()) {
                                case DATE:
                                    Button datePicker = new Button(getApplicationContext());
                                    datePicker.setText("Set date");
                                    updaters.add(new DatepickerSegmentUpdater(getSupportFragmentManager(), datePicker));
                                    view = datePicker;
                                    break;
                                case EDIT:
                                    EditText editText = new EditText(getApplicationContext());
                                    editText.setHint(item.getData().getHint());
                                    updaters.add(new EditSegmentUpdater(editText));
                                    view = editText;

                                    break;
                                case IMAGE:
                                    Button imagePicker = new Button(getApplicationContext());
                                    final ImageSegmentUpdater imageUpdater = new ImageSegmentUpdater(imagePicker, item
                                            .getKey(), requestImages);
                                    imagePicker.setText("Browse image");
                                    imagePicker.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            forImageUpdater = imageUpdater;
                                            Intent intent = new Intent(Intent.ACTION_PICK,
                                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            startActivityForResult(intent, IMAGE_PICKER_SELECT);
                                        }
                                    });
                                    view = imagePicker;

                                    break;
                                    
                                case TEXT:
                                    TextView textView = new TextView(getApplicationContext());
                                    textView.setText(item.getData().getText());
                                    view = textView;
                                    break;
                                default:
                                    break;

                            }
                            
                            if (null != view)
                                layoutParagraphs.addView(view);
                        }
                    }

                    @Override
                    public void onComplete(RequestCommand<ParserResponse> request) {

                    }

                });
        command.execute();
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
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    /**
     * Saving file to Dropbox folder
     * 
     * @param filename
     */
    private void saveDocument(String filename) {
        /*
        try {
            // TODO: request server
            // new ReqCo
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
        */
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {
            makeToast("Image selected, but will not be processed.", true);
        }
    }

    /**
     * Helper for startActivity intent creation
     * 
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
