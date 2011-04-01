package org.podval.directory;

import org.podval.photo.Folder;
import org.podval.photo.FolderType;
import org.podval.photo.PhotoException;

import java.util.Map;
import java.util.HashMap;

import java.io.File;


/* package */ final class FileFolder extends Folder<FileConnection, FilePhoto> {

    public FileFolder(final FileConnection connection, final File directory) {
        super(connection);

        if (!directory.exists()) {
            throw new IllegalArgumentException("Does not exist: " + directory);
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + directory);
        }

        this.directory = directory;
    }


    @Override
    public String getName() {
        return directory.getName();
    }


    @Override
    public FolderType getFolderType() {
        return FolderType.Mix;
    }


    @Override
    public boolean isPublic() {
        return true;
    }


    @Override
    public void setPublic(final boolean value) throws PhotoException {
        if (!value) {
            throw new PhotoException("File system folders are always public!");
        }
    }


    @Override
    protected void populate() {
        final Map<String, Map<String, File>> bunches = new HashMap<String, Map<String, File>>();

        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                register(new FileFolder(getConnection(), file));
            } else {
                getBunch(bunches, getName(file)).put(getExtension(file), file);
            }
        }

        for (final String name: bunches.keySet()) {
            register(new FilePhoto(this, name, bunches.get(name)));
        }
    }


    private Map<String, File> getBunch(final Map<String, Map<String, File>> bunches, final String name) {
        Map<String, File> result = bunches.get(name);
        if (result == null) {
            result = new HashMap<String, File>();
            // TODO duplicates?
            bunches.put(name, result);
        }

        return result;
    }


    @Override
    protected void checkFolderType(final FolderType folderType) {
    }


    @Override
    protected FileFolder doCreateFolder(
        final String name, final FolderType folderType) throws PhotoException
    {
        throw new UnsupportedOperationException();
    }


    @Override
    protected FileFolder doCreateFakeFolder(
        final String name, final FolderType folderType) throws PhotoException
    {
        throw new UnsupportedOperationException();
    }


    @Override
    public void doAddFile(final String name, final File file) {
        // TODO implement
        throw new UnsupportedOperationException("Not implemented yet!!!");
    }


    private String getName(final File file) {
        final String filename = file.getName();
        final int dot = filename.lastIndexOf('.');
        return (dot == -1) ? filename : filename.substring(0, dot);
    }


    private String getExtension(final File file) {
        final String filename = file.getName();
        final int dot = filename.lastIndexOf('.');
        return (dot == -1) ? filename : filename.substring(dot+1);
    }


    @Override
    public void updateIfChanged() throws PhotoException {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private final File directory;
}
