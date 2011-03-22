package org.podval.things;

import java.util.Date;


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
