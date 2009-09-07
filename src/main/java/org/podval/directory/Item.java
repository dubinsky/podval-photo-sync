package org.podval.directory;

import java.util.HashMap;
import java.util.Map;

import java.io.File;


public final class Item {

    /* package */ Item(final String name) {
        this.name = name;
    }


    /* package */ void add(final String extension, final File file) {
        // @todo detect duplicates?
        components.put(extension, file);
    }


    private final String name;


    private final Map<String, File> components = new HashMap<String, File>();
}
