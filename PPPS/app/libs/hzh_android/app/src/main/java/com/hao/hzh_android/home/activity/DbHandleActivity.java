package com.hao.hzh_android.home.activity;

import android.view.View;
import android.widget.EditText;
import com.hao.baselib.base.BaseActivity;
import com.hao.baselib.utils.NullUtil;
import com.hao.baselib.utils.ToastUtil;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.DbHandleCallback;
import com.hao.hzh_android.home.model.DbHandleModel;
import com.longway.roomdb.Book;
import com.longway.roomdb.MyDatabase;
import com.longway.roomdb.Student;

import java.util.List;
import java.util.UUID;
import butterknife.BindView;
import butterknife.OnClick;

public class DbHandleActivity extends BaseActivity<DbHandleModel> implements DbHandleCallback {

    @BindView(R.id.et_xsmc)
    EditText et_xsmc;
    @BindView(R.id.et_xsnl)
    EditText et_xsnl;
    @BindView(R.id.et_sjmc)
    EditText et_sjmc;
    @BindView(R.id.et_sjsl)
    EditText et_sjsl;

    @Override
    protected DbHandleModel getModelImp() {
        return new DbHandleModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_db_handle;
    }

    @Override
    protected void initWidget() {

    }

    @OnClick({R.id.add,R.id.queryBook,R.id.queryStudent})
    void click(View v){
        switch (v.getId()){
            case R.id.add:
                //添加对应数据
                if (!NullUtil.isTextEmpty(et_xsmc) && !NullUtil.isTextEmpty(et_xsnl)){
                    //加入对应的学生数据
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MyDatabase myDatabase = MyDatabase.getInstance(DbHandleActivity.this);
                            Student student = new Student(UUID.randomUUID().toString(),et_xsmc.getText().toString().trim(),et_xsnl.getText().toString().trim());
                            myDatabase.studdentDao().insertStudent(student);
                        }
                    }).start();
                }
                if (!NullUtil.isTextEmpty(et_sjmc) && !NullUtil.isTextEmpty(et_sjsl)){
                    //加入对应的学生数据
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MyDatabase myDatabase = MyDatabase.getInstance(DbHandleActivity.this);
                            Book book =  new Book(UUID.randomUUID().toString(),et_sjmc.getText().toString().trim(),Integer.parseInt(et_sjsl.getText().toString().trim()));
                            myDatabase.bookDao().insertBook(book);
                        }
                    }).start();
                }
                break;
            case R.id.queryBook:
                //查询书籍
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MyDatabase myDatabase = MyDatabase.getInstance(DbHandleActivity.this);
                        List<Book> listBook = myDatabase.bookDao().getBookList();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.toastWord(DbHandleActivity.this,listBook.get(listBook.size()-1).bookName);
                            }
                        });
                    }
                }).start();
                break;
            case R.id.queryStudent:
                //查询学生
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MyDatabase myDatabase = MyDatabase.getInstance(DbHandleActivity.this);
                        List<Student> listStudent = myDatabase.studdentDao().getStudentList();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.toastWord(DbHandleActivity.this,listStudent.get(listStudent.size()-1).name);
                            }
                        });
                    }
                }).start();
                break;
        }
    }
}
