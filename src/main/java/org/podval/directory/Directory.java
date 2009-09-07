package org.podval.directory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import java.io.File;


public final class Directory {

    public Directory(final String directoryPath) {
        this(new File(directoryPath));
    }


    public Directory(final File directory) {
        if (!directory.exists()) {
            throw new IllegalArgumentException("Does not exist: " + directory);
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + directory);
        }

        this.directory = directory;

        load();
    }


    public String getName() {
        return directory.getName();
    }


    private void load() {
        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                loadDirectory(file);
            } else {
                loadFile(file);
            }
        }
    }


    private void loadDirectory(final File subDirectory) {
        subDirectories.put(subDirectory.getName(), subDirectory);
    }


    private void loadFile(final File file) {
        final String name = getName(file);

        Item item = getItem(name);
        if (item == null) {
            item = new Item(name);
            items.put(name, item);
        }

        item.add(getExtension(file), file);
    }


    public boolean hasSubDirectories() {
        return !subDirectories.isEmpty();
    }


    public Collection<File> getSubDirectories() {
        // @todo enforce immutability?
        return subDirectories.values();
    }


    public File getSubDirectory(final String name) {
        return subDirectories.get(name);
    }


    public Item getItem(final String name) {
        return items.get(name);
    }


    public Collection<Item> getItems() {
        return items.values();
    }


    private String getName(final File file) {
        final String filename = file.getName();
        final int dot = filename.lastIndexOf(".");
        return (dot == -1) ? filename : filename.substring(0, dot);
    }


    private String getExtension(final File file) {
        final String filename = file.getName();
        final int dot = filename.lastIndexOf(".");
        return (dot == -1) ? filename : filename.substring(dot+1);
    }


    private final File directory;


    private final Map<String, File> subDirectories = new HashMap<String, File>();


    private final Map<String, Item> items = new HashMap<String, Item>();
}
