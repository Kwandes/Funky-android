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

    public static void fetchAndSetBitmapToImageView(StorageReference ref, ImageView imageView, Context context) {
        fetchAndSetBitmapToImageView(ref, imageView, context, false);
    }

    /**
     * Fetch a bitmap and assign it to a given imageView in the snapList
     * Optionally blurs the image (default is no blur)
     *
     * @param ref       Firebase Storage reference of the image file to download
     * @param imageView ImageView controller object that the downloaded image will be set to
     * @param context   The context that the imageView controller exists in
     * @param blur      Optional flag of whether or not to blur the image. Default is false
     */
    public static void fetchAndSetBitmapToImageView(StorageReference ref, ImageView imageView, Context context, boolean blur) {
        final long ONE_MEGABYTE = 1024 * 1024;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            // convert the bits into a bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // blur the bitmap to hide details and promote viewing the snap
            if (blur) {
                bitmap = BitmapService.blur(context, bitmap, 25);
            }
            // Set the bitmap to the given imageView
            imageView.setImageBitmap(bitmap);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w("Get bitmap from file", exception);
            }
        });
    }

    // fetch snaps and assign them to a list of image views
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
                            FirebaseStorageRepo.fetchAndSetBitmapToImageView(item, snapList.get(snapCounter), context, true);
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
