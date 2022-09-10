package cn.edu.scut.ppps.gallary;

public class FolderBean {
    /**
     * 文件夹路径
     */
    private String dir;
    /**
     * 第一张图片路径
     */
    private String firstImgPath;
    /**
     * 文件夹名称
     */
    private String name;
    /**
     * 文件数量
     */
    private int count;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndexOf = this.dir.lastIndexOf("/");
        this.name = this.dir.substring(lastIndexOf+1);
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
