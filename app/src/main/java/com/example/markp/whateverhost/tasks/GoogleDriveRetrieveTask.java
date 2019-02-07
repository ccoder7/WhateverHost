package com.example.markp.whateverhost.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.markp.whateverhost.MainActivity;
import com.example.markp.whateverhost.files.GoogleDriveFile;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleDriveRetrieveTask extends AsyncTask <String,Integer, ArrayList<GoogleDriveFile>>
{
    @Override
    protected ArrayList<GoogleDriveFile> doInBackground(String... strings)
    {
        listFiles(strings[0]);
        return null;
    }


    private void listFiles(String folderID)
    {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(MainActivity.mainActivity.getApplicationContext(), Collections.singleton(DriveScopes.DRIVE));

        credential.setSelectedAccount(MainActivity.mainActivity.googleAccount.getAccount());

        com.google.api.services.drive.Drive googleDriveService = new com.google.api.services.drive.Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName("WhateverHost")
                .build();








        List<File> result = new ArrayList<>();

        try
        {
            com.google.api.services.drive.Drive.Files.List request = googleDriveService.files().list().set("folderId","root")
                    .setFields("files(id, name, parents)")
                    .setQ("'" + MainActivity.mainActivity.googleAccount.getEmail() + "' in owners").setQ("trashed = false and 'root' in parents");

            do
            {


                try
                {
                    //CREATE TEST FOLDER

                    File folder = new File();

                    folder.setName("TEST FOLDER");
                    folder.setMimeType("application/vnd.google-apps.folder");

                    File file = googleDriveService.files().create(folder).setFields("id").execute();

                    FileList files = request.execute();
                    result.addAll(files.getFiles());
                    request.setPageToken(files.getNextPageToken());


                }
                catch (IOException e)
                {
                }

            }while (request.getPageToken() != null && request.getPageToken().length()>0);

            int counter = 0;

            for (File file : result)
            {
                if (file.getParents()!=null)
                {
                    Log.d("test",file.getName() + " - " + file.getParents().get(0));

                    if (file.getParents().get(0).equals("root"))
                    {
                        Log.d("InRoot",file.getName() + " - " + file.getParents().get(0));
                    }
                }

                counter++;
            }

            Log.d("length",Integer.toString(counter));

        }
        catch (IOException e)
        {

        }


    }
}
