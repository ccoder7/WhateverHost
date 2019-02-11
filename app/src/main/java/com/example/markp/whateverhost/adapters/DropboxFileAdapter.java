package com.example.markp.whateverhost.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.example.markp.whateverhost.MainActivity;
import com.example.markp.whateverhost.R;
import com.example.markp.whateverhost.files.DropboxFile;
import com.example.markp.whateverhost.fragments.DropboxListFragment;
import com.example.markp.whateverhost.tasks.DropboxDownloadTask;

import java.io.File;
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



        final String fileName = myViewHolder.dropboxFileName.getText().toString();

        myViewHolder.dropboxFileName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                PopupMenu popup = new PopupMenu(MainActivity.mainActivity,v);

                popup.getMenu().add("Download");

                popup.getMenu().add("Move");

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId()==0)
                        {
                            Log.d("Downloading","Downloading " + dropboxFiles.get(position).fileName);

                            downloadFile(position);
                        }

                        return true;
                    }
                });

                popup.show();

                return true;
            }
        });

        myViewHolder.dropboxTypeImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.mainActivity,v);

                popup.getMenu().add("Download");

                popup.getMenu().add("Move");

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId()==0)
                        {
                            Log.d("Downloading","Downloading " + dropboxFiles.get(position).fileName);
                            downloadFile(position);

                        }



                        return true;
                    }
                });

                popup.show();

                return true;
            }
        });

        myViewHolder.dropboxFileDate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.mainActivity,v);

                popup.getMenu().add("Download " + dropboxFiles.get(i).fileName);

                popup.getMenu().add("Move");

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId()==0)
                        {
                            Log.d("Downloading","Downloading " + dropboxFiles.get(position).fileName);

                            downloadFile(position);
                        }



                        return true;
                    }
                });


                popup.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dropboxFiles.size();
    }

    private void downloadFile(int position)
    {
        final ProgressDialog dialog = new ProgressDialog(MainActivity.mainActivity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Downloading");
        dialog.show();


        FileMetadata file;

        if (dropboxFiles.get(position).metadata instanceof  FileMetadata)
        {
            file = (FileMetadata)dropboxFiles.get(position).metadata;
        }
        else
        {
            Log.d("Folder","Selected item is not a file");
            dialog.dismiss();
            return;
        }

        new DropboxDownloadTask(MainActivity.mainActivity, MainActivity.mainActivity.client, new DropboxDownloadTask.Callback() {
            @Override
            public void onDownloadComplete(File result)
            {
                dialog.dismiss();

                Toast.makeText(MainActivity.mainActivity,"File downloaded",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Exception e)
            {
                dialog.dismiss();

                Toast.makeText(MainActivity.mainActivity,"Error while downloading",Toast.LENGTH_SHORT).show();

            }
        }).execute(file);
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
