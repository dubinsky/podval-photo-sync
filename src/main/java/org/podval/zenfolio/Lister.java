package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.Photo;
import java.rmi.RemoteException;


public final class Lister extends Processor {

    public Lister(
        final String login,
        final String password,
        final String groupPath) throws RemoteException
    {
        super(login, password, groupPath);
    }


    @Override
    protected void run(final ZenfolioDirectory rootDirectory) {
        list(rootDirectory, 0);
    }


    private void list(final ZenfolioDirectory directory, int level) {
        println(level, directory.getName());

        level++;

        for (final ZenfolioDirectory subDirectory : directory.getSubDirectories()) {
            list(subDirectory, level);
        }

        for (final Photo photo : directory.getItems()) {
        }
    }
}
