package org.podval.directory;

import java.util.Date;
import org.podval.photo.Rotation;
import org.podval.photo.Photo;

import java.util.Map;

import java.io.File;


public final class FilePhoto extends Photo {

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


    @Override
    public File getOriginalFile() {
        return get("jpg");
    }


    public boolean exists(final String extension) {
        return get(extension) != null;
    }


    private final String name;


    private final Map<String, File> components;
}
