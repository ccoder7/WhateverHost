package com.example.markp.whateverhost;

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

import java.io.File;
import java.util.ArrayList;

public class DropboxListFragment extends Fragment
{
    public String path;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drobox_list, container,false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setList("");

        //new DropboxRetrieveTask().execute(this);
    }

    public void setList(String path)
    {
        try
        {
            ListFolderResult result = MainActivity.mainActivity.client.files().listFolder("");

            ArrayList<String> fileList = new ArrayList<>();
            ArrayList<String> fileDate = new ArrayList<>();

            while (true)
            {
                for (Metadata metadata : result.getEntries())
                {
                    fileList.add(metadata.getName());
                    fileDate.add("");

                    RecyclerView myRv = (RecyclerView)((MainActivity)getActivity()).findViewById(R.id.dropboxListView);

                    DropboxFileAdapter adapter = new DropboxFileAdapter(getContext(),fileList,fileDate,this);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager((getActivity()));
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    myRv.setLayoutManager(linearLayoutManager);

                    myRv.setAdapter(adapter);

                    TextView folderPath = getActivity().findViewById(R.id.currentDropboxFolderText);

                    folderPath.setText(metadata.getPathDisplay());
                }
            }



        }
        catch (DbxException e)
        {
            Log.d("2nd","retrieve files exception");
            Log.d("Error 2",e.getRequestId());
        }
    }
}
