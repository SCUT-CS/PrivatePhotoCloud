package cn.edu.scut.ppps;

public interface CloudService {
    void upload(String filePath);
    void upload(byte[] file, String fileName);
    void download(String fileName);
    void getThumbnail(String fileName);

}
