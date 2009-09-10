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

import java.util.Date;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public final class Gallery {

    public Gallery(final Zenfolio zenfolio, final PhotoSet photoSet) {
        this.zenfolio = zenfolio;
        this.photoSet = photoSet;
    }


    public String getName() {
        return photoSet.getTitle();
    }


    public void populate() throws RemoteException {
        // PhotoSet needs to be loaded, since in the "structure" it is not populated with the Photos.

        final int id = photoSet.getId();

        if (id != 0) {
            photoSet = zenfolio.getConnection().loadPhotoSet(id);
        }
    }


    public Photo findPhotoByFileName(final String name) {
        Photo result = null;

        for (final Photo photo : getPhotos()) {
            final String fileName = photo.getFileName();
            if (fileName.equals(name)) {
                result = photo;
                break;
            }
        }

        return result;
    }


    public Photo[] getPhotos() {
        final ArrayOfPhoto array = photoSet.getPhotos();
        final Photo[] result = (array == null) ?  null : array.getPhoto();
        return (result == null) ? new Photo[0] : result;
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
