package org.podval.directory;

import org.podval.things.Crate;
import org.podval.things.CrateFactory;
import org.podval.things.CrateTicket;


public final class RootFactory extends CrateFactory<Item> {

    @Override
    public Crate<Item> createCrate(final CrateTicket ticket) {
        return new Root(ticket.path);
    }


    @Override
    public String getScheme() {
        return "file";
    }
}
