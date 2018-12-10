package cs371m.csc2726.busbuddy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.xml.datatype.Duration;

public class AddKid extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_kid_menu);

        final Button cancel = findViewById(R.id.cancelButton);
        final Button save = findViewById(R.id.saveButton);
        final Button photo = findViewById(R.id.takePhoto);

        photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pictureButtonPressed(v);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goBack = new Intent(getApplicationContext(), MainActivity.class);
                setResult(RESULT_CANCELED, goBack);
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveClicked();
            }
        });
    }

    public void saveClicked() {
        TextView kidName = (TextView) findViewById(R.id.kidName);
        String name = kidName.getText().toString();
        ImageView kidImage = findViewById(R.id.kidImage);
        BitmapDrawable photo = (BitmapDrawable) kidImage.getDrawable();
        Bitmap photoBits = photo.getBitmap();
        BitmapDrawable original = (BitmapDrawable) getResources().getDrawable(R.drawable.profile);
        Bitmap originalBits = original.getBitmap();
        if (name.isEmpty() || photoBits == originalBits) {
            Toast.makeText(getApplicationContext(), "Please enter name and photo",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent goBack = new Intent(getApplicationContext(), MainActivity.class);
        goBack.putExtra("kidName", name);
        goBack.putExtra("bitmap", photoBits);
        setResult(RESULT_OK, goBack);
        finish();
    }

    public void pictureButtonPressed(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            update_image(photo);
        }
    }

    private void update_image(Bitmap photo) {
        ImageView picture = findViewById(R.id.kidImage);
        picture.setImageBitmap(photo);
    }
}
