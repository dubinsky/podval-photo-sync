package org.podval.zenfolio;

import org.podval.things.Folder;

import java.rmi.RemoteException;

import java.util.Collection;
import java.util.List;


public abstract class ZenfolioDirectory extends Folder {

    @Override
    public abstract Collection<ZenfolioDirectory> getSubDirectories();


    @Override
    public abstract List<Photo> getItems() throws RemoteException;


    public final ZenfolioDirectory createSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws RemoteException
    {
        checkCanHaveSubDirectories();
        checkSubDirectoryType(canHaveDirectories, canHaveItems);

        return doCreateSubDirectory(name, canHaveDirectories, canHaveItems);
    }


    public final ZenfolioDirectory createFakeSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws RemoteException
    {
        checkCanHaveSubDirectories();
        checkSubDirectoryType(canHaveDirectories, canHaveItems);

        return doCreateFakeSubDirectory(name, canHaveDirectories, canHaveItems);
    }


    public abstract boolean canHaveSubDirectories();


    private void checkCanHaveSubDirectories() {
        if (!canHaveSubDirectories()) {
            throw new UnsupportedOperationException("This directory can not have subdirectories");
        }
    }


    protected abstract void checkSubDirectoryType(
        final boolean canHaveDirectories,
        final boolean canHaveItems);


    protected abstract ZenfolioDirectory doCreateSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws RemoteException;


    protected abstract ZenfolioDirectory doCreateFakeSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws RemoteException;
}
