package org.podval.zenfolio;

import org.podval.things.Thing;


public final class ZenfolioThing extends Thing {

    /* package */ ZenfolioThing(final com.zenfolio.www.api._1_1.Photo photo) {
        this.photo = photo;
    }


    @Override
    public String getName() {
        return photo.getFileName();
    }


    private com.zenfolio.www.api._1_1.Photo photo;
}
