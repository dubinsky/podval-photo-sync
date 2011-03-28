package org.podval.things;

import java.util.Collection;
import java.util.List;

import java.io.File;
import java.io.IOException;


public abstract class Folder<T extends Photo> {

    public abstract String getName();


    public abstract FolderType getFolderType();


    public abstract boolean isPublic();


    public abstract void setPublic(final boolean value);


    public final boolean hasFolders() throws PhotoException {
        return !getFolders().isEmpty();
    }


    public abstract Collection<Folder<T>> getFolders() throws PhotoException;


    public abstract Folder<T> getFolder(final String name) throws PhotoException;


    public abstract List<T> getThings() throws PhotoException;


    public abstract T getThing(final String name) throws PhotoException;


    public void list(final Indenter out) throws PhotoException {
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


    public <O extends Photo> void syncFolderTo(
        final Folder<O> toFolder,
        final boolean doIt,
        final Indenter out)
        throws PhotoException
    {
        out.println(getName());

        syncProperties(toFolder);
        syncContentTo(toFolder, doIt, out);
        syncFoldersTo(toFolder, doIt, out);

//        syncBackwards(from, to);
    }


    private <O extends Photo> void syncProperties(final Folder<O> toFolder) throws PhotoException {
        toFolder.setPublic(isPublic());

        toFolder.updateIfChanged();
    }


    private <O extends Photo> void syncBackwards(
        final Folder<O> toFolder,
        final Indenter out) throws PhotoException
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


    private <O extends Photo> void syncContentTo(
        final Folder<O> toFolder,
        final boolean doIt,
        final Indenter out) throws PhotoException
    {
        out.push();

        for (final T thing : getThings()) {
            if (hasFolders()) {
                out.message("Skipping " + thing + " on the folder level");
            } else {
                if (toFolder.getThing(thing.getName()) == null) {
                    try {
                        toFolder.addPhoto(thing, doIt, out);
                    } catch (final IOException e) {
                        throw new PhotoException(e);
                    }
                }
            }
        }

        out.pop();
    }


    private <O extends Photo> void addPhoto(
        final O thing,
        final boolean doIt,
        final Indenter out) throws IOException
    {
        final String name = thing.getName();

        // @todo distinguish between "exist" and "available as local file"...
        final File file = thing.getOriginalFile();
        if (file != null) {
            final String message = ((doIt) ? "adding" : "'adding'") + " thing" + " " + name;
            out.message(message);

            if (doIt) {
                try {
                    addFile(file.getName(), file);
                } catch (final PhotoException e) {
                    out.message(e.getMessage());
                }
            }

        } else {
            out.message("Raw conversions are not yet implemented. Can not add " + name);
        }
    }


    private <O extends Photo> void syncFoldersTo(
        final Folder<O> toFolder,
        final boolean doIt,
        final Indenter out) throws PhotoException
    {
        // @todo skip the collections!

        for (final Folder<T> fromSubFolder : getFolders()) {
            final Folder<O> toSubFolder = toFolder.getElementForSubDirectory(fromSubFolder, doIt, out);

            if (toSubFolder != null) {
                out.push();
                fromSubFolder.syncFolderTo(toSubFolder, doIt, out);
                out.pop();
            }
        }
    }


    private <O extends Photo> Folder<T> getElementForSubDirectory(
        final Folder<O> toFolder,
        final boolean doIt,
        final Indenter out) throws PhotoException
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


    protected final void ensureIsPopulated() throws PhotoException {
        if (!isPopulated) {
            populate();
            isPopulated = true;
        }
    }


    protected abstract void populate() throws PhotoException;


    public final Folder<T> createFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        checkFolderCreation(folderType);

        return doCreateFolder(name, folderType);
    }


    public final Folder<T> createFakeFolder(
        final String name,
        final FolderType folderType) throws PhotoException
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
        final FolderType folderType) throws PhotoException;


    protected abstract Folder<T> doCreateFakeFolder(
        final String name,
        final FolderType folderType) throws PhotoException;


    public final void addFile(final String name, final File file) throws PhotoException {
        getFolderType().checkCanHaveFolders(this);

        doAddFile(name, file);
    }


    protected abstract void doAddFile(final String name, final File file) throws PhotoException;


    public abstract void updateIfChanged() throws PhotoException;


    private boolean isPopulated;
}
