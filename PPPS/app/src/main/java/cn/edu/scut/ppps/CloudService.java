package cn.edu.scut.ppps;

import java.io.FileNotFoundException;

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
    boolean upload(String filePath) throws FileNotFoundException;

    /**
     * Upload a file to the cloud storage and return if success.
     * @param file File to be uploaded.
     * @param fileName Name of the file to be uploaded.
     * @author Cui Yuxin
     */
    boolean upload(byte[] file, String fileName);

    /**
     * Download a file from the cloud storage and return if success.
     * @param fileName Name of the file to be downloaded.
     * @author Cui Yuxin
     */
    boolean download(String fileName);

    /**
     * Get a thumbnail of a file from the cloud storage and return.
     * @param fileName Name of the file to be downloaded.
     * @author Cui Yuxin
     */
    void getThumbnail(String fileName);

}
