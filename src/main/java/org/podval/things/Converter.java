package org.podval.things;

import java.io.File;


public interface Converter<L extends Thing, R extends Thing> {

    boolean isConvertible(final R right);


    File toFile(final R right);
}
