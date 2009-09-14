package org.podval.directory;

import org.podval.things.Folder;
import org.podval.things.ThingsException;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import java.io.File;


public final class Directory extends Folder<Item> {

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
    }


    @Override
    public String getName() {
        return directory.getName();
    }


    @Override
    protected void populate() {
        final Map<String, Map<String, File>> bunches = new HashMap<String, Map<String, File>>();

        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                loadDirectory(file);
            } else {
                loadFile(file, bunches);
            }
        }

        for (final String name : bunches.keySet()) {
            final Map<String, File> bunch = bunches.get(name);
            final Item item = makeItem(name, bunch);
            items.put(name, item);
        }
    }


    private void loadDirectory(final File subDirectory) {
        subDirectories.put(subDirectory.getName(), new Directory(subDirectory));
    }


    private void loadFile(final File file, final Map<String, Map<String, File>> bunches) {
        final String name = getName(file);

        Map<String, File> bunch = bunches.get(name);
        if (bunch == null) {
            bunch = new HashMap<String, File>();
            // @todo duplicates?
            bunches.put(name, bunch);
        }

        bunch.put(getExtension(file), file);
    }


    private Item makeItem(final String name, final Map<String, File> components) {
        return new Item(name, components);
    }


    @Override
    public Collection<Directory> getSubDirectories() throws ThingsException {
        ensureIsPopulated();
        return sortedValues(subDirectories);
    }


    @Override
    public Directory getSubDirectory(final String name) throws ThingsException {
        ensureIsPopulated();
        return subDirectories.get(name);
    }


    @Override
    public Item getItem(final String name) throws ThingsException {
        ensureIsPopulated();
        return items.get(name);
    }


    @Override
    public List<Item> getItems() throws ThingsException {
        ensureIsPopulated();
        return sortedValues(items);
    }


    @Override
    protected void checkSubDirectoryType(
        final boolean canHaveDirectories,
        final boolean canHaveItems)
    {
    }


    @Override
    public boolean canHaveSubDirectories() {
        return true;
    }


    @Override
    protected Directory doCreateSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws ThingsException
    {
        throw new UnsupportedOperationException();
    }


    @Override
    protected Directory doCreateFakeSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws ThingsException
    {
        throw new UnsupportedOperationException();
    }


    private <T> List<T> sortedValues(final Map<String, T> map) {
        final List<String> keys = new LinkedList<String>(map.keySet());
        final List<T> result = new ArrayList<T>(keys.size());
        Collections.sort(keys);
        for (final String key : keys) {
            result.add(map.get(key));
        }
        return Collections.unmodifiableList(result);
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


    private final Map<String, Directory> subDirectories = new HashMap<String, Directory>();


    private final Map<String, Item> items = new HashMap<String, Item>();
}
