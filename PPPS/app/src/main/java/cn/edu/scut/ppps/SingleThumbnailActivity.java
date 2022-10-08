package cn.edu.scut.ppps;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class SingleThumbnailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_thumnail);

        Bundle bundle = getIntent().getExtras();
        String imgPath1 = bundle.getString("imgPath1");
        String imgPath2 = bundle.getString("imgPath2");

        ImageView originalImage = findViewById(R.id.originalImage);
        ImageView encrypt1 = findViewById(R.id.encryptedImage1);
        //ImageView decrypt2 = findViewById(R.id.decryptedImage2);



       // Bitmap bitmapOrigin = BitmapFactory.decodeFile(imgPath); //从路径加载出图片bitmap
       // originalImage.setImageBitmap(bitmapOrigin); //ImageView显示图片
        Bitmap bitmapdecrypt1 = BitmapFactory.decodeFile(imgPath1);
        encrypt1.setImageBitmap(bitmapdecrypt1);
        //Bitmap bitmapdecrypt2 = BitmapFactory.decodeFile(imgPath2);
       // decrypt2.setImageBitmap(bitmapdecrypt2);


    }
}