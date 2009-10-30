package org.podval.things;

import java.util.ServiceLoader;


public abstract class CrateFactory<T extends Thing> {

    public static CrateFactory get(final String scheme) {
        CrateFactory result = null;

        if (loader == null) {
            loader = ServiceLoader.load(CrateFactory.class);
        }

        for (final CrateFactory factory : loader) {
            if (factory.getScheme().equals(scheme)) {
                result = factory;
                break;
            }
        }

        return result;
    }


    private static ServiceLoader<CrateFactory> loader;


    public abstract Crate<T> createCrate(final CrateTicket ticket) throws ThingsException;


    public abstract String getScheme();
}
