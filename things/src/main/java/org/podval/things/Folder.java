package org.podval.things;

import java.util.Collection;
import java.util.List;

import java.io.File;
import java.io.IOException;


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


    public void list(final Indenter out) throws ThingsException {
        out.println("<folder>");
        out.push();

        out.println("<name>" + getName() + "</name>");

        for (final Folder<T> subFolder : getFolders()) {
            subFolder.list(out);
        }

        for (final T thing : getThings()) {
            thing.list(out);
        }

        out.pop();
        out.println("</folder>");
    }


    public <O extends Thing> void syncFolderTo(
        final Folder<O> toFolder,
        final ThingsConverter<T, O> converter,
        final boolean doIt,
        final Indenter out)
        throws ThingsException
    {
        out.println(getName());

        syncProperties(toFolder);
        syncContentTo(toFolder, converter, doIt, out);
        syncFoldersTo(toFolder, converter, doIt, out);

//        syncBackwards(from, to);
    }


    private <O extends Thing> void syncProperties(final Folder<O> toFolder) throws ThingsException {
        toFolder.setPublic(isPublic());

        toFolder.updateIfChanged();
    }


    private <O extends Thing> void syncBackwards(
        final Folder<O> toFolder,
        final Indenter out) throws ThingsException
    {
        out.push();

        for (final Folder<O> toSubFolder : toFolder.getFolders()) {
            final String name = toSubFolder.getName();
            final Folder<T> fromSubFolder = getFolder(name);
            if (fromSubFolder == null) {
                out.message("No file for the element: " + name);
            }
        }

        out.pop();
    }


    private <O extends Thing> void syncContentTo(
        final Folder<O> toFolder,
        final ThingsConverter<T, O> converter,
        final boolean doIt,
        final Indenter out) throws ThingsException
    {
        out.push();

        for (final T fromThing : getThings()) {
            if (hasFolders()) {
                out.message("Skipping " + fromThing + " on the folder level");
            } else {
                if (!converter.isConvertible(fromThing)) {
                    out.message("Skipping non-convertible " + fromThing + " on the folder level");

                } else {
                    if (toFolder.getThing(converter.getName(fromThing)) == null) {
                        try {
                            fromThing.addPhoto(toFolder, converter, doIt, out);
                        } catch (final IOException e) {
                            throw new ThingsException(e);
                        }
                    }
                }
            }
        }

        out.pop();
    }


    private <O extends Thing> void syncFoldersTo(
        final Folder<O> toFolder,
        final ThingsConverter<T, O> converter,
        final boolean doIt,
        final Indenter out) throws ThingsException
    {
        // @todo skip the collections!

        for (final Folder<T> fromSubFolder : getFolders()) {
            final Folder<O> toSubFolder = toFolder.getElementForSubDirectory(fromSubFolder, doIt, out);

            if (toSubFolder != null) {
                out.push();
                fromSubFolder.syncFolderTo(toSubFolder, converter, doIt, out);
                out.pop();
            }
        }
    }


    private <O extends Thing> Folder<T> getElementForSubDirectory(
        final Folder<O> toFolder,
        final boolean doIt,
        final Indenter out) throws ThingsException
    {
        Folder<T> result = null;

        final String name = getName();

        final boolean shouldHaveFolders = hasFolders();

        final FolderType folderType = (shouldHaveFolders) ?
            FolderType.Folders :
            FolderType.Things;

        Folder<T> toSubFolder = getFolder(name);

        if (toSubFolder == null) {
            final String message =
                ((doIt) ? "creating" : "'creating'") + " " +
                folderType + " " + name;

            out.message(message);

            // TODO: set properties when creating, so that we do not have to update it immediately!
            toSubFolder = (doIt) ?
                createFolder(name, folderType) :
                createFakeFolder(name, folderType);
        }

        final boolean canHaveFolders = toSubFolder.getFolderType().canHaveFolders();

        if (canHaveFolders && !shouldHaveFolders) {
            out.message("Can have sub-folders, but should't: " + name);
        } if (!canHaveFolders && shouldHaveFolders) {
            out.message("Can't have sub-folders, but should: " + name);
        } else {
            result = toSubFolder;
        }

        return result;
    }


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


    public abstract void updateIfChanged() throws ThingsException;


    private boolean isPopulated;
}
