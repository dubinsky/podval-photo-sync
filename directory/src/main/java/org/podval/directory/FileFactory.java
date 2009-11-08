package org.podval.directory;

import org.podval.things.Crate;
import org.podval.things.CrateFactory;
import org.podval.things.CrateTicket;


public final class FileFactory extends CrateFactory<FileThing> {

    public static final String SCHEME = "file";


    @Override
    public Crate<FileThing> createCrate(final CrateTicket ticket) {
        return new FileCrate(ticket.getPath());
    }


    @Override
    public String getScheme() {
        return SCHEME;
    }
}
