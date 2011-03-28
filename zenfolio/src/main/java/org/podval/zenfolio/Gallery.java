package org.podval.zenfolio;

import org.podval.photo.Folder;
import org.podval.photo.FolderType;
import org.podval.photo.PhotoException;

import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.AccessType;

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


/* package */ final class Gallery extends Folder<ZenfolioPhoto> {

    public Gallery(final Zenfolio zenfolio, final PhotoSet photoSet) {
        this.zenfolio = zenfolio;
        this.photoSet = photoSet;
    }


    @Override
    public String getName() {
        return photoSet.getTitle();
    }


    @Override
    public FolderType getFolderType() {
        return FolderType.Things;
    }


    @Override
    public boolean isPublic() {
        return photoSet.getAccessDescriptor().getAccessType() == AccessType.Public;
    }


    @Override
    public void setPublic(final boolean value) {
        photoSet.getAccessDescriptor().setAccessType((value) ? AccessType.Public : AccessType.Private );
    }


    @Override
    protected void populate() throws PhotoException {
        // PhotoSet needs to be loaded, since in the "structure" it is not populated with the Photos.

        final int id = photoSet.getId();

        if (id != 0) {
            try {
                photoSet = zenfolio.getConnection().loadPhotoSet(id);
            } catch (final RemoteException e) {
                throw new PhotoException(e);
            }

            if ((photoSet.getPhotos() != null) && (photoSet.getPhotos().getPhoto() != null)) {
                for (final com.zenfolio.www.api._1_1.Photo rawPhoto : photoSet.getPhotos().getPhoto()) {
                    final ZenfolioPhoto photo = new ZenfolioPhoto(rawPhoto);
                    photos.add(photo);
                }
            }
        }
    }


    @Override
    public List<Folder<ZenfolioPhoto>> getFolders() {
        return new LinkedList<Folder<ZenfolioPhoto>>();
    }


    @Override
    public Folder<ZenfolioPhoto> getFolder(final String name) {
        return null;
    }


    @Override
    public ZenfolioPhoto getThing(final String name) throws PhotoException {
        ZenfolioPhoto result = null;

        for (final ZenfolioPhoto photo : getThings()) {
            if (photo.getName().equals(name)) {
                result = photo;
                break;
            }
        }

        return result;
    }


    @Override
    public List<ZenfolioPhoto> getThings() throws PhotoException {
        ensureIsPopulated();

        // @todo sort and immute?
        return photos;
    }


    @Override
    protected void checkFolderType(final FolderType folderType) {
        // will not be called - checked in the base class
    }


    @Override
    protected Folder<ZenfolioPhoto> doCreateFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        // will not be called - checked in the base class
        throw new UnsupportedOperationException("Gallery can not have subdirectories");
    }


    @Override
    protected Folder<ZenfolioPhoto> doCreateFakeFolder(
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
        final String url = "http://www.zenfolio.com" + photoSet.getUploadUrl();

        final PostMethod filePost = new PostMethod(url);

        filePost.setRequestHeader(zenfolio.getAuthTokenHeader());

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


    private final Zenfolio zenfolio;


    private PhotoSet photoSet;


    private final List<ZenfolioPhoto> photos = new LinkedList<ZenfolioPhoto>();
}
