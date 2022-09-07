package com.longway.roomdb;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "book")
public class Book {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "uuid",typeAffinity = ColumnInfo.TEXT)
    public String uuid;
    @ColumnInfo(name = "bookName",typeAffinity = ColumnInfo.TEXT)
    public String bookName;
    @ColumnInfo(name = "count",typeAffinity = ColumnInfo.INTEGER)
    public int count;

    public Book(@NonNull String uuid, String bookName, int count) {
        this.uuid = uuid;
        this.bookName = bookName;
        this.count = count;
    }
}
