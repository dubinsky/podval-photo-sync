package org.podval.directory;

import java.util.Date;
import org.podval.things.Rotation;
import org.podval.things.Thing;

import java.util.Map;

import java.io.File;


public final class FileThing extends Thing {

    /* package */ FileThing(final String name, final Map<String, File> components) {
        this.name = name;
        this.components = components;
    }


    @Override
    public String getName() {
        return name;
    }


    @Override
    public Date getTimestamp() {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public int getSize() {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public Rotation getRotation() {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public File get(final String extension) {
        return components.get(extension);
    }


    public boolean exists(final String extension) {
        return get(extension) != null;
    }


    private final String name;


    private final Map<String, File> components;
}
