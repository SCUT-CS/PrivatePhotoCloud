package com.longway.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDao {
    @Insert
    void insertBook(Book book);

    @Delete
    void deleteBook(Book book);

    @Update
    void updateBook(Book book);

    @Query("SELECT * FROM book")
    List<Book> getBookList();

    @Query("SELECT * FROM book WHERE uuid = :id")
    Book getBookById(String id);
}
