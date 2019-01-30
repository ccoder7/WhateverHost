package com.example.markp.whateverhost;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class FileFolderAdapter extends RecyclerView.Adapter<FileFolderAdapter.MyViewHolder> {


    private Context mContext;
    private ArrayList<File> fileList;

    public FileFolderAdapter(Context mContext, ArrayList<File> fileList)
    {
        this.mContext=mContext;
        this.fileList=fileList;
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
        final int position = i;

        myViewHolder.name.setText(fileList.get(position).getName());
        Date lastModDate = new Date(fileList.get(position).lastModified());
        myViewHolder.date.setText(lastModDate.toString());
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView typeImage;
        TextView name;
        TextView date;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            name = itemView.findViewById(R.id.fileFolderName);
            date = itemView.findViewById(R.id.fileFolderDate);
        }
    }
}
