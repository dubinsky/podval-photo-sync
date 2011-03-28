package org.podval.directory;

import org.podval.photo.Connection;
import org.podval.photo.Folder;
import org.podval.photo.PhotoException;


/* package */ final class FileConnection extends Connection<FilePhoto> {

    public FileConnection(final String rootPath) {
        rootFolder = new FileFolder(this, rootPath);
    }


    @Override
    public String getScheme() {
        return FileFactory.SCHEME;
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
