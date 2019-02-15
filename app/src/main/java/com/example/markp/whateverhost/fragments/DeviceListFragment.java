package com.example.markp.whateverhost.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.markp.whateverhost.MainActivity;
import com.example.markp.whateverhost.R;
import com.example.markp.whateverhost.adapters.FileFolderAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DeviceListFragment extends Fragment {

    public static ArrayList<File> filesList;
    public static ArrayList<Boolean> isChecked;

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
            NavigationView navigationView = (NavigationView) MainActivity.mainActivity.findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(0).setChecked(true);
            MainActivity.mainActivity.setHomepage();
            return;
        }

        currentFolder = parentFolder;
        //GET FILES FROM FOLDER

        File[] files = parentFolder.listFiles();

        filesList = new ArrayList<>();
        isChecked = new ArrayList<>();


        for (int i=0;i<files.length;i++)
        {
            if (!files[i].getName().substring(0,1).equals("."))
            {
                filesList.add(files[i]);

                isChecked.add(false);
            }

        }

        sortFiles();

        //INIT LIST
        RecyclerView myRv = (RecyclerView)((MainActivity)getActivity()).findViewById(R.id.deviceListView);

        FileFolderAdapter myAdapter;

        myAdapter= new FileFolderAdapter((getContext()),filesList, isChecked, this);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((getActivity()));
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRv.setLayoutManager(linearLayoutManager);

        myRv.setAdapter(myAdapter);

        TextView folderPath = getActivity().findViewById(R.id.currentFolderText);

        folderPath.setText(parentFolder.getAbsolutePath());
    }

    private void sortFiles()
    {
        Collections.sort(filesList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });

        ArrayList<File> folders = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();

        for (File file : filesList)
        {
            if (file.isDirectory())
            {
                folders.add(file);
            }
            else
            {
                files.add(file);
            }
        }

        filesList = folders;
        filesList.addAll(files);


    }
}
