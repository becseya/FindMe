package edu.upm.findme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.upm.findme.R;
import edu.upm.findme.model.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    List<Message> messages = null;

    public MessageAdapter() {
        super();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_message, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bindValues(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return (messages == null) ? 0 : messages.size();
    }

    public void updateMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }
}
