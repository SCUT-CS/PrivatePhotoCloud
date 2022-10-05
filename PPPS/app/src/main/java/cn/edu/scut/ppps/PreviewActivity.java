package cn.edu.scut.ppps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


/**
 * Preview Activity
 * @author Huang Zixi
 */
public class PreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("imgPath");
        ImageView originalImage = findViewById(R.id.originalImage);
        Bitmap bitmapOrigin = BitmapFactory.decodeFile(path); //从路径加载出图片bitmap
        originalImage.setImageBitmap(bitmapOrigin); //ImageView显示图片

        Button btn = findViewById(R.id.encryptButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}