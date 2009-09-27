package org.podval.sync;

import java.io.PrintStream;


public final class Indenter {

    public Indenter(final PrintStream out) {
        this.out= out;
    }


    public final void message(final int level, final String line) {
        println(level, "*** " + line);
    }


    public final void println(final int level, final String line) {
        indent(level);
        out.println(line);
    }


    private void indent(final int level) {
        for (int i = 0; i < level; i++) {
            out.print("  ");
        }
    }


    private final PrintStream out;
}
