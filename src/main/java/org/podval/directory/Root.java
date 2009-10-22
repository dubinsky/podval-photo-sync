package org.podval.directory;

import org.podval.things.Crate;
import org.podval.things.Folder;
import org.podval.things.ThingsException;

import java.io.File;


public class Root extends Crate<Item> {

    public Root(final String rootPath, final String defaultExtension) {
        rootFolder = new Directory(rootPath);
        this.defaultExtension = defaultExtension;
    }


    @Override
    public void open() throws ThingsException {
    }


    @Override
    public Folder<Item> getRootFolder() throws ThingsException {
        return rootFolder;
    }


    @Override
    public File toFile(final Item thing) {
        return thing.get(defaultExtension);
    }


    private final Folder<Item> rootFolder;


    private final String defaultExtension;
}
