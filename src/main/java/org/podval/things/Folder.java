package org.podval.things;

import java.util.Collection;
import java.util.List;


public abstract class Folder<T extends Thing> {

    public abstract String getName();


    public final boolean hasSubDirectories() throws ThingsException {
        return !getSubDirectories().isEmpty();
    }


    public abstract <F extends Folder<T>> Collection<F> getSubDirectories() throws ThingsException;


    public abstract <F extends Folder<T>> F getSubDirectory(final String name) throws ThingsException;


    public abstract List<T> getItems() throws ThingsException;


    public abstract T getItem(final String name) throws ThingsException;


    protected final void ensureIsPopulated() throws ThingsException {
        if (!isPopulated) {
            populate();
            isPopulated = true;
        }
    }


    protected abstract void populate() throws ThingsException;


    public final Folder<T> createSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws ThingsException
    {
        checkCanHaveSubDirectories();
        checkSubDirectoryType(canHaveDirectories, canHaveItems);

        return doCreateSubDirectory(name, canHaveDirectories, canHaveItems);
    }


    public final Folder<T> createFakeSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws ThingsException
    {
        checkCanHaveSubDirectories();
        checkSubDirectoryType(canHaveDirectories, canHaveItems);

        return doCreateFakeSubDirectory(name, canHaveDirectories, canHaveItems);
    }


    private void checkCanHaveSubDirectories() {
        if (!canHaveSubDirectories()) {
            throw new UnsupportedOperationException("This directory can not have subdirectories");
        }
    }


    public abstract boolean canHaveSubDirectories();


    protected abstract void checkSubDirectoryType(
        final boolean canHaveDirectories,
        final boolean canHaveItems);


    protected abstract <F extends Folder<T>> F doCreateSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws ThingsException;


    protected abstract <F extends Folder<T>> F doCreateFakeSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws ThingsException;


    private boolean isPopulated;
}
