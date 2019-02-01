package com.example.markp.whateverhost;

import android.os.AsyncTask;

public class DropboxConnectTask extends AsyncTask<MainActivity,Integer, Long> {


    @Override
    protected Long doInBackground(MainActivity... mainActivities) {

        mainActivities[0].accessDropbox();

        return null;
    }
}
