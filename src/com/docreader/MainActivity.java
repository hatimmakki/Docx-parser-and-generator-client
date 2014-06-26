package com.docreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.docreader.dropbox.DropboxFileDownloader;
import com.docreader.dropbox.DropboxTaskListener;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewById
    Button buttonLogout;

    @Click
    void buttonLoadFile() {
        if (!isLoggedIn()) {
            login();
            return;
        }
        
        showFilenameDialog();
    }
    
    private void showFilenameDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Title");
        alert.setMessage("Message");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setText("/Test1.doc");
        alert.setView(input);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                loadFile(value);
            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });

        alert.show();

    }

    private void loadFile(String dropboxFilename) {
        final String filename = getApplicationContext().getCacheDir().getAbsoluteFile() + "/" + dropboxFilename;
        File file = new File(filename);
        if (file.exists()) {
            startActivity(DocumentActivity.getStartIntent(MainActivity.this, filename));
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Getting file");
        dialog.show();
        DropboxFileDownloader df = new DropboxFileDownloader(this, mApi, "/" + dropboxFilename, new DropboxTaskListener<byte[]>() {

            @Override
            public void onDownloadSuccess(byte[] data) {
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(filename);
                    fos.write(data);
                    fos.close();

                    showToast("File saved");

                    startActivity(DocumentActivity.getStartIntent(MainActivity.this, filename));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            void showToast(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDownloadError() {
                showToast("Error while getting file.");
            }

            @Override
            public void onDownloadComplete() {
                dialog.dismiss();
            }

            @Override
            public void onDownloadProgress(int percent) {
                dialog.setProgress(percent);
            }
        });
        df.execute();
    }
        @Click
    void buttonLogout() {
        logout();
    }

    @Override
    protected void onSetLoggedIn(boolean logged) {
        buttonLogout.setVisibility(isLoggedIn() ? View.VISIBLE : View.GONE);
    }

}
