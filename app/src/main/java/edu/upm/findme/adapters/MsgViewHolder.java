package edu.upm.findme.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.upm.findme.R;
import edu.upm.findme.model.Message;

public class MsgViewHolder extends RecyclerView.ViewHolder {

    TextView content;


    public MsgViewHolder(View itemView) {
        super(itemView);
        this.content = itemView.findViewById(R.id.message);

    }

    void bindValues(Message message) {
        message.setContent(message.getContent());

    }

}
