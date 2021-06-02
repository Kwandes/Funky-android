package dev.hotdeals.snapshat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ViewSnapDetailsActivity extends AppCompatActivity {

    ImageView detailedSnapImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snap_details);

        detailedSnapImageView = findViewById(R.id.detailedSnapImageView);

        String snapId = getIntent().getStringExtra("SnapId");
        Log.d("Detailed Snap", "This is the detailed Snap name: " + snapId);
    }

    public void goBack(View view) {
        finish();
    }
}