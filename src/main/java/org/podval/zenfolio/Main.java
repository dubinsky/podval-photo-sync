package org.podval.zenfolio;


public final class Main {

    public static void main(final String[] args) throws Exception {
        final String login = args[0];
        final String password = getArg(args, 1);
        String groupPath =  getArg(args, 2);
        String path =  getArg(args, 3);
        final String suffix = getArg(args, 4);

        if (suffix != null) {
            groupPath += suffix;
            path += suffix;
        }

        final Processor processor = (path == null) ?
            new Lister(login, password, groupPath) :
            new Synchronizer(login, password, groupPath, path, false);

        processor.run();
    }


    private static String getArg(final String[] args, final int n) {
        return (args.length > n) ? args[n] : null;
    }
}
