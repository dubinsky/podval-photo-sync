package org.podval.directory;

import org.podval.things.Thing;

import java.util.Map;

import java.io.File;


public final class Item extends Thing {

    /* package */ Item(final String name, final Map<String, File> components) {
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


    private final String name;


    private final Map<String, File> components;
}
