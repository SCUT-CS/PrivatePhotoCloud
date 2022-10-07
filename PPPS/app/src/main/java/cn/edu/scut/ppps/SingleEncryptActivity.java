package cn.edu.scut.ppps;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

/**
 * Single Encrypt Activity
 * @author Huang Zixi
 */
public class SingleEncryptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_encrypt);

        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("imgPath");
        String cachePath = bundle.getString("cachePath");

        ImageView originalImage = findViewById(R.id.originalImage);
        ImageView encrypt1 = findViewById(R.id.encryptedImage1);
        ImageView encrypt2 = findViewById(R.id.encryptedImage2);
        
        String encryptPath1 = null;
        try {
            encryptPath1 = cachePath + File.separator + "Disk1" + File.separator+ Utils.getFileName(path) + ".ori.webp";
        } catch (IOException e) {
            e.printStackTrace();
        }

        String encryptPath2 = null;
        try {
            encryptPath2 = cachePath + File.separator + "Disk2" + File.separator+ Utils.getFileName(path) + ".ori.webp";
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmapOrigin = BitmapFactory.decodeFile(path); //从路径加载出图片bitmap
        originalImage.setImageBitmap(bitmapOrigin); //ImageView显示图片
        Bitmap bitmapencrypt1 = BitmapFactory.decodeFile(encryptPath1);
        encrypt1.setImageBitmap(bitmapencrypt1);
        Bitmap bitmapencrypt2 = BitmapFactory.decodeFile(encryptPath2);
        encrypt2.setImageBitmap(bitmapencrypt2);
    }
}