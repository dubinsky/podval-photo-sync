package org.podval.things;


public final class Lister<T extends Thing> {

    public Lister(final Connection connection) {
        this.connection = connection;

        this.out = new Indenter(System.out);
    }


    public void run() throws ThingsException {
        connection.open();

        list(connection.getRootFolder());
    }


    private void list(final Folder<T> folder) throws ThingsException {
        out.println("<folder>");
        out.push();

        out.println("<name>" + folder.getName() + "</name>");

        for (final Folder<T> subFolder : folder.getFolders()) {
            list(subFolder);
        }

        for (final T thing : folder.getThings()) {
            list(thing);
        }

        out.pop();
        out.println("</folder>");
    }


    private void list(final Thing thing) {
        out.println(
            "<photo name=\"" + thing.getName() +
            "\" date=\"" + thing.getTimestamp() +
            "\" size=\"" + thing.getSize() +
            "\" rotation=\"" + thing.getRotation() + "\"" +
            "/>");
    }


    private final Connection connection;


    private final Indenter out;
}
