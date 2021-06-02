package dev.hotdeals.snapshat.repo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import dev.hotdeals.snapshat.service.BitmapService;

public class FirebaseStorageRepo {
    // Fetch a bitmap and assign it to a given imageView in the snapList
    public static void fetchAndSetBitmapToImageView(StorageReference ref, ImageView imageView, Context context) {
        final long ONE_MEGABYTE = 1024 * 1024;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            // convert the bits into a bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // blur the bitmap to hide details and promote viewing the snap
            bitmap = BitmapService.blur(context, bitmap, 25);
            // Set the bitmap to the given imageView
            imageView.setImageBitmap(bitmap);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w("Get bitmap from file", exception);
            }
        });
    }

    public static void fetchSnapsToList(List<ImageView> snapList, Context context) {
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
                            FirebaseStorageRepo.fetchAndSetBitmapToImageView(item, snapList.get(snapCounter), context);
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

}
