package org.podval.things;

import java.util.ServiceLoader;


public abstract class CrateFactory<T extends Thing> {

    public static Crate getCrate(final CrateTicket ticket) throws ThingsException {
        final String scheme = ticket.scheme;

        final CrateFactory crateFactory = get(scheme);

        if (crateFactory == null) {
            throw new ThingsException("Unknown scheme: " + scheme);
        }

        return crateFactory.createCrate(ticket);
    }


    public static CrateFactory get(final String scheme) {
        CrateFactory result = null;

        for (final CrateFactory factory : getLoader()) {
            if (factory.getScheme().equals(scheme)) {
                result = factory;
                break;
            }
        }

        return result;
    }


    private static synchronized ServiceLoader<CrateFactory> getLoader() {
        if (loader == null) {
            loader = ServiceLoader.load(CrateFactory.class);
        }

        return loader;
    }


    private static ServiceLoader<CrateFactory> loader;


    public abstract Crate<T> createCrate(final CrateTicket ticket) throws ThingsException;


    public abstract String getScheme();
}
