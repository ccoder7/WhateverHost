package com.example.markp.whateverhost;

import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;

public class DeviceListFragment extends Fragment {

    public static ArrayList<File> filesList;

    public File currentFolder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list, container,false);

        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setList(Environment.getExternalStorageDirectory());
    }

    public void setList(File parentFolder)
    {

        if (!parentFolder.canRead())
        {
            return;
        }

        currentFolder = parentFolder;
        //GET FILES FROM FOLDER

        File[] files = parentFolder.listFiles();

        filesList = new ArrayList<>();


        for (int i=0;i<files.length;i++)
        {
            filesList.add(files[i]);
        }

        //INIT LIST
        RecyclerView myRv = (RecyclerView)((MainActivity)getActivity()).findViewById(R.id.deviceListView);

        FileFolderAdapter myAdapter;

        myAdapter= new FileFolderAdapter((getContext()),filesList, this);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((getActivity()));
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRv.setLayoutManager(linearLayoutManager);

        myRv.setAdapter(myAdapter);

        TextView folderPath = getActivity().findViewById(R.id.currentFolderText);

        folderPath.setText(parentFolder.getAbsolutePath());
    }
}
