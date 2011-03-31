package org.podval.photo;

import java.util.List;
import java.util.LinkedList;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;


public abstract class Folder<C extends Connection<P>, P extends Photo> {

    protected Folder(final C connection) {
        this.connection = connection;
    }


    public final C getConnection() {
        return connection;
    }


    public abstract String getName();


    // TODO when in Scala, encode the ability to have folders/photos in traits...
    public abstract FolderType getFolderType();


    public abstract boolean isPublic();


    public abstract void setPublic(final boolean value);


    public final boolean hasFolders() throws PhotoException {
        return !getFolders().isEmpty();
    }


    public final List<? extends Folder<C, P>> getFolders() throws PhotoException {
        ensureIsPopulated();
        // TODO sort?
        // TODO immute?

        return folders;
    }


    public final Folder<C, P> getFolder(final String name) throws PhotoException {
        return (getFolderType().canHaveFolders()) ? findFolder(name) : null;
    }


    private Folder<C, P> findFolder(final String name) throws PhotoException {
        Folder<C, P> result = null;

        for (final Folder<C, P> folder : getFolders()) {
            if (folder.getName().equals(name)) {
                result = folder;
                break;
            }
        }

        return result;
    }


    public final boolean hasPhotos() throws PhotoException {
        return !getPhotos().isEmpty();
    }


    public final List<P> getPhotos() throws PhotoException {
        ensureIsPopulated();
        // TODO sort?
        // TODO immute?
        return photos;
    }


    public final List<P> getPhotos(final PhotoId id) throws PhotoException {
        final List<P> result = new LinkedList<P>();

        for (final P photo: getPhotos()) {
            if (photo.isIdentifiedWith(id)) {
                result.add(photo);
            }
        }

        for (final Folder<C, P> folder: getFolders()) {
            result.addAll(folder.getPhotos(id));
        }

        return result;
    }


//    private <T> List<T> sortedValues(final Map<String, T> map) {
//        final List<String> keys = new LinkedList<String>(map.keySet());
//        final List<T> result = new ArrayList<T>(keys.size());
//        Collections.sort(keys);
//        for (final String key : keys) {
//            result.add(map.get(key));
//        }
//        return Collections.unmodifiableList(result);
//    }


    public final P getPhoto(final String name) throws PhotoException {
        return (getFolderType().canHavePhotos()) ? findPhoto(name) : null;
    }


    private P findPhoto(final String name) throws PhotoException {
        P result = null;

        for (final P photo : getPhotos()) {
            if (photo.getName().equals(name)) {
                result = photo;
                break;
            }
        }

        return result;
    }


    public void list(final Indenter out) throws PhotoException {
        out.println("<folder>");
        out.push();

        out.println("<name>" + getName() + "</name>");

        for (final Folder<C, P> subFolder : getFolders()) {
            subFolder.list(out);
        }

        for (final P photo : getPhotos()) {
            photo.list(out);
        }

        out.pop();
        out.println("</folder>");
    }


    public <D extends Connection<O>, O extends Photo> void syncFolderTo(
        final Folder<D, O> toFolder)
        throws PhotoException
    {
        getLog().debug("Synchronizing " + getName());

        syncProperties(toFolder);
        syncContentTo(toFolder);
        syncFoldersTo(toFolder);

//        syncBackwards(from, to);
    }


    private <D extends Connection<O>, O extends Photo> void syncProperties(final Folder<D, O> toFolder) throws PhotoException {
        toFolder.setPublic(isPublic());

        toFolder.updateIfChanged();
    }


    private <D extends Connection<O>, O extends Photo> void syncBackwards(
        final Folder<D, O> toFolder) throws PhotoException
    {
        for (final Folder<D, O> toSubFolder : toFolder.getFolders()) {
            final String name = toSubFolder.getName();
            final Folder<C, P> fromSubFolder = getFolder(name);
            if (fromSubFolder == null) {
                getLog().info("No file for the element: " + name);
            }
        }
    }


