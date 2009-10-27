package org.podval.sync;

import org.podval.directory.Item;
import org.podval.things.Converter;
import org.podval.zenfolio.Photo;

import java.io.File;


public final class PhotoConverter implements Converter<Photo, Item> {

    @Override
    public boolean isConvertible(final Item item) {
        return
            item.exists("jpg") ||
            item.exists("crw") ||
            item.exists("cr2") ||
            item.exists("thm");
    }


    @Override
    public File toFile(final Item item) {
        return item.get("jpg");
    }
}
