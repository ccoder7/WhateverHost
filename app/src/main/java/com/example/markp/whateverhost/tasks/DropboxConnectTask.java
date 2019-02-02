package com.example.markp.whateverhost.tasks;

import android.os.AsyncTask;

import com.example.markp.whateverhost.MainActivity;

public class DropboxConnectTask extends AsyncTask<MainActivity,Integer, Boolean> {


    @Override
    protected Boolean doInBackground(MainActivity... mainActivities) {



        return mainActivities[0].accessDropbox();
    }
}
