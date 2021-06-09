package dev.hotdeals.snapshat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import dev.hotdeals.snapshat.repo.FirebaseStorageRepo;

public class ViewSnapsActivity extends AppCompatActivity {

    // List of images to show on the screen
    List<ImageView> snapList = new ArrayList<>();
    public static boolean needsToBeRecreated = false;

    @Override
    // initialize the snap list and fetch the data from the database
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snaps);

        initSnaps();
        FirebaseStorageRepo.fetchSnapsToList(snapList, this);
    }

    // initialize the snap list and their onclick listeners
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

    // quite self explanatory
    public void goBackToMainActivity(View view) {
        finish();
    }

    @Override
    // refresh the current activity to reload the snaps etc
    public void onResume() {
        super.onResume();
        if (needsToBeRecreated) {
            needsToBeRecreated = false;
            // reset the activity
            recreate();
        }
    }
}