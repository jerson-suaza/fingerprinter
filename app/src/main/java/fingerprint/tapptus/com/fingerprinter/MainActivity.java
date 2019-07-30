package fingerprint.tapptus.com.fingerprinter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import asia.kanopi.fingerscan.Status;
import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity {

    ImageView ivFinger;
    TextView tvMessage;
    byte[] img;
    Bitmap bm;
    private static final int SCAN_FINGER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMessage = findViewById(R.id.tvMessage);
        ivFinger = findViewById(R.id.ivFingerDisplay);
    }

    public void startScan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, SCAN_FINGER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int status;
        String errorMessage;
        switch (requestCode) {
            case (SCAN_FINGER): {
                if (resultCode == RESULT_OK) {
                    status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        tvMessage.setText("Fingerprint captured");
                        img = data.getByteArrayExtra("img");
                        bm = BitmapFactory.decodeByteArray(img, 0, img.length);
                        ivFinger.setImageBitmap(bm);
                        this.saveImage(bm, new Timestamp(System.currentTimeMillis()).toString());
                    } else {
                        errorMessage = data.getStringExtra("errorMessage");
                        tvMessage.setText("-- Error: " + errorMessage + " --");
                    }
                }
                break;
            }
        }
    }

    private void saveImage(Bitmap finalBitmap, String ImageName) {
        int check = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (check == PackageManager.PERMISSION_GRANTED) {
            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),
                    finalBitmap, "Finger-" + ImageName + ".jpg", "description");
        } else {
            requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1024);
        }
    }
}
