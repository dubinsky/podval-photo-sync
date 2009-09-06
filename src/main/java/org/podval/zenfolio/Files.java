package org.podval.zenfolio;

import java.io.File;


public final class Files {

    public static File getDirectory(final String path) {
        final File result;

        if (path != null) {
            result = new File(path);

            if (!result.isDirectory()) {
                throw new IllegalArgumentException("Not a directory: " + result);
            }
        } else {
            result = null;
        }

        return result;
    }


    public static boolean hasSubDirectories(final File directory) {
        boolean result = false;

        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                result = true;
                break;
            }
        }

        return result;
    }
}
