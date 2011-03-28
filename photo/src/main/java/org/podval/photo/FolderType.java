/*
 *  Copyright 2011 Leonid Dubinsky (dub@podval.org).
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.podval.photo;


/**
 *
 * @author dub
 */
public enum FolderType {

    Folders(true, false, "pile"),
    Photos(false, true, "gallery"),
    Mix(true, true, "group");


    private FolderType(
        final boolean canHaveFodlers,
        final boolean canHavePhotos,
        final String name)
    {
        this.canHaveFolders = canHaveFodlers;
        this.canHavePhotos = canHavePhotos;
        this.name = name;
    }


    public final boolean canHaveFolders() {
        return canHaveFolders;
    }


    public final boolean canHavePhotos() {
        return canHavePhotos;
    }


    public void checkCanHaveFolders(final Folder<?, ?> folder) {
        if (!canHaveFolders()) {
            throw new IllegalArgumentException("This folder can not have subfolders: " + folder);
        }
    }


    public void checkCanHavePhotos(final Folder<?, ?> folder) {
        if (!canHavePhotos()) {
            throw new IllegalArgumentException("Folder can not have photos in it: " + folder);
        }
    }


    public void checkNotMixed() {
        if (this == FolderType.Mix) {
            throw new IllegalArgumentException("Mixed directories not supported!");
        }
    }


    @Override
    public String toString() {
        return name;
    }


    private final String name;


    private final boolean canHaveFolders;


    private final boolean canHavePhotos;
}
