package org.podval.things;


public final class Lister<T extends Thing> {

    public Lister(final Connection connection) {
        this.connection = connection;

        this.out = new Indenter(System.out);
    }


    public void run() throws ThingsException {
        connection.open();

        list(connection.getRootFolder(), 0);
    }


    private void list(final Folder<T> folder, int level) throws ThingsException {
        out.println(level, "<folder>");
        out.println(level+1, "<name>" + folder.getName() + "</name>");

        final int newLevel = level+1;

        for (final Folder<T> subFolder : folder.getFolders()) {
            list(subFolder, newLevel);
        }

        for (final T thing : folder.getThings()) {
            list(thing, newLevel);
        }

        out.println(level, "</folder>");
    }


    private void list(final Thing thing, final int level) {
        out.println(level,
            "<photo name=\"" + thing.getName() +
            "\" date=\"" + thing.getTimestamp() +
            "\" size=\"" + thing.getSize() +
            "\" rotation=\"" + thing.getRotation() + "\"" +
            "/>");
    }


    private final Connection connection;


    private final Indenter out;
}
