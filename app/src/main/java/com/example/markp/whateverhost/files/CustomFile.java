package com.example.markp.whateverhost.files;

public abstract class CustomFile
{
    public String fileName;
    public String filePath;

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFilePath()
    {
        return filePath;
    }

}
