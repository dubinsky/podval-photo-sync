package org.podval.directory;

import java.util.Map;
import java.util.Comparator;

import java.io.File;


public final class Item {

    /* package */ Item(final String name, final Map<String, File> components) {
        this.name = name;
        this.components = components;
    }


    public String getName() {
        return name;
    }


    public File get(final String extension) {
        return components.get(extension);
    }


    public boolean exists(final String extension) {
        return get(extension) != null;
    }


    @Override
    public String toString() {
        return getName();
    }


    private final String name;


    private final Map<String, File> components;
}
