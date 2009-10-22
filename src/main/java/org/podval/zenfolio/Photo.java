package org.podval.zenfolio;

import org.podval.things.Thing;


public final class Photo extends Thing {

    /* package */ Photo(final Zenfolio zenfolio, final com.zenfolio.www.api._1_1.Photo photo) {
        this.zenfolio = zenfolio;
        this.photo = photo;
    }


    @Override
    public String getName() {
        return photo.getFileName();
    }


    private final Zenfolio zenfolio;


    private com.zenfolio.www.api._1_1.Photo photo;
}
