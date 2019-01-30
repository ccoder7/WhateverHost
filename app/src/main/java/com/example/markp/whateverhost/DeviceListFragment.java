package com.example.markp.whateverhost;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

public class DeviceListFragment extends Fragment {

    public static ArrayList<File> filesList;

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

    private void setList(File parentFolder)
    {
        //GET FILES FROM FOLDER

        File testFile = new File("/storage/4B42-1A17/DCIM/Camera");
        //File[] files = parentFolder.listFiles();
        File[] files = testFile.listFiles();

        filesList = new ArrayList<>();


        for (int i=0;i<files.length;i++)
        {
            filesList.add(files[i]);
        }

        //INIT LIST
        RecyclerView myRv = (RecyclerView)((MainActivity)getActivity()).findViewById(R.id.deviceListView);

        FileFolderAdapter myAdapter =new FileFolderAdapter(((MainActivity)getContext()),filesList);

        myRv.setLayoutManager(new LinearLayoutManager(((MainActivity)getContext())));

        myRv.setAdapter(myAdapter);
    }
}
