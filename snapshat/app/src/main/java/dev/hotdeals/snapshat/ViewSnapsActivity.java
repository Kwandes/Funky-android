package dev.hotdeals.snapshat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewSnapsActivity extends AppCompatActivity {

    ImageView testImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snaps);

        testImageView = findViewById(R.id.testImageView);

        // Referencing to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference islandRefLcl = storageReference.child("snap1e2d0944-91ba-4d3d-899e-8544c65ee2d3.jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        islandRefLcl.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            testImageView.setImageBitmap(bmp);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                testImageView.setImageResource(R.mipmap.ic_launcher);
            }
        });

    }
}