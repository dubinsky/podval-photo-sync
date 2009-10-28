package org.podval.things;

import java.io.File;


public interface Converter<F extends Thing, T extends Thing> {

    boolean isConvertible(final F right);


    File toFile(final F right);
}
