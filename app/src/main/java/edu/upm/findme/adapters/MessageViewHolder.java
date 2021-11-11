package edu.upm.findme.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.upm.findme.R;
import edu.upm.findme.model.MessageDetails;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView content;

    public MessageViewHolder(View itemView) {
        super(itemView);
        this.content = itemView.findViewById(R.id.lblMessageCardContent);

    }

    void bindValues(MessageDetails message) {
        content.setText(message.getContent());
    }
}
