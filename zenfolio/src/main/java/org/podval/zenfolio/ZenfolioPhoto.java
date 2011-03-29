package org.podval.zenfolio;

import org.podval.photo.Photo;
import org.podval.photo.Rotation;

import com.zenfolio.www.api._1_1.PhotoRotation;

import java.io.File;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;


public final class ZenfolioPhoto extends Photo {

    // I do not deal with photos in the Groups; just in the Galleries.
    /* package */ ZenfolioPhoto(final Gallery folder, final com.zenfolio.www.api._1_1.Photo photo) {
        super(folder);

        this.photo = photo;
    }


    @Override
    public String getName() {
        return photo.getFileName();
    }


    @Override
    public Date getTimestamp() {
        return photo.getTakenOn().getTime();
    }


    @Override
    public int getSize() {
        return photo.getSize();
    }


    private static final Map<PhotoRotation, Rotation> rotations = new HashMap<PhotoRotation, Rotation>();

    static {
        rotations.put(PhotoRotation.None, Rotation.None);
//        rotations.put(PhotoRotation.Flip, Rotation.Flip);
        rotations.put(PhotoRotation.Rotate180, Rotation.R180);
//        rotations.put(PhotoRotation.Rotate180Flip, Rotation.Rotate180Flip);
        rotations.put(PhotoRotation.Rotate270, Rotation.Left);
//        rotations.put(PhotoRotation.Rotate270Flip, Rotation.Rotate270);
        rotations.put(PhotoRotation.Rotate90, Rotation.Right);
//        rotations.put(PhotoRotation.Rotate90Flip, Rotation.Rotate90Flip);
    }


    @Override
    public Rotation getRotation() {
        final Rotation result = rotations.get(photo.getRotation());
        // TODO: deal with null...
        return result;
    }


    @Override
    public File getOriginalFile() {
        // TODO: download
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private com.zenfolio.www.api._1_1.Photo photo;
}
