package com.example.markp.whateverhost.fragments;

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

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.markp.whateverhost.MainActivity;
import com.example.markp.whateverhost.R;
import com.example.markp.whateverhost.adapters.DropboxFileAdapter;
import com.example.markp.whateverhost.files.DropboxFile;
import com.example.markp.whateverhost.tasks.DropboxRetrieveTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DropboxListFragment extends Fragment
{
    public String currentPath = null;
    private ArrayList<Boolean> isChecked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drobox_list, container,false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        try
        {
            MainActivity.mainActivity.dropboxFiles = new DropboxRetrieveTask().execute("").get();

            isChecked = new ArrayList<>();

            for (DropboxFile file : MainActivity.mainActivity.dropboxFiles)
            {
                isChecked.add(false);
            }

            sortDropboxFiles();

            setDropboxAdapters("");


        }
        catch (Exception e)
        {

        }

    }

    public void updateAdapterPath(String path)
    {
        try
        {
            MainActivity.mainActivity.dropboxFiles = new DropboxRetrieveTask().execute(path).get();

            isChecked = new ArrayList<>();

            for (DropboxFile file : MainActivity.mainActivity.dropboxFiles)
            {
                isChecked.add(false);
            }

            sortDropboxFiles();

            setDropboxAdapters(path);


        }
        catch (Exception e)
        {
        }
    }

    private void sortDropboxFiles()
    {
        Collections.sort(MainActivity.mainActivity.dropboxFiles, new Comparator<DropboxFile>() {
            @Override
            public int compare(DropboxFile o1, DropboxFile o2) {
                return o1.getFileName().toLowerCase().compareTo(o2.getFileName().toLowerCase());
            }
        });

        ArrayList<DropboxFile> folders = new ArrayList<>();

        ArrayList<DropboxFile> files = new ArrayList<>();

        for (DropboxFile file : MainActivity.mainActivity.dropboxFiles)
        {
            if (file.metadata instanceof FolderMetadata)
            {
                folders.add(file);
            }
            else
            {
                files.add(file);
            }
        }

        MainActivity.mainActivity.dropboxFiles = folders;

        MainActivity.mainActivity.dropboxFiles.addAll(files);
    }

    private void setDropboxAdapters(String path)
    {
        currentPath=path;
        RecyclerView myRv = ((MainActivity)getActivity()).findViewById(R.id.dropboxListView);

        DropboxFileAdapter adapter = new DropboxFileAdapter(getContext(),
                ((MainActivity)getActivity()).dropboxFiles,isChecked,this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRv.setLayoutManager(linearLayoutManager);

        myRv.setAdapter(adapter);

        TextView folderPath = ((MainActivity)getActivity()).findViewById(R.id.currentDropboxFolderText);

        folderPath.setText(path);
    }

    public String getParentFolder()
    {
        String parentFolder;

        parentFolder = currentPath.substring(0, currentPath.lastIndexOf("/"));

        Log.d("Parent",parentFolder);

        return parentFolder;
    }
}
