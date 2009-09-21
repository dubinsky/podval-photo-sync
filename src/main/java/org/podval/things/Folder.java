package org.podval.things;

import java.util.Collection;
import java.util.List;


public abstract class Folder<T extends Thing> {

    public abstract String getName();


    public final boolean hasFolders() throws ThingsException {
        return !getFolders().isEmpty();
    }


    public abstract <F extends Folder<T>> Collection<F> getFolders() throws ThingsException;


    public abstract <F extends Folder<T>> F getFolder(final String name) throws ThingsException;


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


    protected abstract void checkFolderType(
        final boolean canHaveFolders,
        final boolean canHaveThings);


    protected abstract <F extends Folder<T>> F doCreateFolder(
        final String name,
        final boolean canHaveFolders,
        final boolean canHaveThings) throws ThingsException;


    protected abstract <F extends Folder<T>> F doCreateFakeFolder(
        final String name,
        final boolean canHaveFolders,
        final boolean canHaveThings) throws ThingsException;


    private boolean isPopulated;
}
