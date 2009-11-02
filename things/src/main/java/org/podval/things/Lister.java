package org.podval.things;


public final class Lister<T extends Thing> {

    public Lister(final Crate crate) {
        this.crate = crate;

        this.out = new Indenter(System.out);
    }


    public void run() throws ThingsException {
        crate.open();

        list(crate.getRootFolder(), 0);
    }


    private void list(final Folder<T> folder, int level) throws ThingsException {
        out.println(level, folder.getName());

        level++;

        for (final Folder<T> subFolder : folder.getFolders()) {
            list(subFolder, level);
        }

//        for (final T thing : folder.getThings()) {
//        }
    }


    private final Crate crate;


    private final Indenter out;
}
