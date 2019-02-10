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

        ArrayList<GoogleDriveFile> myFiles = listFiles(strings[0]);

        return myFiles;
    }


    private ArrayList<GoogleDriveFile> listFiles(String folderID)
    {
        ArrayList<GoogleDriveFile> currentFiles = new ArrayList<>();
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(MainActivity.mainActivity.getApplicationContext(), Collections.singleton(DriveScopes.DRIVE));

        credential.setSelectedAccount(MainActivity.mainActivity.googleAccount.getAccount());

        com.google.api.services.drive.Drive googleDriveService = new com.google.api.services.drive.Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName("WhateverHost")
                .build();

        List<File> result = new ArrayList<>();

        Log.d("Before","Before 1st try");

        try
        {
            com.google.api.services.drive.Drive.Files.List request = googleDriveService.files().list().set("folderId","root")
                    .setFields("files(id, name, parents)")
                    .setQ("'" + MainActivity.mainActivity.googleAccount.getEmail() + "' in owners").setQ("trashed = false and 'root' in parents");

            Log.d("Try","In 1st try");
            do
            {
                Log.d("Do","In do-while");
                try
                {


                    FileList files = request.execute();

                    Log.d("Try","In 2nd try D");

                    result.addAll(files.getFiles());

                    Log.d("Try","In 2nd try E");

                    request.setPageToken(files.getNextPageToken());

                    Log.d("Try","In 2nd try final");

                }
                catch (IOException e)
                {
                    Log.d("Catch","In 2nd catch");
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

                Log.d("Added","File added to current files");

                currentFiles.add(new GoogleDriveFile(file.getName(),file.getParents().get(0)));

                counter++;
            }

            Log.d("length",Integer.toString(counter));

        }
        catch (IOException e)
        {
            Log.d("Catch","1st try caught");
        }

        return currentFiles;

    }

    @Override
    protected void onPostExecute(ArrayList<GoogleDriveFile> googleDriveFiles) {
        super.onPostExecute(googleDriveFiles);

        for (GoogleDriveFile file : googleDriveFiles)
        {
            Log.d("Name",file.getFileName());
        }
        return;
    }
}
