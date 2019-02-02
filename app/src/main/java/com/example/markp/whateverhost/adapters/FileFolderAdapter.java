package com.example.markp.whateverhost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.markp.whateverhost.fragments.DeviceListFragment;
import com.example.markp.whateverhost.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class FileFolderAdapter extends RecyclerView.Adapter<FileFolderAdapter.MyViewHolder> {


    private Context mContext;
    private ArrayList<File> fileList;
    private DeviceListFragment fragment;

    public FileFolderAdapter(Context mContext, ArrayList<File> fileList, DeviceListFragment fragment)
    {
        this.mContext=mContext;
        this.fileList=fileList;
        this.fragment=fragment;
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

        if (fileList.get(position).isDirectory())
        {
            Glide.with(fragment).load(R.drawable.ic_folder_icon).into(myViewHolder.typeImage);
        }
        else
        {
            String filepath = fileList.get(position).getAbsolutePath();

            if (filepath.contains("."))
            {
                String extension = filepath.substring(filepath.lastIndexOf("."));

                if(extension.equals(".jpeg") || extension.equals(".jpg") || extension.equals(".png"))
                {
                    Glide.with(fragment).load(filepath).into(myViewHolder.typeImage);
                }
                else if (extension.equals(".mp3") || extension.equals(".wav") || extension.equals(".flac"))
                {
                    Glide.with(fragment).load(R.drawable.ic_icons8_music).into(myViewHolder.typeImage);
                }
            }


        }

        myViewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick(position);
            }
        });

        myViewHolder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick(position);
            }
        });

        myViewHolder.typeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick(position);
            }
        });
    }

    public void setFileList(ArrayList<File> fileList)
    {
        this.fileList=fileList;
    }

    private void handleClick(int position)
    {
        File clicked = fileList.get(position);

        if (clicked.isDirectory())
        {
            fragment.setList(clicked);
            this.notifyDataSetChanged();
        }
        else
        {
            fragment.setList(clicked);
        }
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

            typeImage = itemView.findViewById(R.id.fileFolderTypeImage);
            name = itemView.findViewById(R.id.fileFolderName);
            date = itemView.findViewById(R.id.fileFolderDate);
        }
    }
}
