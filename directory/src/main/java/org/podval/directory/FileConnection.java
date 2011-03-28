package org.podval.directory;

import org.podval.things.Connection;
import org.podval.things.Folder;
import org.podval.things.PhotoException;


/* package */ final class FileConnection extends Connection<FilePhoto> {

    public FileConnection(final String rootPath) {
        rootFolder = new FileFolder(rootPath);
    }


    @Override
    public String getScheme() {
        return FileFactory.SCHEME;
    }


    @Override
    public void open() throws PhotoException {
    }


    @Override
    public Folder<FilePhoto> getRootFolder() throws PhotoException {
        return rootFolder;
    }


    private final Folder<FilePhoto> rootFolder;
}
