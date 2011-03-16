package org.podval.directory;

import org.podval.things.Thing;
import org.podval.things.Indenter;

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


    public File get(final String extension) {
        return components.get(extension);
    }


    public boolean exists(final String extension) {
        return get(extension) != null;
    }


    public void list(final Indenter out, final int level) {}


    private final String name;


    private final Map<String, File> components;
}
