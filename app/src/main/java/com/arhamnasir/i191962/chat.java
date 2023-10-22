package com.arhamnasir.i191962;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class chat extends AppCompatActivity {

    private DatabaseReference messagesRef;
    private EditText editTextMessage;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesRef = FirebaseDatabase.getInstance().getReference("messages");
        editTextMessage = findViewById(R.id.editTextMessage);
        Button buttonSendMessage = findViewById(R.id.buttonSendMessage);
        ImageButton recordButton = findViewById(R.id.recordVoiceButton);
        ImageButton galleryButton = findViewById(R.id.sendImageButton);
        ImageView imagePreview = findViewById(R.id.imagePreview);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMessages);

        messageList = new ArrayList<>(); // Initialize messageList
        chatAdapter = new ChatAdapter(messageList, this);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize MediaRecorder and audio file path
        mediaRecorder = new MediaRecorder();
        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/audio.3gp"; // Temporary storage

        // Set up click listener for the record button
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start recording
                startRecording();
            }
        });

        // Set up click listener for the gallery button
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery to select an image
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messageList.add(message);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String receiverId = "ID_of_the_receiver_user"; // Replace this with the actual receiver's ID
                String messageText = editTextMessage.getText().toString().trim();
                long timestamp = System.currentTimeMillis();

                if (messageText.isEmpty() && selectedImageUri == null) {
                    // Handle the case when there's no text message or image selected
                    return;
                }

                // Determine the type of message
                String messageType;
                if (selectedImageUri != null) {
                    // If an image is selected, set the message type to "image"
                    messageType = "image";
                } else {
                    // If no image is selected, set the message type to "text"
                    messageType = "text";
                }

                // Create a new Message object based on the message type
                Message message;
                if (messageType.equals("text")) {
                    message = new Message(senderId, receiverId, messageText, messageType, timestamp);
                } else {
                    // If the message type is "image", set the image URL to the selected image's URI
                    message = new Message(senderId, receiverId, selectedImageUri.toString(), messageType, timestamp);
                }

                // Push the message to the "messages" node in Firebase Realtime Database
                messagesRef.push().setValue(message);

                // Clear the message input field and image preview after sending
                editTextMessage.setText("");
                imagePreview.setVisibility(View.GONE);
                selectedImageUri = null;
            }
        });
    }

    private void startRecording() {
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        // Stop recording
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        // Create a Message object with necessary details
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverId = "ID_of_the_receiver_user"; // Replace this with the actual receiver's ID
        long timestamp = System.currentTimeMillis();

        // Upload the audio file to Firebase Storage
        uploadAudioToStorage(audioFilePath, senderId, receiverId, timestamp);
    }

    private void uploadAudioToStorage(String audioFilePath, String senderId, String receiverId, long timestamp) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        Uri audioFileUri = Uri.fromFile(new File(audioFilePath));
        StorageReference audioRef = storageRef.child("audio/" + audioFileUri.getLastPathSegment());

        // Upload file to Firebase Storage
        audioRef.putFile(audioFileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded audio file
                    audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();

                        // Create a Message object with audio download URL
                        Message message = new Message(senderId, receiverId, downloadUrl, "audio", timestamp);

                        // Push the Message object to the "messages" node in Firebase Realtime Database
                        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
                        messagesRef.push().setValue(message);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle failed upload
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Release MediaRecorder when the activity is stopped
        if (mediaRecorder != null) {
            stopRecording();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            // Display the selected image in the preview area
            ImageView imagePreview = findViewById(R.id.imagePreview);
            imagePreview.setVisibility(View.VISIBLE);
            imagePreview.setImageURI(selectedImageUri);
            // Save the selectedImageUri for sending
            this.selectedImageUri = selectedImageUri;
        }
    }
}
