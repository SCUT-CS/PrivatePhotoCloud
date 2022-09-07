package com.longway.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Student.class,Book.class},version = 3)
public abstract class MyDatabase extends RoomDatabase {

    private static final String DATABASE_NAME="my_db";

    private static MyDatabase databaseInstance;

    public static synchronized MyDatabase getInstance(Context context){
        if (databaseInstance == null){
            databaseInstance = Room.databaseBuilder(context.getApplicationContext()
                    ,MyDatabase.class,DATABASE_NAME).build();
        }
        return databaseInstance;
    }

    public abstract StuddentDao studdentDao();

    public abstract BookDao bookDao();
}
