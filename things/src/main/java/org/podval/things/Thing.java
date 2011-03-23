package org.podval.things;

import java.util.Date;


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
}
