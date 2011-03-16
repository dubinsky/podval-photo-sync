package org.podval.things;


public abstract class Thing {

    public abstract String getName();


    @Override
    public final String toString() {
        return getName();
    }


    public abstract void list(final Indenter out, final int level); // TODO: temporary; for Zenfolio dump...
}
