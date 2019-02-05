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
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.markp.whateverhost.MainActivity;
import com.example.markp.whateverhost.R;
import com.example.markp.whateverhost.adapters.DropboxFileAdapter;
import com.example.markp.whateverhost.tasks.DropboxRetrieveTask;

import java.util.ArrayList;

public class DropboxListFragment extends Fragment
{

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

            setDropboxAdapters(path);


        }
        catch (Exception e)
        {
            Log.d("Exception","mothafucking exception");
        }
    }

    private void setDropboxAdapters(String path)
    {
        RecyclerView myRv = ((MainActivity)getActivity()).findViewById(R.id.dropboxListView);

        DropboxFileAdapter adapter = new DropboxFileAdapter(getContext(),((MainActivity)getActivity()).dropboxFiles,this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRv.setLayoutManager(linearLayoutManager);

        myRv.setAdapter(adapter);

        TextView folderPath = ((MainActivity)getActivity()).findViewById(R.id.currentDropboxFolderText);

        folderPath.setText(path);
    }

}
