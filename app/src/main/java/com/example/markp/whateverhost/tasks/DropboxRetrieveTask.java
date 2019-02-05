package com.example.markp.whateverhost.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.markp.whateverhost.MainActivity;
import com.example.markp.whateverhost.files.DropboxFile;

import java.util.ArrayList;

public class DropboxRetrieveTask extends AsyncTask<String, Integer, ArrayList<DropboxFile>>
{
    @Override
    protected ArrayList<DropboxFile> doInBackground(String... path)
    {
        ArrayList<DropboxFile> files = new ArrayList<>();
        try
        {
            ListFolderResult result = MainActivity.mainActivity.client.files().listFolder(path[0]);
            
            files = new ArrayList<>();

            for (Metadata metadata : result.getEntries())
            {

                files.add(new DropboxFile(metadata.getName(),metadata.getPathLower()));

            }

            return files;


        }
        catch (DbxException e)
        {
            Log.d("2nd","retrieve files exception");
            Log.d("Error 2",e.getRequestId());
            return null;
        }


        //return mainActivities[0].getDropboxList("");
    }
}
