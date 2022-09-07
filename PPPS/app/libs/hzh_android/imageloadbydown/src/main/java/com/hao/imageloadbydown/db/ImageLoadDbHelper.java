package com.hao.imageloadbydown.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hao.imageloadbydown.entity.ImageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地数据库
 * @author WaterWood
 */
public class ImageLoadDbHelper extends SQLiteOpenHelper {

    private static ImageLoadDbHelper mHelper = null;
    protected static final String DB_NAME = "hzhimage.db";//数据库名称
    protected static final int DB_VERSION = 2;//数据库版本
    protected SQLiteDatabase mDB = null;
    private final String IMAGE_FILES = "image_files";

    protected ImageLoadDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    protected ImageLoadDbHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createImageFileDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * 关闭数据库
     */
    public void closeLink() {
        if (mDB != null && mDB.isOpen() == true) {
            mDB.close();
            mDB = null;
        }
    }

    /**
     * 单例获取Helper对象
     *
     * @param context
     * @param version
     * @return
     */
    public static ImageLoadDbHelper getInstance(Context context, int version) {
        if (version > 0 && mHelper == null) {
            mHelper = new ImageLoadDbHelper(context, version);
        } else if (mHelper == null) {
            mHelper = new ImageLoadDbHelper(context);
        }
        return mHelper;
    }

    /**
     * 通过读链接获取SQLiteDatabase
     *
     * @return
     */
    public SQLiteDatabase openReadLink() {
        if (mDB == null || mDB.isOpen() != true) {
            mDB = mHelper.getReadableDatabase();
        }
        return mDB;
    }

    /**
     * 通过写链接获取SQLiteDatabase
     *
     * @return
     */
    public SQLiteDatabase openWriteLink() {
        if (mDB == null || mDB.isOpen() != true) {
            mDB = mHelper.getWritableDatabase();
        }
        return mDB;
    }

    /**
     * 获取数据库名称
     *
     * @return
     */
    public String getDbName() {
        if (mHelper != null) {
            return mHelper.getDatabaseName();
        } else {
            return DB_NAME;
        }
    }

    /**
     * 创建图片表
     * @param db
     */
    private void createImageFileDb(SQLiteDatabase db) {
        String drop_sql = "DROP TABLE IF EXISTS " + IMAGE_FILES + ";";
        db.execSQL(drop_sql);
        String create_sql = "CREATE TABLE IF NOT EXISTS " + IMAGE_FILES + " ("
                + "uuid VARCHAR PRIMARY KEY NOT NULL,"
                + "imgurl VARCHAR NOT NULL,"
                + "imgpath VARCHAR NOT NULL,"
                + "isdelete INTEGER NOT NULL"
                + ");";
        db.execSQL(create_sql);
    }

    /**
     * 添加多条数据到数据库
     * @param infoArray
     * @return
     */
    public void insertImageFileDb(List<ImageBean> infoArray) {
        //循环全部数据
        for (int i = 0; i < infoArray.size(); i++) {
            ImageBean info = infoArray.get(i);
            ArrayList<ImageBean> tempArray = new ArrayList<>();
            //如果存在相同任务ID，就更新记录。注意条件语句的等号后面要用单引号括起来
            String condition = String.format("imgurl='%s'", info.getImgurl());
            tempArray = queryImageFileDb(condition);
            if (tempArray.size() > 0) {
                updateImageFileDb(info, condition);
                continue;
            }
            //如果不存在唯一性重复的记录，就插入新记录
            ContentValues cv = new ContentValues();
            cv.put("uuid", info.getUuid());
            cv.put("imgurl", info.getImgurl());
            cv.put("imgpath", info.getImgpath());
            cv.put("isdelete", info.getIsdelete());
            mDB.insert(IMAGE_FILES, "", cv);
        }
    }

    /**
     * 查询指定的图片数据
     * @param condition
     * @return
     */
    public ArrayList<ImageBean> queryImageFileDb(String condition) {
        String sql = String.format("select uuid,imgurl,imgpath,isdelete from %s where %s;", IMAGE_FILES, condition);
        ArrayList<ImageBean> infoArray = new ArrayList<>();
        Cursor cursor = mDB.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            for (; ; cursor.moveToNext()) {
                ImageBean info = new ImageBean();
                info.setUuid(cursor.getString(0));
                info.setImgurl(cursor.getString(1));
                info.setImgpath(cursor.getString(2));
                info.setIsdelete(cursor.getInt(3));
                infoArray.add(info);
                if (cursor.isLast() == true) {
                    break;
                }
            }
        }
        cursor.close();
        return infoArray;
    }


    /**
     * 更新图片信息
     * @param info
     * @param condition
     * @return
     */
    public int updateImageFileDb(ImageBean info, String condition) {
        ContentValues cv = new ContentValues();
        cv.put("uuid", info.getUuid());
        cv.put("imgurl", info.getImgurl());
        cv.put("imgpath", info.getImgpath());
        cv.put("isdelete", info.getIsdelete());
        int count = mDB.update(IMAGE_FILES, cv, condition, null);
        return count;
    }

    /**
     * 巡视列表本地库删除全部数据
     * @return
     */
    public void deleteImageFileDb() {
        ArrayList<ImageBean> imageBeans = queryImageFileDb("isdelete=0");
        for (int i = 0; i < imageBeans.size(); i++) {
            String condition = String.format("uuid='%s'", imageBeans.get(i).getUuid());
            imageBeans.get(i).setIsdelete(1);
            updateImageFileDb(imageBeans.get(i), condition);
        }
    }
}