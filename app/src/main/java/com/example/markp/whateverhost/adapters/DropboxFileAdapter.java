package com.example.markp.whateverhost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.markp.whateverhost.R;

import java.util.ArrayList;

public class DropboxFileAdapter extends RecyclerView.Adapter<DropboxFileAdapter.MyViewHolder>
{
    Context mContext;
    ArrayList<String> fileList;
    ArrayList<String> fileDates;
    Fragment fragment;


    public DropboxFileAdapter(Context mContext, ArrayList<String> fileList, ArrayList<String> fileDates, Fragment fragment) {
        this.mContext = mContext;
        this.fileList = fileList;
        this.fileDates = fileDates;
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
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final int position = i;

        myViewHolder.dropboxFileName.setText(fileList.get(position));
        myViewHolder.dropboxFileDate.setText(fileList.get(position));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
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
