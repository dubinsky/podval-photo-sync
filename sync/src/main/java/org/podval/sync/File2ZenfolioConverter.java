package org.podval.sync;

import org.podval.things.ThingsConverter;

import org.podval.directory.FileFactory;
import org.podval.directory.FileThing;

import org.podval.zenfolio.ZenfolioFactory;
import org.podval.zenfolio.ZenfolioThing;

import java.io.File;


public final class File2ZenfolioConverter extends ThingsConverter<FileThing, ZenfolioThing> {

    @Override
    public String getFromScheme() {
        return FileFactory.SCHEME;
    }


    @Override
    public String getToScheme() {
        return ZenfolioFactory.SCHEME;
    }


    @Override
    public boolean isConvertible(final FileThing item) {
        return
            item.exists("jpg") ||
            item.exists("crw") ||
            item.exists("cr2") ||
            item.exists("thm");
    }


    @Override
    public String getName(final FileThing from) {
        return from.getName() + ".jpg";
    }


    @Override
    public File toFile(final FileThing item) {
        return item.get("jpg");
    }
}
