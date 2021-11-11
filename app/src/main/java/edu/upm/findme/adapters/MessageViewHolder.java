package edu.upm.findme.adapters;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import edu.upm.findme.R;
import edu.upm.findme.model.MessageDetails;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView content;
    TextView senderName;
    LinearLayout wrapper;

    public MessageViewHolder(View itemView) {
        super(itemView);
        this.content = itemView.findViewById(R.id.message_card_content);
        this.senderName = itemView.findViewById(R.id.message_card_sender_name);
        this.wrapper = itemView.findViewById(R.id.message_card_wrapper);
    }

    void bindValues(MessageDetails message) {
        content.setText(message.getContent());
        senderName.setText(message.getSenderName());
        wrapper.setGravity(message.isSentByThemself() ? Gravity.END : Gravity.START);
    }
}
