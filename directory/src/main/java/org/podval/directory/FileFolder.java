package org.podval.directory;

import org.podval.photo.Folder;
import org.podval.photo.FolderType;
import org.podval.photo.PhotoException;

import java.util.Map;
import java.util.HashMap;

import java.io.File;


/* package */ final class FileFolder extends Folder<FileConnection, FilePhoto> {

    public FileFolder(final FileConnection connection, final String directoryPath) {
        this(connection, new File(directoryPath));
    }


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
    public void setPublic(final boolean value) {
        // TODO ...
    }


    @Override
    protected void populate() {
        final Map<String, Map<String, File>> bunches = new HashMap<String, Map<String, File>>();

        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                loadDirectory(file);
            } else {
                loadFile(file, bunches);
            }
        }

        for (final Map.Entry<String, Map<String, File>> entry : bunches.entrySet()) {
            final String name = entry.getKey();
            final Map<String, File> bunch = entry.getValue();
            final FilePhoto photo = makePhoto(name, bunch);

            register(photo);
        }
    }


    private void loadDirectory(final File folder) {
        register(new FileFolder(getConnection(), folder));
    }


    private void loadFile(final File file, final Map<String, Map<String, File>> bunches) {
        final String name = getName(file);

        Map<String, File> bunch = bunches.get(name);
        if (bunch == null) {
            bunch = new HashMap<String, File>();
            // @todo duplicates?
            bunches.put(name, bunch);
        }

        bunch.put(getExtension(file), file);
    }


    private FilePhoto makePhoto(final String name, final Map<String, File> components) {
        return new FilePhoto(this, name, components);
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
        // @todo implement
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
