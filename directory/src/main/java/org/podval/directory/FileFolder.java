package org.podval.directory;

import org.podval.photo.Folder;
import org.podval.photo.FolderType;
import org.podval.photo.PhotoException;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import java.io.File;


/* package */ final class FileFolder extends Folder<FilePhoto> {

    public FileFolder(final String directoryPath) {
        this(new File(directoryPath));
    }


    public FileFolder(final File directory) {
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
            final FilePhoto item = makeItem(name, bunch);
            items.put(name, item);
        }
    }


    private void loadDirectory(final File subDirectory) {
        subDirectories.put(subDirectory.getName(), new FileFolder(subDirectory));
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


    private FilePhoto makeItem(final String name, final Map<String, File> components) {
        return new FilePhoto(name, components);
    }


    @Override
    public Collection<Folder<FilePhoto>> getFolders() throws PhotoException {
        ensureIsPopulated();
        return sortedValues(subDirectories);
    }


    @Override
    public Folder<FilePhoto> getFolder(final String name) throws PhotoException {
        ensureIsPopulated();
        return subDirectories.get(name);
    }


    @Override
    public FilePhoto getPhoto(final String name) throws PhotoException {
        ensureIsPopulated();
        return items.get(name);
    }


    @Override
    public List<FilePhoto> getPhotos() throws PhotoException {
        ensureIsPopulated();
        return sortedValues(items);
    }


    @Override
    protected void checkFolderType(final FolderType folderType) {
    }


    @Override
    protected Folder<FilePhoto> doCreateFolder(
        final String name, final FolderType folderType) throws PhotoException
    {
        throw new UnsupportedOperationException();
    }


    @Override
    protected Folder<FilePhoto> doCreateFakeFolder(
        final String name, final FolderType folderType) throws PhotoException
    {
        throw new UnsupportedOperationException();
    }


    @Override
    public void doAddFile(final String name, final File file) {
        // @todo implement
        throw new UnsupportedOperationException("Not implemented yet!!!");
    }


    private <T> List<T> sortedValues(final Map<String, T> map) {
        final List<String> keys = new LinkedList<String>(map.keySet());
        final List<T> result = new ArrayList<T>(keys.size());
        Collections.sort(keys);
        for (final String key : keys) {
            result.add(map.get(key));
        }
        return Collections.unmodifiableList(result);
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


    private final Map<String, Folder<FilePhoto>> subDirectories = new HashMap<String, Folder<FilePhoto>>();


    private final Map<String, FilePhoto> items = new HashMap<String, FilePhoto>();
}
