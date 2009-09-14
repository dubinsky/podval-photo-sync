package org.podval.things;


public abstract class Thing {

    public abstract String getName();


    @Override
    public String toString() {
        return getName();
    }
}
