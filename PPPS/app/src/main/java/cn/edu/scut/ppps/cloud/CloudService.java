package cn.edu.scut.ppps.cloud;

import android.os.Handler;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Cloud storage service interface.
 * @author Cui Yuxin
 */
public interface CloudService {

    /**
     * Upload a file to the cloud storage and return if success.
     * @param filePath Path of the file to be uploaded.
     * @author Cui Yuxin
     */
    boolean upload(String filePath);

    /**
     * Set the handler.
     * @param handler Handler to set.
     * @author Cui Yuxin
     */
    void setHandler(Handler handler);

    /**
     * Upload a file to the cloud storage and return if success.
     * @param file File to be uploaded.
     * @param fileName Name of the file to be uploaded.
     * @author Cui Yuxin
     */
    boolean upload(byte[] file, String fileName) throws Exception;

    /**
     * Download a file from the cloud storage and return if success.
     * @param fileName Name of the file to be uploaded.
     * @param downloadPath Path to store the file, e.g. "Disk1".
     * @author Cui Yuxin
     */
    boolean download(String fileName, String downloadPath);

    /**
     * Save a thumbnail of a file from the cloud storage and return if success.
     * @param fileName Name of the file to be downloaded.
     * @param downloadPath Path to store the file, e.g. "Disk1Thumbnail".
     * @author Cui Yuxin
     */
    boolean getThumbnail(String fileName, String downloadPath);

    /**
     * Get a list of files in the cloud storage and return.
     * @author Cui Yuxin
     */
    List<String> getFileList() throws Exception;

    /**
     * Delete a file from the cloud storage and return if success.
     * @param fileName Name of the file to be deleted.
     * @author Cui Yuxin
     */
    boolean delete(String fileName);

    /**
     * Delete all files from the cloud storage and return if success.
     * @author Cui Yuxin
     */
    boolean deleteAll() throws Exception;

    /**
     * Delete files from the cloud storage and return if success.
     * @param fileNames Names of the files to be deleted.
     * @author Cui Yuxin
     */
    boolean delete(List<String> fileNames);

}
