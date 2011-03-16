package org.podval.zenfolio;

import org.podval.things.Thing;
import org.podval.things.Rotation;
import org.podval.things.Indenter;

import com.zenfolio.www.api._1_1.Photo;
import com.zenfolio.www.api._1_1.PhotoRotation;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;


public final class ZenfolioThing extends Thing {

    /* package */ ZenfolioThing(final Photo photo) {
        this.photo = photo;
    }


    @Override
    public String getName() {
        return photo.getFileName();
    }


    public Date getTakenOn() {
        return photo.getTakenOn().getTime();
    }


    public int getSize() {
        return photo.getSize();
    }


    private static final Map<PhotoRotation, Rotation> rotations = new HashMap<PhotoRotation, Rotation>();

    static {
        rotations.put(PhotoRotation.None, Rotation.None);
        rotations.put(PhotoRotation.Flip, Rotation.Flip);
        rotations.put(PhotoRotation.Rotate180, Rotation.Rotate180);
        rotations.put(PhotoRotation.Rotate180Flip, Rotation.Rotate180Flip);
        rotations.put(PhotoRotation.Rotate270, Rotation.Rotate270);
        rotations.put(PhotoRotation.Rotate270Flip, Rotation.Rotate270);
        rotations.put(PhotoRotation.Rotate90, Rotation.Rotate90);
        rotations.put(PhotoRotation.Rotate90Flip, Rotation.Rotate90Flip);
    }


    public Rotation getRotation() {
        return rotations.get(photo.getRotation());
    }


    @Override
    public void list(final Indenter out, final int level) {
        out.println(level,
            "<photo name=\"" + getName() +
            "\" date=\"" + getTakenOn() +
            "\" size=\"" + getSize() +
            "\" rotation=\"" + getRotation() + "\"/>");
    }


    private com.zenfolio.www.api._1_1.Photo photo;
}
