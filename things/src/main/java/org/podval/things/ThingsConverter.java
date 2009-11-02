package org.podval.things;

import java.io.File;
import java.util.ServiceLoader;


public abstract class ThingsConverter<F extends Thing, T extends Thing> {

    public static ThingsConverter get(final String fromScheme, final String toScheme) {
        ThingsConverter result = null;

        for (final ThingsConverter converter : getLoader()) {
            if (converter.getFromScheme().equals(fromScheme) &&
                converter.getToScheme().equals(toScheme))
            {
                result = converter;
                break;
            }
        }

        return result;
    }


    private static synchronized ServiceLoader<ThingsConverter> getLoader() {
        if (loader == null) {
            loader = ServiceLoader.load(ThingsConverter.class);
        }

        return loader;
    }


    private static ServiceLoader<ThingsConverter> loader;


    public abstract String getFromScheme();


    public abstract String getToScheme();


    public abstract boolean isConvertible(final F from);


    public abstract File toFile(final F from);
}
