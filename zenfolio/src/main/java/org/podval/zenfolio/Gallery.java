package org.podval.zenfolio;

import org.podval.photo.FolderType;
import org.podval.photo.PhotoException;

import com.zenfolio.www.api._1_1.PhotoSet;

import java.rmi.RemoteException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import java.util.List;
import java.util.LinkedList;
import java.util.Date;

import java.io.File;
import java.io.IOException;


/* package */ final class Gallery extends GroupLike<PhotoSet> {

    public Gallery(final Zenfolio zenfolio, final PhotoSet element) {
        super(zenfolio, element);
    }


    @Override
    public FolderType getFolderType() {
        return FolderType.Photos;
    }


    @Override
    protected void populate() throws PhotoException {
        // PhotoSet needs to be loaded, since in the "structure" it is not populated with the Photos.

        final int id = getElement().getId();

        if (id != 0) {
            try {
                setElement(getConnection().getConnection().loadPhotoSet(id));
            } catch (final RemoteException e) {
                throw new PhotoException(e);
            }

            if ((getElement().getPhotos() != null) && (getElement().getPhotos().getPhoto() != null)) {
                for (final com.zenfolio.www.api._1_1.Photo rawPhoto : getElement().getPhotos().getPhoto()) {
                    final ZenfolioPhoto photo = new ZenfolioPhoto(this, rawPhoto);
                    photos.add(photo);
                }
            }
        }
    }


    @Override
    public List<Gallery> getFolders() {
        return new LinkedList<Gallery>();
    }


    @Override
    public Gallery getFolder(final String name) {
        return null;
    }


    @Override
    public ZenfolioPhoto getPhoto(final String name) throws PhotoException {
        ZenfolioPhoto result = null;

        for (final ZenfolioPhoto photo : getPhotos()) {
            if (photo.getName().equals(name)) {
                result = photo;
                break;
            }
        }

        return result;
    }


    @Override
    public List<ZenfolioPhoto> getPhotos() throws PhotoException {
        ensureIsPopulated();

        // @todo sort and immute?
        return photos;
    }


    @Override
    protected void checkFolderType(final FolderType folderType) {
        // will not be called - checked in the base class
    }


    @Override
    protected Gallery doCreateFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        // will not be called - checked in the base class
        throw new UnsupportedOperationException("Gallery can not have subdirectories");
    }


    @Override
    protected Gallery doCreateFakeFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        // will not be called - checked in the base class
        throw new UnsupportedOperationException("Gallery can not have subdirectories");
    }


    @Override
    public void doAddFile(final String name, final File file) throws PhotoException {
        try {
            final String message = postFile(name, file);
            if (message != null) {
                throw new PhotoException(message);
            }
        } catch (final IOException e) {
            throw new PhotoException(e);
        }
    }


    private String postFile(final String name, final File file) throws IOException {
        final String url = "http://www.zenfolio.com" + getElement().getUploadUrl();

        final PostMethod filePost = new PostMethod(url);

        filePost.setRequestHeader(getConnection().getAuthTokenHeader());

        final String date = new Date(file.lastModified()).toString();

        final RequestEntity entity =
//        makeSimplifiedPost
        makeMultiPartPost
            (filePost, name, file, date);

        filePost.setRequestEntity(entity);

        final HttpClient client = new HttpClient();
        final int status = client.executeMethod(filePost);

        filePost.releaseConnection();

        return (status == HttpStatus.SC_OK) ? null : HttpStatus.getStatusText(status);
    }


    private RequestEntity makeMultiPartPost(
        final PostMethod filePost,
        final String name,
        final File file,
        final String date) throws IOException
    {
//        filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);

        final Part[] parts = {
            new FilePart("file", name, file, "image/jpeg", "UTF-8"),
            new StringPart("file_modified", date)
        };

        return new MultipartRequestEntity(parts, filePost.getParams());
    }


//    private RequestEntity makeSimplifiedPost(
//        final PostMethod filePost,
//        final String name,
//        final File file,
//        final String date)
//    {
//        final NameValuePair[] queryParameters = new NameValuePair[2];
//        queryParameters[0] = new NameValuePair("filename", name);
//        queryParameters[1] = new NameValuePair("modified", date);
//
//        filePost.setQueryString(queryParameters);
//
//        return new FileRequestEntity(file, "image/jpeg");
//    }


    @Override
    public void updateIfChanged() throws PhotoException {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private final List<ZenfolioPhoto> photos = new LinkedList<ZenfolioPhoto>();
}
