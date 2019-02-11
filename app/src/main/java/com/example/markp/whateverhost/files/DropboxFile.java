package com.example.markp.whateverhost.files;

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
}
