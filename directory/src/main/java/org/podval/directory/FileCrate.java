package org.podval.directory;

import org.podval.things.Crate;
import org.podval.things.Folder;
import org.podval.things.ThingsException;


/* package */ final class FileCrate extends Crate<FileThing> {

    public FileCrate(final String rootPath) {
        rootFolder = new FileFolder(rootPath);
    }


    @Override
    public String getScheme() {
        return FileFactory.SCHEME;
    }


    @Override
    public void open() throws ThingsException {
    }


    @Override
    public Folder<FileThing> getRootFolder() throws ThingsException {
        return rootFolder;
    }


    private final Folder<FileThing> rootFolder;
}
