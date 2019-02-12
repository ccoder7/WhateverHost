package com.example.markp.whateverhost.files;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;

import java.io.File;

public class DropboxFile extends CustomFile
{

    public Metadata metadata;

    public DropboxFile(String fileName, String filePath, Metadata metadata) {
        super(fileName, filePath);

        this.metadata= metadata;
    }

    public boolean isImage()
    {
        boolean isImage = false;

        String extension = MimeTypeMap.getFileExtensionFromUrl(metadata.getName());

        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        Log.d("Mime",metadata.getName() + " with type : " + type);

        if (type!=null)
        {
            if (type.startsWith("image/"))
            {
                isImage=true;
            }
        }


        return isImage;
    }

    public boolean isAudio()
    {
        boolean isAudio =false;

        String extension = MimeTypeMap.getFileExtensionFromUrl(metadata.getName());

        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        Log.d("Mime",metadata.getName() + " with type : " + type);

        if (type!=null)
        {
            if (type.startsWith("audio/"))
            {
                isAudio=true;
            }
        }


        return isAudio;
    }

    public boolean isVideo()
    {
        boolean isVideo = false;

        String extension = MimeTypeMap.getFileExtensionFromUrl(metadata.getName());

        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        Log.d("Mime",metadata.getName() + " with type : " + type);

        if (type!=null)
        {
            if (type.startsWith("video/"))
            {
                isVideo=true;
            }
        }

        return isVideo;
    }

    public boolean isFileText()
    {
        boolean isFileText = false;

        String extension = MimeTypeMap.getFileExtensionFromUrl(metadata.getName());

        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        Log.d("Mime",metadata.getName() + " with type : " + type);

        if (type!=null)
        {
            if (type.startsWith("text/"))
            {
                isFileText=true;
            }
        }

        return isFileText;
    }

    public boolean isPdf()
    {
        boolean isPdf = false;

        String extension = MimeTypeMap.getFileExtensionFromUrl(metadata.getName());

        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        Log.d("Mime",metadata.getName() + " with type : " + type);

        if (type!=null)
        {
            if (type.startsWith("application/pdf"))
            {
                isPdf=true;
            }
        }

        return isPdf;
    }

}
