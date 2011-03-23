package org.podval.things;


public abstract class Connection<T extends Thing> {

    public abstract String getScheme();


    public abstract void open() throws ThingsException;


    public abstract Folder<T> getRootFolder() throws ThingsException;


    protected final Folder<T> getSubFolderByPath(final Folder<T> folder, final String path) throws ThingsException {
        Folder<T> result = folder;

        if (path != null) {
            for (final String name : path.split("/")) {
                if (!name.isEmpty()) {
                    result.getFolderType().checkCanHaveFolders(result);
                    result = result.getFolder(name);
                }
            }
        }

        return result;
    }
}
