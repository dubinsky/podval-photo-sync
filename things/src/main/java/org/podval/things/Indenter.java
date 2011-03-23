package org.podval.things;

import java.io.PrintStream;


public final class Indenter {

    public Indenter(final PrintStream out) {
        this.out= out;
    }


    public void push() {
        level++;
    }


    public void pop() {
        level--;
    }


    public void message(final String line) {
        println("*** " + line);
    }


    public void println(final String line) {
        indent();
        out.println(line);
    }


    private void indent() {
        for (int i = 0; i < level; i++) {
            out.print("  ");
        }
    }


    private final PrintStream out;


    private int level;
}
