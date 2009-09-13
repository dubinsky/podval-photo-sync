package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.Photo;

import java.rmi.RemoteException;

import java.util.List;


public abstract class ZenfolioDirectory {

    public abstract String getName();


    public abstract void populate() throws RemoteException;


    public abstract List<ZenfolioDirectory> getSubDirectories();


    public abstract ZenfolioDirectory getSubDirectory(final String name);


    public abstract List<Photo> getItems();


    public abstract Photo getItem(final String name);


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
