package dev.hotdeals.snapshat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();

    // Image operation variables
    static final int REQUEST_IMAGE_CAPTURE = 1;

    TextView helloWorldTxt;
    EditText captionText;
    ImageView previewImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helloWorldTxt = findViewById(R.id.helloWorldTxt);
        captionText = findViewById(R.id.captionText);
        previewImageView = findViewById(R.id.previewImageView);

        // Add a listener that adds a caption to the image
        captionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                Log.d("Caption", "Text changed");
                previewImageView.setImageBitmap(drawTextToBitmap(
                        ((BitmapDrawable) previewImageView.getDrawable()).getBitmap(),
                        captionText.getText().toString()));
            }
        });
    }

    public void addImage(View view) {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Create a reference to "mountains.jpg"
        // The name is a unique random identifier
        StorageReference mimiRef = storageRef.child("snap" + UUID.randomUUID().toString() + ".jpg");

        // Get the data from an ImageView as bytes
        previewImageView.setDrawingCacheEnabled(true);
        previewImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) previewImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mimiRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.w("The fuck", exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Firebase", "Kiwi Mimi uploaded");
                helloWorldTxt.setText("Image uploaded!");
            }
        });
    }

    public void takeCameraPicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            // I know it is deprecated but all guides are using it
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            Log.d("Camera", "Camera picture has been taken");
        } catch (ActivityNotFoundException e) {
            Log.w("Camera", e);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            previewImageView.setImageBitmap(imageBitmap);
        }
    }

    public Bitmap drawTextToBitmap(Bitmap image, String gText) {
        Bitmap.Config bitmapConfig = image.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are immutable,
        // so we need to convert it to mutable one
        image = image.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// new antialised Paint
        paint.setColor(Color.rgb(161, 161, 161));
        paint.setTextSize((int) (24)); // text size in pixels
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK); // text shadow
        canvas.drawText(gText, 10, 200, paint);
        return image;
    }

    public void viewImages(View view) {
        Intent intent = new Intent(this, ViewSnapsActivity.class);
        startActivity(intent);
    }
}