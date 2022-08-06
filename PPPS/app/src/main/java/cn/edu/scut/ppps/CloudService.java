package cn.edu.scut.ppps;

/**
 * Cloud storage service interface.
 * @author Cui Yuxin
 */
public interface CloudService {

    /**
     * Upload a file to the cloud storage.
     * @param filePath Path of the file to be uploaded.
     * @author Cui Yuxin
     */
    void upload(String filePath);

    /**
     * Upload a file to the cloud storage.
     * @param file File to be uploaded.
     * @param fileName Name of the file to be uploaded.
     * @author Cui Yuxin
     */
    void upload(byte[] file, String fileName);

    /**
     * Download a file from the cloud storage.
     * @param fileName Name of the file to be downloaded.
     * @author Cui Yuxin
     */
    void download(String fileName);

    /**
     * Get a thumbnail of a file from the cloud storage.
     * @param fileName Name of the file to be downloaded.
     * @author Cui Yuxin
     */
    void getThumbnail(String fileName);

}
