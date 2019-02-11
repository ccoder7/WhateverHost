package com.example.markp.whateverhost.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DropboxDownloadTask extends AsyncTask<FileMetadata, Void, File>
{
    private final Context mContext;

    private final DbxClientV2 mDbxClient;

    private final Callback mCallback;

    private Exception mException;

    public DropboxDownloadTask(Context mContext, DbxClientV2 mDbxClient, Callback mCallback) {
        this.mContext = mContext;
        this.mDbxClient = mDbxClient;
        this.mCallback = mCallback;
    }

    @Override
    protected void onPostExecute(File result) {
        super.onPostExecute(result);

        if (mException!=null)
        {
            mCallback.onError(mException);
        }
        else
        {
            mCallback.onDownloadComplete(result);
        }
    }

    public interface Callback
    {
        void onDownloadComplete(File result);

        void onError(Exception e);
    }




    @Override
    protected File doInBackground(FileMetadata... fileMetadata)
    {
        FileMetadata metadata = fileMetadata[0];
        try {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, metadata.getName());

            // Make sure the Downloads directory exists.
            if (!path.exists()) {
                if (!path.mkdirs()) {
                    mException = new RuntimeException("Unable to create directory: " + path);
                }
            } else if (!path.isDirectory()) {
                mException = new IllegalStateException("Download path is not a directory: " + path);
                return null;
            }

            // Download the file.
            try (OutputStream outputStream = new FileOutputStream(file)) {
                mDbxClient.files().download(metadata.getPathLower(), metadata.getRev())
                        .download(outputStream);
            }

            // Tell android about the file
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            mContext.sendBroadcast(intent);

            return file;
        } catch (DbxException | IOException e) {
            mException = e;
        }

        return null;
    }
}
