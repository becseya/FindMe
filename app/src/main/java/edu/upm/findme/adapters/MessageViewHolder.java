package edu.upm.findme.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.upm.findme.R;
import edu.upm.findme.model.MessageDetails;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView content;
    TextView senderName;

    public MessageViewHolder(View itemView) {
        super(itemView);
        this.content = itemView.findViewById(R.id.message_card_content);
        this.senderName = itemView.findViewById(R.id.message_card_sender_name);
    }

    void bindValues(MessageDetails message) {
        content.setText(message.getContent());
        senderName.setText(message.getSenderName());
    }
}
