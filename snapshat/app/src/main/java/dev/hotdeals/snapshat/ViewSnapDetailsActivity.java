package dev.hotdeals.snapshat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import dev.hotdeals.snapshat.repo.FirebaseStorageRepo;

public class ViewSnapDetailsActivity extends AppCompatActivity {

    ImageView detailedSnapImageView;
    String snapId = "n/a";

    @Override
    // Get a high res version of the given snap
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snap_details);

        detailedSnapImageView = findViewById(R.id.detailedSnapImageView);

        snapId = getIntent().getStringExtra("SnapId");
        Log.d("Detailed Snap", "This is the detailed Snap name: " + snapId);

        StorageReference snapRef = FirebaseStorage.getInstance().getReference().child(snapId);
        FirebaseStorageRepo.fetchAndSetBitmapToImageView(snapRef, detailedSnapImageView, this);
    }

    // Go back to the snap list activity
    public void goBack(View view) {
        FirebaseStorage.getInstance().getReference().child(snapId).delete();
        ViewSnapsActivity.needsToBeRecreated = true;
        finish();
    }
}