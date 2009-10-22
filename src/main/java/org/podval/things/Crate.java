package org.podval.things;

import java.io.File;


public abstract class Crate<T extends Thing> {

    public abstract void open() throws ThingsException;


    public abstract Folder<T> getRootFolder() throws ThingsException;


    public final Folder<T> getFolderByPath(final String path) throws ThingsException {
        Folder<T> result = getRootFolder();

        if (path != null) {
            for (final String name : path.split("/")) {
                if (!name.isEmpty()) {
                    result = result.getFolder(name);
                    checkCanHaveFolders(result);
                }
            }
        }

        return result;
    }


    private void checkCanHaveFolders(final Folder<T> element) {
        if (!element.canHaveFolders()) {
            throw new IllegalArgumentException("can not have folders: " + element);
        }
    }


    public abstract File toFile(final T thing);
}
