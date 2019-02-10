package com.example.markp.whateverhost.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.markp.whateverhost.MainActivity;
import com.example.markp.whateverhost.R;
import com.example.markp.whateverhost.adapters.GoogleDriveFileAdapter;
import com.example.markp.whateverhost.files.GoogleDriveFile;
import com.example.markp.whateverhost.tasks.GoogleDriveRetrieveTask;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleDriveListFragment extends Fragment
{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_googledrive_list, container,false);


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //LOAD FILES

        retrieveFiles();


    }

    private void retrieveFiles()
    {

        MainActivity.mainActivity.mDriveServiceHelper.queryFiles().addOnSuccessListener(fileList ->
        {
            MainActivity.mainActivity.googleDriveFiles = new ArrayList<>();
            for (File file : fileList.getFiles())
            {
                MainActivity.mainActivity.googleDriveFiles.add(new GoogleDriveFile(file.getName(),file.getParents().get(0)));
            }


            setGoogleDriveAdapters("root");

        }).addOnFailureListener(e -> Log.e("Failure","Unable to query files",e));
    }

    private void setGoogleDriveAdapters(String path)
    {
        RecyclerView myRv = ((MainActivity)getActivity()).findViewById(R.id.googleDriveListView);

        GoogleDriveFileAdapter adapter = new GoogleDriveFileAdapter(getContext(),((MainActivity)getActivity()).googleDriveFiles,this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRv.setLayoutManager(linearLayoutManager);

        myRv.setAdapter(adapter);

        TextView folderPath = ((MainActivity)getActivity()).findViewById(R.id.currentGoogleDriveFolderText);

        folderPath.setText(path);
    }


}
