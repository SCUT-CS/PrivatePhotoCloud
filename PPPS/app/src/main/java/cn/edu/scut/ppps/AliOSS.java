package cn.edu.scut.ppps;

import android.content.Context;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Alibaba cloud OSS service.
 * @author Cui Yuxin
 */
public class AliOSS implements CloudService {

    /**
     * Alibaba cloud OSS login token. It should include the following fields:
     * "type", "accessId", "accessSecret", "endpoint", "bucketName", "filePath".
     * "type" is the type of the cloud service, e.g. "aliyun".
     * "filePath" is the directory in the bucket to store files, e.g. "exampleDir/".
     * @author Cui Yuxin
     */
    private Map<String,String> token;
    private OSS ossClient;
    private Context context;

    /**
     * Constructor.
     * @param tokenName Token`s name.
     * @param context Context of the application.
     * @param tokens Tokens for the cloud storage.
     * @author Cui Yuxin
     */
    public AliOSS(String tokenName, Context context, Tokens tokens) {
        Map<String, String> token = tokens.getToken(tokenName);
        // TODO Error handling.
        assert token.get("type").equals("aliyun");
        this.token = token;
        this.context = context;
        ossClient = new OSSClientBuilder().build(token.get("endpoint"),
                token.get("accessId"),
                token.get("accessSecret"));
    }

    /**
     * Upload a file to the cloud storage and return if success.
     * @param filePath Path of the file to be uploaded.
     * @author Cui Yuxin
     */
    @Override
    public boolean upload(String filePath) throws FileNotFoundException {
        String fileName = (new File(filePath)).getName();
        String objectName = token.get("filePath") + fileName;
        try {
            InputStream inputStream = new FileInputStream(filePath);
            // 创建PutObject请求。
            ossClient.putObject(token.get("bucketName"), objectName, inputStream);
        } catch (OSSException oe) {
            // TODO Error handling.
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return false;
        } catch (ClientException ce) {
            // TODO Error handling.
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Upload a file to the cloud storage and return if success.
     * @param file     File to be uploaded.
     * @param fileName Name of the file to be uploaded.
     * @author Cui Yuxin
     */
    @Override
    public boolean upload(byte[] file, String fileName) {
        String objectName = token.get("filePath") + fileName;
        try {
            // 创建PutObject请求。
            ossClient.putObject(token.get("bucketName"), objectName, new ByteArrayInputStream(file));
        } catch (OSSException oe) {
            // TODO Error handling.
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return false;
        } catch (ClientException ce) {
            // TODO Error handling.
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Download a file from the cloud storage and return if success.
     * @param fileName Name of the file to be downloaded.
     * @param downloadPath Path to store the file, e.g. "Disk1".
     * @author Cui Yuxin
     */
    @Override
    public boolean download(String fileName, String downloadPath) {
        String cachePath = context.getCacheDir().getAbsolutePath();
        String savePath = cachePath + File.separator + downloadPath;
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        String pathName = savePath + File.separator + fileName;
        String objectName = token.get("filePath") + fileName;
        try {
            // 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
            ossClient.getObject(new GetObjectRequest(token.get("bucketName"), objectName), new File(pathName));
        } catch (OSSException oe) {
            // TODO Error handling.
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return false;
        } catch (ClientException ce) {
            // TODO Error handling.
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Get a thumbnail of a file from the cloud storage and return.
     * @param fileName Name of the file to be downloaded.
     * @author Cui Yuxin
     */
    @Override
    public void getThumbnail(String fileName) {

    }

    /**
     * Get a list of files in the cloud storage and return.
     * @author Cui Yuxin
     */
    @Override
    public List<String> getFileList() {
        String prefix = token.get("filePath");
        try {
            // 列举指定前缀的文件。
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(token.get("bucketName"));
            listObjectsV2Request.setPrefix(prefix);
            ListObjectsV2Result result = ossClient.listObjectsV2(listObjectsV2Request);
            List<OSSObjectSummary> ossObjectSummaries = result.getObjectSummaries();
            List<String> fileList = new ArrayList<>();
            ossObjectSummaries.forEach(ossObjectSummary -> {
                fileList.add(ossObjectSummary.getKey());
            });
            return fileList;
        } catch (OSSException oe) {
            // TODO Error handling.
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return null;
        } catch (ClientException ce) {
            // TODO Error handling.
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return null;
        }
    }

    /**
     * Delete a file from the cloud storage and return if success.
     * @param fileName Name of the file to be deleted.
     * @author Cui Yuxin
     */
    @Override
    public boolean delete(String fileName) {
        String objectName = token.get("filePath") + fileName;
        try {
            // 删除Object。
            ossClient.deleteObject(token.get("bucketName"), objectName);
        } catch (OSSException oe) {
            // TODO Error handling.
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return false;
        } catch (ClientException ce) {
            // TODO Error handling.
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Delete all files from the cloud storage and return if success.
     * @author Cui Yuxin
     */
    @Override
    public boolean deleteAll() {
        // 如果仅需要删除src目录及目录下的所有文件，则prefix设置为src/。
        // 如果prefix设置为src后，所有前缀为src的非目录文件、src目录以及目录下的所有文件均会被删除！！！
        String prefix = token.get("filePath");
        try {
            // 列举所有包含指定前缀的文件并删除。
            String nextMarker = null;
            ObjectListing objectListing = null;
            do {
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest(token.get("bucketName"))
                        .withPrefix(prefix)
                        .withMarker(nextMarker);
                objectListing = ossClient.listObjects(listObjectsRequest);
                if (objectListing.getObjectSummaries().size() > 0) {
                    List<String> keys = new ArrayList<String>();
                    for (OSSObjectSummary s : objectListing.getObjectSummaries()) {
                        keys.add(s.getKey());
                    }
                    DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(token.get("bucketName")).withKeys(keys).withEncodingType("url");
                    ossClient.deleteObjects(deleteObjectsRequest);
                }
                nextMarker = objectListing.getNextMarker();
            } while (objectListing.isTruncated());
        } catch (OSSException oe) {
            // TODO Error handling.
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return false;
        } catch (ClientException ce) {
            // TODO Error handling.
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Delete files from the cloud storage and return if success.
     * @param fileNames Names of the files to be deleted.
     * @author Cui Yuxin
     */
    @Override
    public boolean delete(List<String> fileNames) {
        List<String> keys = new ArrayList<>();
        String filePath = token.get("filePath");
        fileNames.forEach(fileName -> {
            keys.add(filePath + fileName);
        });
        try {
            ossClient.deleteObjects(new DeleteObjectsRequest(token.get("bucketName")).withKeys(keys).withEncodingType("url"));
        } catch (OSSException oe) {
            // TODO Error handling.
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return false;
        } catch (ClientException ce) {
            // TODO Error handling.
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return false;
        }
        return true;
    }
}
