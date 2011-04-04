package org.podval.directory;

import java.util.Date;
import org.podval.photo.Rotation;
import org.podval.photo.Photo;

import java.util.Map;

import java.io.File;


public final class FilePhoto extends Photo<FileFolder> {

    /* package */ FilePhoto(final FileFolder folder, final String name, final Map<String, File> components) {
        super(folder);

        this.name = name;
        this.components = components;
    }


    @Override
    public String getName() {
        return name;
    }


    @Override
    public Date getTimestamp() {
        return new Date(getOriginalFile().lastModified());
    }


    @Override
    public int getSize() {
        return (int) getOriginalFile().length();
    }


    @Override
    public Rotation getRotation() {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public File getOriginalFile() {
        return get("jpg");
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
