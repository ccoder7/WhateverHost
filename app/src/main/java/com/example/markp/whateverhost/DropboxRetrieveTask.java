package com.example.markp.whateverhost;

import android.os.AsyncTask;

public class DropboxRetrieveTask extends AsyncTask<MainActivity, Integer, Long>
{
    @Override
    protected Long doInBackground(MainActivity... mainActivities)
    {
        mainActivities[0].setDropboxListView();

        return null;
    }
}
