package org.podval.photo;


public abstract class Connection<P extends Photo> {

    public abstract String getScheme();


    public abstract void open() throws PhotoException;


    public abstract <C extends Connection<P>> Folder<C, P> getRootFolder() throws PhotoException;


    protected final <C extends Connection<P>> Folder<C, P> getSubFolderByPath(final Folder<C, P> folder, final String path) throws PhotoException {
        Folder<C, P> result = folder;

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
