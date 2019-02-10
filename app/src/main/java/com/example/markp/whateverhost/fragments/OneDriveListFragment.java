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
import android.widget.Toast;

import com.example.markp.whateverhost.MainActivity;
import com.example.markp.whateverhost.R;
import com.example.markp.whateverhost.adapters.OneDriveFileAdapter;
import com.example.markp.whateverhost.files.OneDriveFile;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.IItemCollectionPage;
import com.onedrive.sdk.extensions.Item;

import java.util.ArrayList;
import java.util.List;

public class OneDriveListFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_onedrive_list, container,false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        //load root folder

        retrieveFiles();
    }

    private void retrieveFiles()
    {
        MainActivity.mainActivity.mOneDriveClient.get().getDrive().getRoot().getChildren().buildRequest().get(new ICallback<IItemCollectionPage>() {
            @Override
            public void success(IItemCollectionPage iItemCollectionPage) {
                List<Item> filesList =  iItemCollectionPage.getCurrentPage();

                MainActivity.mainActivity.oneDriveFiles = new ArrayList<>();

                for (Item item : filesList)
                {
                    Log.d("Item",item.name);

                    MainActivity.mainActivity.oneDriveFiles.add(new OneDriveFile(item.name,item.webUrl));

                    setOneDriveAdapters("root");
                }
            }

            @Override
            public void failure(ClientException ex) {
                Toast.makeText(MainActivity.mainActivity,"Failed while retrieving files.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOneDriveAdapters(String path)
    {
        RecyclerView myRv = ((MainActivity)getActivity()).findViewById(R.id.oneDriveListView);

        OneDriveFileAdapter adapter = new OneDriveFileAdapter(getContext(),((MainActivity)getActivity()).oneDriveFiles,this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRv.setLayoutManager(linearLayoutManager);

        myRv.setAdapter(adapter);

        TextView folderPath = ((MainActivity)getActivity()).findViewById(R.id.currentOneDriveFolderText);

        folderPath.setText(path);
    }
}
