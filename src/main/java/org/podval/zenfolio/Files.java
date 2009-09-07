package org.podval.zenfolio;

import java.io.File;


public final class Files {

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


    public static String getName(final File file) {
        final String filename = file.getName();
        final int dot = filename.lastIndexOf(".");
        return (dot == -1) ? filename : filename.substring(0, dot);
    }


    public static String getExtension(final File file) {
        final String filename = file.getName();
        final int dot = filename.lastIndexOf(".");
        return (dot == -1) ? filename : filename.substring(dot+1);
    }


    public static boolean exists(final File directory, final String name, final String extension) {
        return new File(directory, name + "." + extension).exists();
    }
}
