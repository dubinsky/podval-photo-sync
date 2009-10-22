package org.podval.things;


public final class Lister<T extends Thing> {

    public Lister(final Crate crate, final String path) {
        this.crate = crate;
        this.path = path;
        this.out = new Indenter(System.out);
    }


    public void run() throws ThingsException {
        crate.open();
        list(crate.getFolderByPath(path), 0);
    }


    private void list(final Folder<T> folder, int level) throws ThingsException {
        out.println(level, folder.getName());

        level++;

        for (final Folder<T> subFolder : folder.getFolders()) {
            list(subFolder, level);
        }

        for (final T thing : folder.getThings()) {
        }
    }


    private final Crate crate;


    private final String path;


    private final Indenter out;
}
