package com.example.markp.whateverhost.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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

import com.bumptech.glide.Glide;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
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

        setImageType(dropboxFiles.get(position), myViewHolder);

        myViewHolder.dropboxFileName.setText(dropboxFiles.get(position).getFileName());
        myViewHolder.dropboxFileDate.setText(dropboxFiles.get(position).getFilePath());

        myViewHolder.dropboxFileDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dropboxFiles.get(position).metadata instanceof FolderMetadata)
                {
                    fragment.updateAdapterPath(dropboxFiles.get(position).getFilePath());
                }
                else
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.mainActivity);

                    alertDialog.setTitle("Download file");

                    alertDialog.setMessage("Do you want to download '" + dropboxFiles.get(position).getFileName() + "'?");

                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            downloadFile(position);

                        }
                    });

                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("Alert","chose not to download");
                        }
                    });

                    alertDialog.create().show();
                }
            }

            });


        myViewHolder.dropboxFileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if folder to navigate to it

                if (dropboxFiles.get(position).metadata instanceof FolderMetadata)
                {
                    fragment.updateAdapterPath(dropboxFiles.get(position).getFilePath());
                }
                else
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.mainActivity);

                    alertDialog.setTitle("Download file");

                    alertDialog.setMessage("Do you want to download '" + dropboxFiles.get(position).getFileName() + "'?");

                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            downloadFile(position);

                    }
                    });

                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("Alert","chose not to download");
                        }
                    });

                    alertDialog.create().show();
                }
            }
        });

        myViewHolder.dropboxTypeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dropboxFiles.get(position).metadata instanceof FolderMetadata)
                {
                    fragment.updateAdapterPath(dropboxFiles.get(position).getFilePath());
                }
                else
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.mainActivity);

                    alertDialog.setTitle("Download file");

                    alertDialog.setMessage("Do you want to download '" + dropboxFiles.get(position).getFileName() + "'?");

                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            downloadFile(position);

                        }
                    });

                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("Alert","chose not to download");
                        }
                    });

                    alertDialog.create().show();
                }
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

                popup.getMenu().add("Delete");

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().toString()=="Download")
                        {
                            downloadFile(position);
                        }
                        else if (item.getTitle().toString()=="Move")
                        {

                        }
                        else if (item.getTitle().toString()=="Delete")
                        {
                            Log.d("Delete","Deleting File");
                            deleteFile(position);
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

                popup.getMenu().add("Delete");

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().toString()=="Download")
                        {
                            downloadFile(position);
                        }
                        else if (item.getTitle().toString()=="Move")
                        {

                        }
                        else if (item.getTitle().toString()=="Delete")
                        {
                            Log.d("Delete","Deleting File");
                            deleteFile(position);
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

                popup.getMenu().add("Delete");

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().toString()=="Download")
                        {
                            downloadFile(position);
                        }
                        else if (item.getTitle().toString()=="Move")
                        {

                        }
                        else if (item.getTitle().toString()=="Delete")
                        {
                            Log.d("Delete","Deleting File");
                            deleteFile(position);
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

    private void setImageType(DropboxFile file, MyViewHolder myViewHolder)
    {
        if (file.metadata instanceof FolderMetadata)
        {
            Glide.with(fragment).load(R.drawable.ic_folder_icon).into(myViewHolder.dropboxTypeImage);
            return;
        }
        if (file.isImage())
        {
            Glide.with(fragment).load(R.drawable.ic_image_icon).into(myViewHolder.dropboxTypeImage);
            return;
        }
        else if (file.isAudio())
        {
            Glide.with(fragment).load(R.drawable.ic_audio_icon).into(myViewHolder.dropboxTypeImage);
            return;
        }
        else if (file.isFileText())
        {
            Glide.with(fragment).load(R.drawable.ic_text_icon).into(myViewHolder.dropboxTypeImage);
            return;
        }
        else if (file.isPdf())
        {
            Glide.with(fragment).load(R.drawable.ic_pdf_icon).into(myViewHolder.dropboxTypeImage);
            return;
        }
        else if (file.isVideo())
        {
            Glide.with(fragment).load(R.drawable.ic_video_icon).into(myViewHolder.dropboxTypeImage);
            return;
        }
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

    private void deleteFile(int position)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.mainActivity);

        alertDialog.setTitle("Deleting file");

        alertDialog.setMessage("Are you sure you want to delete '" + dropboxFiles.get(position).getFileName() + "'?");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                try
                {
                    new AsyncTask<Void,Void,Void>()
                    {
                        @Override
                        protected Void doInBackground(Void... voids)
                        {
                            try{
                                MainActivity.mainActivity.client
                                        .files().deleteV2(dropboxFiles.get(position).getFilePath());
                            }catch (DbxException e)
                            {
                                Log.d("Exception","Exception caught while deleting file");
                            }
                            return null;
                        }

                    }.execute().get();

                    Toast.makeText(MainActivity.mainActivity,"File deleted",Toast.LENGTH_SHORT).show();



                    MainActivity.dropboxListFragment.updateAdapterPath(MainActivity.dropboxListFragment.currentPath);


                }
                catch (Exception e)
                {

                }

                Log.d("Alert","deleting file after alert : "
                        + dropboxFiles.get(position).getFileName());
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Alert","chose not to delete");
            }
        });

        if (dropboxFiles.get(position).metadata instanceof FolderMetadata)
        {
            Toast.makeText(MainActivity.mainActivity,"Cannot delete folder yet",Toast.LENGTH_SHORT).show();
            return;
        }

        alertDialog.create().show();
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
