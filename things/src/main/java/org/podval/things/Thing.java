package org.podval.things;

import java.util.Date;

import java.io.File;
import java.io.IOException;


// TODO: Specialize to Photo
/* This can be generalized to other things - like music.
 * In fact, I even tried it.
 * But let's first deal with what we are dealing with - photos!
 */
public abstract class Thing {

    public abstract String getName();


    public abstract Date getTimestamp();


    public abstract int getSize();


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


    public final <O extends Thing> void addPhoto(
        final Folder<O> toFolder,
        final ThingsConverter<Thing, O> converter,
        final boolean doIt,
        final Indenter out) throws IOException
    {
        final String name = getName();

        // @todo distinguish between "exist" and "available as local file"...
        final File file = converter.toFile(this);
        if (file != null) {
            final String message = ((doIt) ? "adding" : "'adding'") + " thing" + " " + name;
            out.message(message);

            if (doIt) {
                try {
                    toFolder.addFile(file.getName(), file);
                } catch (final ThingsException e) {
                    out.message(e.getMessage());
                }
            }

        } else {
            out.message("Raw conversions are not yet implemented. Can not add " + name);
        }
    }
}
