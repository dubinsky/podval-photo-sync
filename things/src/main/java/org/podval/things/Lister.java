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
        out.println(level, "<folder>");
        out.println(level+1, "<name>" + folder.getName() + "</name>");

        final int newLevel = level+1;

        for (final Folder<T> subFolder : folder.getFolders()) {
            list(subFolder, newLevel);
        }

        for (final T thing : folder.getThings()) {
            thing.list(out, newLevel);
        }

        out.println(level, "</folder>");
    }


    private final Crate crate;


    private final Indenter out;
}
