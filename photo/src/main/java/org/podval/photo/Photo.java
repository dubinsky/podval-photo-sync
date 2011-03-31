package org.podval.photo;

import java.util.Date;

import java.io.File;


/* This can be generalized to other things - like music.
 * In fact, I even tried it.
 * But let's first deal with what we are dealing with - photos!
 */
public abstract class Photo<F extends Folder> implements PhotoId {

    protected Photo(final F folder) {
        this.folder = folder;
    }


    public F getFolder() {
        return folder;
    }


    @Override
    public abstract String getName();


    @Override
    public abstract Date getTimestamp();


    @Override
    public abstract int getSize();


    // TODO when in Scala, move this into the PhotoId trait...
    public final boolean isIdentifiedWith(final PhotoId id) {
        boolean namesEqual = getName().equals(id.getName());
        boolean timestampsEqual = getTimestamp().equals(id.getTimestamp());
        boolean sizesEqual = getSize() == id.getSize();

        return timestampsEqual || (namesEqual && sizesEqual);
    }


    public abstract Rotation getRotation();


    @Override
    public final String toString() {
        return getName();
    }


    public final void list(final Indenter out) {
        out.println(
            "<photo name=\"" + getName() +
            "\" date=\"" + getTimestamp() +
            "\" size=\"" + getSize() +
            "\" rotation=\"" + getRotation() + "\"" +
            "/>");
    }


    public abstract File getOriginalFile();


    private final F folder;
}
