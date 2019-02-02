package com.example.markp.whateverhost.tasks;

import android.os.AsyncTask;

import com.example.markp.whateverhost.MainActivity;
import com.example.markp.whateverhost.files.CustomFile;

import java.util.ArrayList;

public class DropboxRetrieveTask extends AsyncTask<MainActivity, Integer, ArrayList<CustomFile>>
{
    @Override
    protected ArrayList<CustomFile> doInBackground(MainActivity... mainActivities)
    {
        mainActivities[0].setDropboxListView();

        return null;
    }
}
