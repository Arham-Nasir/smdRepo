package com.arhamnasir.i191962;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private Context context;
    private MediaPlayer mediaPlayer;

    public ChatAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
        this.mediaPlayer = new MediaPlayer();
    }

    // ... other methods

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bindMessage(message);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView senderNameTextView;
        private TextView messageTextView;
        private TextView timestampTextView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            senderNameTextView = itemView.findViewById(R.id.senderName);
            messageTextView = itemView.findViewById(R.id.messageText);
            timestampTextView = itemView.findViewById(R.id.timestamp);
        }

        public void bindMessage(Message message) {
            senderNameTextView.setText(message.getSenderId());

            // Check if the message contains an audio URL
            if (message.getMessageType().equals("audio")) {
                // Handle audio download and playback here
                playAudio(message.getMessageText());
            } else {
                messageTextView.setText(message.getMessageText());
            }

            // Convert timestamp to a formatted date and time string
            Date date = new Date(message.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            String formattedDate = simpleDateFormat.format(date);

            timestampTextView.setText(formattedDate);
        }

        private void playAudio(String audioUrl) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Release MediaPlayer when the adapter is no longer in use
    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
