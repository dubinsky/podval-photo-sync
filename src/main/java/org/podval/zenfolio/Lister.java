package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.Group;
import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.ArrayOfChoice1Choice;

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
    protected void run(final Group rootGroup) {
        list(rootGroup, 0);
    }


    private void list(final Group group, int level) {
        println(level, group.getTitle());

        level++;

        final ArrayOfChoice1Choice[] elements = getZenfolio().getElements(group);
        if (elements != null) {
            for (final ArrayOfChoice1Choice element : elements) {
                final Group subGroup = element.getGroup();
                if (subGroup != null) {
                    list(subGroup, level);
                } else {
                    list(element.getPhotoSet(), level);
                }
            }
        }
    }


    private void list(final PhotoSet set, final int level) {
        println(level, set.getTitle());
    }
}
