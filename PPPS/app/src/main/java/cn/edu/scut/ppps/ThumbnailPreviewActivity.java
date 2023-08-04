package cn.edu.scut.ppps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class ThumbnailPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbnail_preview);

        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("imgPath");
        ImageView originalImage = findViewById(R.id.originalImage);
        Bitmap bitmapOrigin = BitmapFactory.decodeFile(path); //从路径加载出图片bitmap
        originalImage.setImageBitmap(bitmapOrigin); //ImageView显示图片
        SeekBar seekBar = findViewById(R.id.modifySeekBar);
        seekBar.setMax(255);
        seekBar.setProgress(127);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Bitmap modifyImage = bitmapOrigin;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                modifyImage = Utils.modifyBrightness(bitmapOrigin,i);
                originalImage.setImageBitmap(modifyImage); //ImageView显示图片
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        Button encryptBtn = findViewById(R.id.encryptButton);
        encryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button modifyBtn = findViewById(R.id.modifyButton);
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除path中末尾的".webp"
                String savePath = path.substring(0,path.length()-5);
              //存储修改后的图片
                Bitmap modifyImage = Utils.modifyBrightness(bitmapOrigin,seekBar.getProgress());
                try {
                    Utils.saveImg(modifyImage,savePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}