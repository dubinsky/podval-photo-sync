package org.podval.things;


public abstract class Thing {

    public abstract String getName();


    @Override
    public final String toString() {
        return getName();
    }
}
