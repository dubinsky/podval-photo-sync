package org.podval.directory;

import org.podval.photo.ConnectionDescriptor;
import org.podval.photo.Connection;
import org.podval.photo.Folder;
import org.podval.photo.PhotoException;

import java.io.File;


public final class FileConnection extends Connection<FilePhoto> {

    public static final String SCHEME = "file";


    public FileConnection(final ConnectionDescriptor descriptor) {
        rootFolder = new FileFolder(this, new File(descriptor.getPath()));
    }


    @Override
    public String getScheme() {
        return SCHEME;
    }


    @Override
    public void enableLowLevelLogging() {
    }


    @Override
    public void open() throws PhotoException {
    }


    @Override
    public Folder<FileConnection, FilePhoto> getRootFolder() throws PhotoException {
        return rootFolder;
    }


    private final Folder<FileConnection, FilePhoto> rootFolder;
}
