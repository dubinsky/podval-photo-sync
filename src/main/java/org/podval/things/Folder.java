package org.podval.things;

import java.util.Collection;
import java.util.List;

import java.io.File;


public abstract class Folder<T extends Thing> {

    public abstract String getName();


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


    public Folder<T> create(
        final String name,
        final boolean canHaveFolders,
        final boolean doIt) throws ThingsException
    {
        return (doIt) ?
            createFolder(name, canHaveFolders, !canHaveFolders) :
            createFakeFolder(name, canHaveFolders, !canHaveFolders);
    }


    public final Folder<T> createFolder(
        final String name,
        final boolean canHaveFolders,
        final boolean canHaveThings) throws ThingsException
    {
        checkCanHaveFolders();
        checkFolderType(canHaveFolders, canHaveThings);

        return doCreateFolder(name, canHaveFolders, canHaveThings);
    }


    public final Folder<T> createFakeFolder(
        final String name,
        final boolean canHaveFolders,
        final boolean canHaveThings) throws ThingsException
    {
        checkCanHaveFolders();
        checkFolderType(canHaveFolders, canHaveThings);

        return doCreateFakeFolder(name, canHaveFolders, canHaveThings);
    }


    private void checkCanHaveFolders() {
        if (!canHaveFolders()) {
            throw new UnsupportedOperationException("This folder can not have subfolders");
        }
    }


    public abstract boolean canHaveFolders();


    private void checkCanHaveThings() {
        if (!canHaveThings()) {
            throw new UnsupportedOperationException("This folder can not have things in it");
        }
    }


    public abstract boolean canHaveThings();


    protected abstract void checkFolderType(
        final boolean canHaveFolders,
        final boolean canHaveThings);


    protected abstract Folder<T> doCreateFolder(
        final String name,
        final boolean canHaveFolders,
        final boolean canHaveThings) throws ThingsException;


    protected abstract Folder<T> doCreateFakeFolder(
        final String name,
        final boolean canHaveFolders,
        final boolean canHaveThings) throws ThingsException;


    public final void addFile(final String name, final File file) throws ThingsException {
        checkCanHaveThings();

        doAddFile(name, file);
    }


    protected abstract void doAddFile(final String name, final File file) throws ThingsException;


    private boolean isPopulated;
}
