package org.podval.sync;

import org.podval.things.Crate;
import org.podval.things.Folder;
import org.podval.things.Thing;
import org.podval.things.ThingsException;


public abstract class Processor<T extends Thing> {

    protected Processor(final Crate<T> crate, final String groupPath)
        throws ThingsException
    {
        this.crate = crate;
        this.groupPath = groupPath;
    }


    public final void run() throws ThingsException {
        crate.open();

        run(crate.getFolderByPath(groupPath));
    }


    protected abstract void run(final Folder<T> rootFolder) throws ThingsException;


    protected final void message(final int level, final String line) {
        println(level, "*** " + line);
    }


    protected final void println(final int level, final String line) {
        indent(level);
        System.out.println(line);
    }


    private void indent(final int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
    }


    private Crate<T> crate;


    private final String groupPath;
}
