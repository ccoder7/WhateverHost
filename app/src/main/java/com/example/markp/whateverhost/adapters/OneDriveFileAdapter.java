package com.example.markp.whateverhost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.markp.whateverhost.R;
import com.example.markp.whateverhost.files.GoogleDriveFile;
import com.example.markp.whateverhost.files.OneDriveFile;
import com.example.markp.whateverhost.fragments.GoogleDriveListFragment;
import com.example.markp.whateverhost.fragments.OneDriveListFragment;

import java.util.ArrayList;

public class OneDriveFileAdapter extends RecyclerView.Adapter<OneDriveFileAdapter.MyViewHolder>
{
    Context mContext;
    ArrayList<OneDriveFile> oneDriveFiles;
    OneDriveListFragment fragment;

    public OneDriveFileAdapter(Context mContext, ArrayList<OneDriveFile> oneDriveFiles, OneDriveListFragment fragment) {
        this.mContext = mContext;
        this.oneDriveFiles = oneDriveFiles;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_file_folder,viewGroup,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i)
    {
        myViewHolder.oneDriveFileName.setText(oneDriveFiles.get(i).getFileName());
        myViewHolder.oneDriveFileDate.setText(oneDriveFiles.get(i).getFilePath());
    }

    @Override
    public int getItemCount() {
        return oneDriveFiles.size();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder
    {
        ImageView oneDriveTypeImage;
        TextView oneDriveFileName;
        TextView oneDriveFileDate;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            oneDriveTypeImage = itemView.findViewById(R.id.fileFolderTypeImage);
            oneDriveFileName = itemView.findViewById(R.id.fileFolderName);
            oneDriveFileDate = itemView.findViewById(R.id.fileFolderDate);
        }
    }
}
