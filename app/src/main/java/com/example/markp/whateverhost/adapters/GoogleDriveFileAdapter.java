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
import com.example.markp.whateverhost.files.DropboxFile;
import com.example.markp.whateverhost.files.GoogleDriveFile;
import com.example.markp.whateverhost.fragments.DropboxListFragment;
import com.example.markp.whateverhost.fragments.GoogleDriveListFragment;

import java.util.ArrayList;

public class GoogleDriveFileAdapter  extends RecyclerView.Adapter<GoogleDriveFileAdapter.MyViewHolder>
{
    Context mContext;
    ArrayList<GoogleDriveFile> googleDriveFiles;
    GoogleDriveListFragment fragment;

    public GoogleDriveFileAdapter(Context mContext, ArrayList<GoogleDriveFile> googleDriveFiles, GoogleDriveListFragment fragment) {
        this.mContext = mContext;
        this.googleDriveFiles = googleDriveFiles;
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
        myViewHolder.googleDriveFileName.setText(googleDriveFiles.get(i).getFileName());
        myViewHolder.googleDriveFileDate.setText(googleDriveFiles.get(i).getFilePath());
    }

    @Override
    public int getItemCount() {
        return googleDriveFiles.size();
    }


    public static class MyViewHolder extends  RecyclerView.ViewHolder
    {
        ImageView googleDriveTypeImage;
        TextView googleDriveFileName;
        TextView googleDriveFileDate;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            googleDriveTypeImage = itemView.findViewById(R.id.fileFolderTypeImage);
            googleDriveFileName = itemView.findViewById(R.id.fileFolderName);
            googleDriveFileDate = itemView.findViewById(R.id.fileFolderDate);
        }
    }
}
