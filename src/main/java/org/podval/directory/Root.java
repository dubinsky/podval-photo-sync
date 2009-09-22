package org.podval.directory;

import org.podval.things.Crate;
import org.podval.things.Folder;
import org.podval.things.ThingsException;


public class Root extends Crate<Item> {

    public Root(final String rootPath) {
        rootFolder = new Directory(rootPath);
    }


    @Override
    public void open() throws ThingsException {
    }


    @Override
    public Folder<Item> getRootFolder() throws ThingsException {
        return rootFolder;
    }


    private final Directory rootFolder;
}
