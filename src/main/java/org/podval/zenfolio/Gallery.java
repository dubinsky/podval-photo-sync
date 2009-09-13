package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.ArrayOfPhoto;
import com.zenfolio.www.api._1_1.Photo;

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
import java.util.Arrays;
import java.util.Date;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public final class Gallery extends ZenfolioDirectory {

    public Gallery(final Zenfolio zenfolio, final PhotoSet photoSet) {
        this.zenfolio = zenfolio;
        this.photoSet = photoSet;
    }


    @Override
    public String getName() {
        return photoSet.getTitle();
    }


    @Override
    public void populate() throws RemoteException {
        // PhotoSet needs to be loaded, since in the "structure" it is not populated with the Photos.

        final int id = photoSet.getId();

        if (id != 0) {
            photoSet = zenfolio.getConnection().loadPhotoSet(id);
        }
    }


    @Override
    public List<ZenfolioDirectory> getSubDirectories() {
        return new LinkedList<ZenfolioDirectory>();
    }


    @Override
    public ZenfolioDirectory getSubDirectory(final String name) {
        return null;
    }


    @Override
    public Photo getItem(final String name) {
        Photo result = null;

        for (final Photo photo : getItems()) {
            final String fileName = photo.getFileName();
            if (fileName.equals(name)) {
                result = photo;
                break;
            }
        }

        return result;
    }


    @Override
    public List<Photo> getItems() {
        final ArrayOfPhoto photos = photoSet.getPhotos();
        final Photo[] array = (photos == null) ?  null : photos.getPhoto();
        Photo[] result = (array == null) ? new Photo[0] : array;
        return Arrays.asList(result);
    }


    @Override
    public boolean canHaveSubDirectories() {
        return false;
    }


    @Override
    protected void checkSubDirectoryType(
        final boolean canHaveDirectories,
        final boolean canHaveItems)
    {
        // will not be called - checked in the base class
    }


    @Override
    protected ZenfolioDirectory doCreateSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws RemoteException
    {
        // will not be called - checked in the base class
        throw new UnsupportedOperationException("Gallery can not have subdirectories");
    }


    @Override
    protected ZenfolioDirectory doCreateFakeSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws RemoteException
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
}
