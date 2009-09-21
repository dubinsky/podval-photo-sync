package org.podval.zenfolio;

import org.podval.things.Folder;
import org.podval.things.ThingsException;

import com.zenfolio.www.api._1_1.PhotoSet;

import java.rmi.RemoteException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import java.util.List;
import java.util.LinkedList;
import java.util.Date;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public final class Gallery extends Folder<Photo> {

    public Gallery(final Zenfolio zenfolio, final PhotoSet photoSet) {
        this.zenfolio = zenfolio;
        this.photoSet = photoSet;
    }


    @Override
    public String getName() {
        return photoSet.getTitle();
    }


    @Override
    protected void populate() throws ThingsException {
        // PhotoSet needs to be loaded, since in the "structure" it is not populated with the Photos.

        final int id = photoSet.getId();

        if (id != 0) {
            try {
                photoSet = zenfolio.getConnection().loadPhotoSet(id);
            } catch (final RemoteException e) {
                throw new ThingsException(e);
            }

            if ((photoSet.getPhotos() != null) && (photoSet.getPhotos().getPhoto() != null)) {
                for (final com.zenfolio.www.api._1_1.Photo rawPhoto : photoSet.getPhotos().getPhoto()) {
                    final Photo photo = new Photo(zenfolio, rawPhoto);
                    photos.add(photo);
                }
            }
        }
    }


    @Override
    public List<Folder<Photo>> getFolders() {
        return new LinkedList<Folder<Photo>>();
    }


    @Override
    public Folder<Photo> getFolder(final String name) {
        return null;
    }


    @Override
    public Photo getThing(final String name) throws ThingsException {
        Photo result = null;

        for (final Photo photo : getThings()) {
            if (photo.getName().equals(name)) {
                result = photo;
                break;
            }
        }

        return result;
    }


    @Override
    public List<Photo> getThings() throws ThingsException {
        ensureIsPopulated();

        // @todo sort and immute?
        return photos;
    }


    @Override
    public boolean canHaveFolders() {
        return false;
    }


    @Override
    protected void checkFolderType(
        final boolean canHaveDirectories,
        final boolean canHaveItems)
    {
        // will not be called - checked in the base class
    }


    @Override
    protected Folder<Photo> doCreateFolder(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws ThingsException
    {
        // will not be called - checked in the base class
        throw new UnsupportedOperationException("Gallery can not have subdirectories");
    }


    @Override
    protected Folder<Photo> doCreateFakeFolder(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws ThingsException
    {
        // will not be called - checked in the base class
        throw new UnsupportedOperationException("Gallery can not have subdirectories");
    }


    public String postFile(final File file) throws FileNotFoundException, IOException {
        final String url = "http://www.zenfolio.com" + photoSet.getUploadUrl();

        final PostMethod filePost = new PostMethod(url);

        filePost.setRequestHeader(zenfolio.getAuthTokenHeader());

        final String date = new Date(file.lastModified()).toString();

        final RequestEntity entity =
//        makeSimplifiedPost
        makeMultiPartPost
            (filePost, file, date);

        filePost.setRequestEntity(entity);

        final HttpClient client = new HttpClient();
//        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

        final int status = client.executeMethod(filePost);

        filePost.releaseConnection();

        return (status == HttpStatus.SC_OK) ? null : HttpStatus.getStatusText(status);
    }


    private RequestEntity makeMultiPartPost(final PostMethod filePost, final File file, final String date)
        throws IOException
    {
//        filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);

        final Part[] parts = {
            new FilePart("file", file.getName(), file, "image/jpeg", "UTF-8"),
            new StringPart("file_modified", date)
        };

        return new MultipartRequestEntity(parts, filePost.getParams());
    }


    private RequestEntity makeSimplifiedPost(final PostMethod filePost, final File file, final String date) {
        final NameValuePair[] queryParameters = new NameValuePair[2];
        queryParameters[0] = new NameValuePair("filename", file.getName());
        queryParameters[1] = new NameValuePair("modified", date);

        filePost.setQueryString(queryParameters);

        return new FileRequestEntity(file, "image/jpeg");
    }


    private final Zenfolio zenfolio;


    private PhotoSet photoSet;


    private final List<Photo> photos = new LinkedList<Photo>();
}
