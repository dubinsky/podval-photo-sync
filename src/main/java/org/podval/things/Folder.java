package org.podval.things;

import java.rmi.RemoteException;

import java.util.Collection;
import java.util.List;


public abstract class Folder {

    public abstract String getName();


    protected abstract void populate() throws RemoteException;


    public abstract boolean hasSubDirectories();


    public abstract <S extends Folder> Collection<S> getSubDirectories();


    public abstract <S extends Folder> S getSubDirectory(final String name);


    public abstract <S extends Thing> S getItem(final String name) throws RemoteException;


    public abstract List<? extends Thing> getItems() throws RemoteException;
}
