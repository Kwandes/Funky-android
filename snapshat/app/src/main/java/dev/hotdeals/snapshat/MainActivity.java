package dev.hotdeals.snapshat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    // Image operation variables
    static final int REQUEST_IMAGE_CAPTURE = 1;

    TextView helloWorldTxt;
    ImageView previewImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helloWorldTxt = findViewById(R.id.helloWorldTxt);
        previewImageView = findViewById(R.id.previewImageView);

    }

    public void addImage(View view) {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Create a reference to "mountains.jpg"
        StorageReference mimiRef = storageRef.child("mimi.jpg");
        // Create a reference to 'images/mountains.jpg'
        StorageReference mimiImagesRef = storageRef.child("mimi/mimi.jpg");

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
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.d("Firebase", "Kiwi Mimi uploaded");
                helloWorldTxt.setText("Image uploaded!");
            }
        });
    }

    public void takeCameraPicture(View view)
    {
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

    /*
    public void testFirestore(View view)
    {
        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("test part one", "test stuff");
        dataToSave.put("test part two", "more test stuff");
        db.document("test/example").set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Saving result", "Thign has been saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Saving result", e);
            }
        });
        db.collection("test").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Yeet", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.w("Yeet", "Error getting documents.", task.getException());
                }
            }
        });
    }
     */
}