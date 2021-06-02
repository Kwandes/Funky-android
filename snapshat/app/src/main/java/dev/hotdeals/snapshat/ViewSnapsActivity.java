package dev.hotdeals.snapshat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import dev.hotdeals.snapshat.service.BitmapService;

public class ViewSnapsActivity extends AppCompatActivity {

    // List of images to show on the screen
    List<ImageView> snapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snaps);

        initSnaps();

        // Referencing to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        // Get all snaps
        storageReference.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        int snapCounter = 0;
                        for (StorageReference item : listResult.getItems()) {
                            // Only fetch the amount of snaps that would fit on the screen
                            if (snapCounter >= snapList.size()) return;
                            Log.d("List fetching", item.getName());
                            // Set the snaps name as its description
                            snapList.get(snapCounter).setContentDescription(item.getName());
                            // Set the snap to the corresponding imageView
                            fetchAndSetBitmapToImageView(item, snapList.get(snapCounter));
                            snapCounter++;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Fetching Files", e);
                    }
                });
    }

    // Fetch a bitmap and assign it to a given imageView in the snapList
    public void fetchAndSetBitmapToImageView(StorageReference ref, ImageView imageView) {
        final long ONE_MEGABYTE = 1024 * 1024;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            // convert the bits into a bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // blur the bitmap to hide details and promote viewing the snap
            bitmap = BitmapService.blur(this, bitmap, 25);
            // Set the bitmap to the given imageView
            imageView.setImageBitmap(bitmap);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w("Get bitmap from file", exception);
            }
        });
    }

    private void initSnaps() {
        snapList.add(findViewById(R.id.snapOne));
        snapList.add(findViewById(R.id.snapTwo));
        snapList.add(findViewById(R.id.snapThree));
        snapList.add(findViewById(R.id.snapFour));
        snapList.add(findViewById(R.id.snapFive));
        snapList.add(findViewById(R.id.snapSix));
        snapList.add(findViewById(R.id.snapSeven));
        snapList.add(findViewById(R.id.snapEight));
        snapList.add(findViewById(R.id.snapNine));

        for (int i = 0; i < snapList.size() - 1; i++) {
            int finalI = i;
            snapList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent snapDetailsIntent = new Intent(ViewSnapsActivity.this, ViewSnapDetailsActivity.class);
                    // Parse the snaps description, aka firebase storage file name, to the new activity
                    snapDetailsIntent.putExtra("SnapId", snapList.get(finalI).getContentDescription());
                    startActivity(snapDetailsIntent);
                }
            });

        }
    }

    public void goBackToMainActivity(View view) {
        finish();
    }
}