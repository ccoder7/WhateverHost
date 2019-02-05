package com.example.markp.whateverhost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.markp.whateverhost.R;
import com.example.markp.whateverhost.files.DropboxFile;
import com.example.markp.whateverhost.fragments.DropboxListFragment;

import java.util.ArrayList;

public class DropboxFileAdapter extends RecyclerView.Adapter<DropboxFileAdapter.MyViewHolder>
{
    Context mContext;
    ArrayList<DropboxFile> dropboxFiles;
    DropboxListFragment fragment;


    public DropboxFileAdapter(Context mContext, ArrayList<DropboxFile> dropboxFiles, DropboxListFragment fragment) {
        this.mContext = mContext;
        this.fragment = fragment;
        this.dropboxFiles = dropboxFiles;
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
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final int position = i;

        myViewHolder.dropboxFileName.setText(dropboxFiles.get(position).getFileName());
        myViewHolder.dropboxFileDate.setText(dropboxFiles.get(position).getFilePath());

        myViewHolder.dropboxFileDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check if folder to navigate to it



                fragment.updateAdapterPath(dropboxFiles.get(position).getFilePath()+"/Test");

                //if file --> prompt to download

            }
        });

        myViewHolder.dropboxFileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if folder to navigate to it



                fragment.updateAdapterPath(dropboxFiles.get(position).getFilePath());

                //if file --> prompt to download
            }
        });

        myViewHolder.dropboxTypeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if folder to navigate to it



                fragment.updateAdapterPath(dropboxFiles.get(position).getFilePath());

                //if file --> prompt to download
            }
        });
    }

    @Override
    public int getItemCount() {
        return dropboxFiles.size();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder
    {
        ImageView dropboxTypeImage;
        TextView dropboxFileName;
        TextView dropboxFileDate;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            dropboxTypeImage = itemView.findViewById(R.id.fileFolderTypeImage);
            dropboxFileName = itemView.findViewById(R.id.fileFolderName);
            dropboxFileDate = itemView.findViewById(R.id.fileFolderDate);
        }
    }
}
