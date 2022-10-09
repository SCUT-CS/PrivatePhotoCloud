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
        ImageView thumbnail1 = findViewById(R.id.thumbnailImage1);
        ImageView thumbnail2 = findViewById(R.id.thumbnailImage2);

        String imgName = null;
        try {
            imgName = Utils.getFileName(imgPath1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "Thumbnail"
                + File.separator + imgName;

       Bitmap bitmapOrigin = BitmapFactory.decodeFile(imgPath); //从路径加载出图片bitmap
       originalImage.setImageBitmap(bitmapOrigin); //ImageView显示图片
        Bitmap bitmapdecrypt1 = BitmapFactory.decodeFile(imgPath1);
        thumbnail1.setImageBitmap(bitmapdecrypt1);
        Bitmap bitmapdecrypt2 = BitmapFactory.decodeFile(imgPath2);
       thumbnail2.setImageBitmap(bitmapdecrypt2);


    }
}