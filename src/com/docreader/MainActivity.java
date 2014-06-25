package com.docreader;

import java.io.FileOutputStream;
import java.io.IOException;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.docreader.DropboxFileDownloader.FileDownloadListener;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    final static private String APP_KEY = "2l6bj3gezommlo0";
    final static private String APP_SECRET = "9o27zneozltgep8";

    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    private static final String TAG = "MainActivity";

    DropboxAPI<AndroidAuthSession> mApi;
    private boolean loggedIn;

    @ViewById
    Button buttonLogout;

    @AfterViews
    void afterViews() {
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        setLoggedIn(mApi.getSession().isLinked());
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();
        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                storeAuth(session);
                setLoggedIn(true);
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Couldn't authenticate with Dropbox:" + e.getLocalizedMessage(), Toast.LENGTH_LONG)
                        .show();
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }

    /**
     * Convenience function to change UI state based on being logged in
     */
    private void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
        buttonLogout.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a
     * local store, rather than storing user name & password, and
     * re-authenticating each time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one. This is only
        // necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    @Click
    void buttonLoadFile() {
        if (!loggedIn) {
            mApi.getSession().startOAuth2Authentication(this);
            return;
        }
        
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Getting file");
        dialog.show();
        DropboxFileDownloader df = new DropboxFileDownloader(this, mApi, "/Test1.doc", new FileDownloadListener() {
            
            @Override
            public void onDownloadSuccess(byte[] data) {
                FileOutputStream fos;
                try {
                    String filename = getApplicationContext().getCacheDir().getAbsoluteFile() + "/Test1.doc";
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
                showToast("Error while getting file");
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
        // Remove credentials from the session
        mApi.getSession().unlink();

        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false);
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a
     * local store, rather than storing user name & password, and
     * re-authenticating each time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0)
            return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is
            // for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

}
