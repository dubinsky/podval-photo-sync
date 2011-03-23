package org.podval.things;

import java.util.Collection;
import java.util.List;

import java.io.File;


public abstract class Folder<T extends Thing> {

    public abstract String getName();


    public abstract FolderType getFolderType();


    public abstract boolean isPublic();


    public abstract void setPublic(final boolean value);


    public final boolean hasFolders() throws ThingsException {
        return !getFolders().isEmpty();
    }


    public abstract Collection<Folder<T>> getFolders() throws ThingsException;


    public abstract Folder<T> getFolder(final String name) throws ThingsException;


    public abstract List<T> getThings() throws ThingsException;


    public abstract T getThing(final String name) throws ThingsException;


    protected final void ensureIsPopulated() throws ThingsException {
        if (!isPopulated) {
            populate();
            isPopulated = true;
        }
    }


    protected abstract void populate() throws ThingsException;


    public final Folder<T> createFolder(
        final String name,
        final FolderType folderType) throws ThingsException
    {
        checkFolderCreation(folderType);

        return doCreateFolder(name, folderType);
    }


    public final Folder<T> createFakeFolder(
        final String name,
        final FolderType folderType) throws ThingsException
    {
        checkFolderCreation(folderType);

        return doCreateFakeFolder(name, folderType);
    }


    private void checkFolderCreation(final FolderType folderType) {
        getFolderType().checkCanHaveFolders(this);

        checkFolderType(folderType);
    }


    protected abstract void checkFolderType(final FolderType folderType);


    protected abstract Folder<T> doCreateFolder(
        final String name,
        final FolderType folderType) throws ThingsException;


    protected abstract Folder<T> doCreateFakeFolder(
        final String name,
        final FolderType folderType) throws ThingsException;


    public final void addFile(final String name, final File file) throws ThingsException {
        getFolderType().checkCanHaveFolders(this);

        doAddFile(name, file);
    }


    protected abstract void doAddFile(final String name, final File file) throws ThingsException;


    private boolean isPopulated;
}
