package org.podval.directory;

import java.util.HashMap;
import java.util.Map;

import java.io.File;


public final class Item {

    /* package */ Item(final String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    /* package */ void add(final String extension, final File file) {
        // @todo detect duplicates?
        components.put(extension, file);
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


    private final Map<String, File> components = new HashMap<String, File>();
}
