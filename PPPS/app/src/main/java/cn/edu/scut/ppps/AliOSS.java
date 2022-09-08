package cn.edu.scut.ppps;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.OSSObjectSummary;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
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
    private Map<String, String> token;
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
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(token.get("accessId"), token.get("accessSecret"));
        ossClient = new OSSClient(context, token.get("endpoint"), credentialProvider);
    }

    /**
     * Upload a file to the cloud storage and return if success.
     * @param filePath Path of the file to be uploaded.
     * @author Cui Yuxin
     */
    @Override
    public boolean upload(String filePath) {
        String fileName = (new File(filePath)).getName();
        String objectName = token.get("filePath") + fileName;
        // 构造上传请求。
        // 依次填写Bucket名称（例如examplebucket）、Object完整路径（例如exampledir/exampleobject.txt）和本地文件完整路径（例如/storage/emulated/0/oss/examplefile.txt）。
        // Object完整路径中不能包含Bucket名称。
        PutObjectRequest put = new PutObjectRequest(token.get("bucketName"), objectName, filePath);
        // 异步上传时可以设置进度回调。
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        OSSAsyncTask task = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
            }
            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常。
                if (clientExcepion != null) {
                    // 客户端异常，例如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务端异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        // 取消上传任务。
        // task.cancel();
        // 等待上传任务完成。
        // task.waitUntilFinished();
        return true;
    }

    /**
     * Upload a file to the cloud storage and return if success.
     * @param file     File bytes to be uploaded.
     * @param fileName Name of the file to be uploaded.
     * @author Cui Yuxin
     */
    @Override
    public boolean upload(byte[] file, String fileName) throws Exception {
        PutObjectRequest put = new PutObjectRequest(token.get("bucketName"), token.get("filePath") + fileName, file);
        PutObjectResult putResult = ossClient.putObject(put);
        Log.d("PutObject", "UploadSuccess");
        Log.d("ETag", putResult.getETag());
        Log.d("RequestId", putResult.getRequestId());
        return true;
    }

    /**
     * Download a file from the cloud storage and return if success.
     * @param fileName Name of the file to be uploaded.
     * @param downloadPath Path to store the file, e.g. "Disk1".
     * @author Cui Yuxin
     */
    @Override
    public boolean download(String fileName, String downloadPath) {
        String savePath = context.getCacheDir().getAbsolutePath() + File.separator + downloadPath;
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        String pathName = savePath + File.separator + fileName;
        GetObjectRequest get = new GetObjectRequest(token.get("bucketName"), token.get("filePath") + fileName);
        ossClient.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 开始读取数据。
                long length = result.getContentLength();
                if (length > 0) {
                    byte[] buffer = new byte[(int) length];
                    int readCount = 0;
                    while (readCount < length) {
                        try{
                            readCount += result.getObjectContent().read(buffer, readCount, (int) length - readCount);
                        }catch (Exception e){
                            OSSLog.logInfo(e.toString());
                        }
                    }
                    // 将下载后的文件存放在指定的本地路径，例如D:\\localpath\\exampleobject.jpg。
                    try {
                        FileOutputStream fout = new FileOutputStream(pathName);
                        fout.write(buffer);
                        fout.close();
                    } catch (Exception e) {
                        OSSLog.logInfo(e.toString());
                    }
                }
            }
            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException,
                                  ServiceException serviceException)  {
            }
        });
        return true;
    }

    /**
     * Save a thumbnail of a file from the cloud storage and return if success.
     * @param fileName     Name of the file to be downloaded.
     * @param downloadPath Path to store the file, e.g. "Disk1Thumbnail".
     * @author Cui Yuxin
     */
    @Override
    public boolean getThumbnail(String fileName, String downloadPath) {
        String cachePath = context.getCacheDir().getAbsolutePath();
        String savePath = cachePath + File.separator + downloadPath;
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        String localPath = savePath + File.separator + fileName;
        String objectName = token.get("filePath") + fileName;
        try {
            // 将图片等比缩放为延伸出指定w与h的矩形框外的最小图片，之后按照固定宽高进行裁剪.
            String style = "image/resize,m_fill,w_400,h_800";
            GetObjectRequest request = new GetObjectRequest(token.get("bucketName"), objectName);
            request.setProcess(style);
            ossClient.getObject(request, new File(localPath));
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
     * Get a list of files in the cloud storage and return.
     * @author Cui Yuxin
     */
    @Override
    public List<String> getFileList() throws Exception {
        // 填写Bucket名称，例如examplebucket。
        ListObjectsRequest request = new ListObjectsRequest(token.get("bucketName"));
        // 填写前缀。
        request.setPrefix(token.get("filePath"));
        List<OSSObjectSummary> objectSummaries = ossClient.asyncListObjects(request, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                for (OSSObjectSummary objectSummary : result.getObjectSummaries()) {
                    Log.i("ListObjects", objectSummary.getKey());
                }
            }
            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientException, ServiceException serviceException) {
                // 请求异常。
                if (clientException != null) {
                    // 客户端异常，例如网络异常等。
                    clientException.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务端异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        }).getResult().getObjectSummaries();
        List<String> fileList = new ArrayList<>();
        objectSummaries.forEach(ossObjectSummary -> {
            fileList.add(ossObjectSummary.getKey());
        });
        return fileList;
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