    private <D extends Connection<O>, O extends Photo> void syncContentTo(
        final Folder<D, O> toFolder) throws PhotoException
    {
        for (final P photo : getPhotos()) {
            if (hasFolders()) {
                getLog().info("Skipping " + photo + " on the folder level");
            } else {
                if (toFolder.getPhoto(photo.getName()) == null) {
                    try {
                        toFolder.addPhoto(photo);
                    } catch (final IOException e) {
                        throw new PhotoException(e);
                    }
                }
            }
        }
    }


    private <O extends Photo> void addPhoto(final O photo) throws IOException {
        final boolean doIt = !getConnection().isReadOnly();

        final String name = photo.getName();

        // TODO distinguish between "exists" and "available as local file"...
        final File file = photo.getOriginalFile();
        if (file != null) {
            final String message = ((doIt) ? "adding" : "'adding'") + " photo" + " " + name;
            getLog().debug(message);

            if (doIt) {
                try {
                    addFile(file.getName(), file);
                } catch (final PhotoException e) {
                    getLog().error(e.getMessage());
                }
            }

        } else {
            getLog().info("Raw conversions are not yet implemented. Can not add " + name);
        }
    }


    private <D extends Connection<O>, O extends Photo> void syncFoldersTo(
        final Folder<D, O> toFolder) throws PhotoException
    {
        // @todo skip the collections!

        for (final Folder<C, P> fromSubFolder : getFolders()) {
            final Folder<D, O> toSubFolder = toFolder.getElementForSubDirectory(fromSubFolder);

            if (toSubFolder != null) {
                fromSubFolder.syncFolderTo(toSubFolder);
            }
        }
    }


    private <D extends Connection<O>, O extends Photo> Folder<C, P> getElementForSubDirectory(
        final Folder<D, O> toFolder) throws PhotoException
    {
        Folder<C, P> result = null;

        final boolean doIt = !getConnection().isReadOnly();

        final String name = toFolder.getName();

        final boolean shouldHaveFolders = toFolder.hasFolders();

        final FolderType folderType = (shouldHaveFolders) ?
            FolderType.Folders :
            FolderType.Photos;

        Folder<C, P> toSubFolder = getFolder(name);

        if (toSubFolder == null) {
            final String message =
                ((doIt) ? "creating" : "'creating'") + " " +
                folderType + " " + name;

            getLog().debug(message);

            // TODO: set properties when creating, so that we do not have to update it immediately!
            toSubFolder = (doIt) ?
                createFolder(name, folderType) :
                createFakeFolder(name, folderType);
        }

        final boolean canHaveFolders = toSubFolder.getFolderType().canHaveFolders();

        if (canHaveFolders && !shouldHaveFolders) {
            getLog().info("Can have folders, but should't: " + name);
        } if (!canHaveFolders && shouldHaveFolders) {
            getLog().info("Can't have folders, but should: " + name);
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


    // TODO when in Scala, change populate()'s signature to return a pair of
    // collections - for folders and for photos...
    protected abstract void populate() throws PhotoException;


    protected final void register(final P photo) {
        // TODO Map<String, P> name2photo?
        photos.add(photo);
    }


    protected final void register(final  Folder<C, P> folder) {
        // TODO Map<String, P> name2folder?
        folders.add(folder);
    }


    public final Folder<C, P> createFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        checkFolderCreation(folderType);

        return doCreateFolder(name, folderType);
    }


    public final Folder<C, P> createFakeFolder(
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


    protected abstract Folder<C, P> doCreateFolder(
        final String name,
        final FolderType folderType) throws PhotoException;


    protected abstract Folder<C, P> doCreateFakeFolder(
        final String name,
        final FolderType folderType) throws PhotoException;


    public final void addFile(final String name, final File file) throws PhotoException {
        getFolderType().checkCanHaveFolders(this);

        doAddFile(name, file);
    }


    protected abstract void doAddFile(final String name, final File file) throws PhotoException;


    public abstract void updateIfChanged() throws PhotoException;


    private Log getLog() {
        return LogFactory.getLog(Connection.LOG);
    }


    private final C connection;


    private final List<Folder<C, P>> folders = new LinkedList<Folder<C, P>>();


    private final List<P> photos = new LinkedList<P>();


    private boolean isPopulated;
}
