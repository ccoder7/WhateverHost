package com.example.markp.whateverhost.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
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
import com.dropbox.core.v2.files.FileMetadata;
import com.example.markp.whateverhost.MainActivity;
import com.example.markp.whateverhost.fragments.DeviceListFragment;
import com.example.markp.whateverhost.R;
import com.example.markp.whateverhost.tasks.DropboxUploadTask;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.net.URLConnection.guessContentTypeFromName;

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

        myViewHolder.name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                handleLongClick(position,v);


                return true;
            }
        });

        myViewHolder.typeImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                handleLongClick(position,v);

                return true;
            }
        });

        myViewHolder.date.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                handleLongClick(position,v);

                return true;
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
            String type = guessContentTypeFromName(clicked.getName());

            Log.d("Type",type);

            Intent intent = new Intent(Intent.ACTION_VIEW);

            Uri data = FileProvider.getUriForFile(MainActivity.mainActivity,
                    MainActivity.mainActivity.getApplicationContext().getPackageName()
                            + ".my.package.name.provider",clicked);

            intent.setDataAndType(data,type);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try
            {
                MainActivity.mainActivity.startActivity(intent);
            }
            catch (ActivityNotFoundException e)
            {
                Toast.makeText(MainActivity.mainActivity, "No suitable application found", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }

    private void handleLongClick(int position, View v)
    {
        PopupMenu popupMenu = new PopupMenu(MainActivity.mainActivity,v);

        popupMenu.getMenu().add("Upload to Dropbox");

        popupMenu.getMenu().add("Delete");



        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if (item.getTitle().equals("Upload to Dropbox"))
                {
                    uploadFile(position);
                }
                else if (item.getTitle().equals("Delete"))
                {
                    AlertDialog deleteAlert = new AlertDialog.Builder(MainActivity.mainActivity)
                            .setTitle("Deleting file")
                            .setMessage("Are you sure you want to delete '" + fileList.get(position).getName() + "'?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    File parent = fileList.get(position).getParentFile();
                                    fileList.get(position).delete();
                                    fragment.setList(parent);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    deleteAlert.show();
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void uploadFile(int position)
    {
        final ProgressDialog dialog = new ProgressDialog(MainActivity.mainActivity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading");
        dialog.show();


        new DropboxUploadTask(MainActivity.mainActivity, MainActivity.mainActivity.client, new DropboxUploadTask.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {
                dialog.dismiss();

                String message = result.getName() + " size " + result.getSize() + " modified " +
                        DateFormat.getDateTimeInstance().format(result.getClientModified());
                Toast.makeText(MainActivity.mainActivity, message, Toast.LENGTH_SHORT)
                        .show();

                // Reload the folder

            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();

                Log.e("UploadFail", "Failed to upload file.", e);
                Toast.makeText(MainActivity.mainActivity,
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(fileList.get(position).getAbsolutePath(), "");
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
