package cn.edu.scut.ppps;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * Alibaba cloud OSS service.
 * @author Cui Yuxin
 */
public class AliOSS implements CloudService {

    /**
     * Alibaba cloud OSS login token. It should include the following fields:
     * "accessId", "accessSecret", "endpoint", "bucketName", "filePath".
     * "filePath" is the directory in the bucket to store files, e.g. "exampleDir/".
     * @author Cui Yuxin
     */
    private Map<String,String> token;
    private OSS ossClient;

    /**
     * Constructor.
     * @param tokenName Token`s name.
     * @author Cui Yuxin
     */
    public AliOSS(String tokenName) {
        this.token = token;
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
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
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
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return true;
    }

    /**
     * Download a file from the cloud storage and return if success.
     * @param fileName Name of the file to be downloaded.
     * @author Cui Yuxin
     */
    @Override
    public boolean download(String fileName) {
        return false;
    }

    /**
     * Get a thumbnail of a file from the cloud storage and return.
     * @param fileName Name of the file to be downloaded.
     * @author Cui Yuxin
     */
    @Override
    public void getThumbnail(String fileName) {

    }
}
