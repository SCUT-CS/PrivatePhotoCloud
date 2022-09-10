package cn.edu.scut.ppps.cloud;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.DeleteMultipleObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteMultipleObjectResult;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteObjectResult;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.OSSObjectSummary;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.edu.scut.ppps.Utils;

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
    private Handler handler = null;

    /**
     * Constructor.
     * @param tokenName Token`s name.
     * @param context Context of the application.
     * @param tokens Tokens for the cloud storage.
     * @author Cui Yuxin
     */
    public AliOSS(String tokenName, Context context, Tokens tokens) {
        Map<String, String> token = tokens.getToken(tokenName);
        assert token.get("type").equals("aliyun");
        this.token = token;
        this.context = context;
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(token.get("accessId"), token.get("accessSecret"));
        ossClient = new OSSClient(context, token.get("endpoint"), credentialProvider);
    }

    /**
     * Set the handler.
     * @param handler Handler to set.
     * @author Cui Yuxin
     */
    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
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
                handler.sendEmptyMessage(Utils.CLOUD_SUCCESS);
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
                handler.sendEmptyMessage(Utils.CLOUD_FAILURE);
            }
        });
        // 取消上传任务。
        // task.cancel();
        // 等待上传任务完成。
        //task.waitUntilFinished();
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
        handler.sendEmptyMessage(Utils.CLOUD_SUCCESS);
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
                handler.sendEmptyMessage(Utils.CLOUD_SUCCESS);
            }
            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException,
                                  ServiceException serviceException)  {
                handler.sendEmptyMessage(Utils.CLOUD_FAILURE);
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
        GetObjectRequest get = new GetObjectRequest(token.get("bucketName"), objectName);
        // TODO 修改为合适的缩略图参数。
        get.setxOssProcess("image/resize,lfit,w_100,h_100");
        OSSAsyncTask task = ossClient.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
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
                        FileOutputStream fout = new FileOutputStream(localPath);
                        fout.write(buffer);
                        fout.close();
                    } catch (Exception e) {
                        OSSLog.logInfo(e.toString());
                    }
                }
                handler.sendEmptyMessage(Utils.CLOUD_SUCCESS);
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                handler.sendEmptyMessage(Utils.CLOUD_FAILURE);
            }
        });
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
                handler.sendEmptyMessage(Utils.CLOUD_SUCCESS);
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
                handler.sendEmptyMessage(Utils.CLOUD_FAILURE);
            }
        }).getResult().getObjectSummaries();
        List<String> fileList = new ArrayList<>();
        objectSummaries.forEach(ossObjectSummary -> {
            fileList.add(ossObjectSummary.getKey().replace(Objects.requireNonNull(token.get("filePath")), ""));
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
        if (fileName == null || fileName.equals("")) {
            return false;
        } else {
            DeleteObjectRequest delete = new DeleteObjectRequest(token.get("bucketName"), token.get("filePath") + fileName);
            OSSAsyncTask deleteTask = ossClient.asyncDeleteObject(delete, new OSSCompletedCallback<DeleteObjectRequest, DeleteObjectResult>() {
                @Override
                public void onSuccess(DeleteObjectRequest request, DeleteObjectResult result) {
                    Log.d("asyncDeleteObject", "success!");
                    handler.sendEmptyMessage(Utils.CLOUD_SUCCESS);
                }

                @Override
                public void onFailure(DeleteObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
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
                    handler.sendEmptyMessage(Utils.CLOUD_FAILURE);
                }
            });
        }
        return true;
    }

    /**
     * Delete all files from the cloud storage and return if success.
     * @author Cui Yuxin
     */
    @Override
    public boolean deleteAll() throws Exception {
        List<String> fileList = getFileList();
        fileList.forEach(this::delete);
        handler.sendEmptyMessage(Utils.CLOUD_SUCCESS);
        return true;
    }

    /**
     * Delete files from the cloud storage and return if success.
     * @param fileNames Names of the files to be deleted.
     * @author Cui Yuxin
     */
    @Override
    public boolean delete(List<String> fileNames) {
        // 设置需要删除的多个Object完整路径。Object完整路径中不能包含Bucket名称。
        List<String> keys = new ArrayList<>();
        String filePath = token.get("filePath");
        fileNames.forEach(fileName -> {
            keys.add(filePath + fileName);
        });
        // 设置为简单模式，只返回删除失败的文件列表。
        DeleteMultipleObjectRequest request = new DeleteMultipleObjectRequest(token.get("bucketName"), keys, true);
        ossClient.asyncDeleteMultipleObject(request, new OSSCompletedCallback<DeleteMultipleObjectRequest, DeleteMultipleObjectResult>() {
            @Override
            public void onSuccess(DeleteMultipleObjectRequest request, DeleteMultipleObjectResult result) {
                Log.i("DeleteMultipleObject", "success");
                handler.sendEmptyMessage(Utils.CLOUD_SUCCESS);
            }

            @Override
            public void onFailure(DeleteMultipleObjectRequest request, ClientException clientException, ServiceException serviceException) {
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
                handler.sendEmptyMessage(Utils.CLOUD_FAILURE);
            }
        });
        return true;
    }
}
