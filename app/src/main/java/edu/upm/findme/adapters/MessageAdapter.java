package edu.upm.findme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.upm.findme.R;
import edu.upm.findme.model.Message;
import edu.upm.findme.model.MessageDetails;
import edu.upm.findme.model.User;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    List<MessageDetails> messages = null;

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

    public void updateMessages(List<Message> allMessages, List<User> users, int userId) {
        if (messages == null) {
            messages = new ArrayList<>();

            for (Message m : allMessages)
                messages.add(new MessageDetails(m, userId));

            notifyDataSetChanged();
        } else if (messages.size() != allMessages.size()) {
            int idx = messages.size();

            for (int i = idx; i < allMessages.size(); i++)
                messages.add(new MessageDetails(allMessages.get(i), userId));

            notifyItemRangeInserted(idx, allMessages.size() - idx);
        }

        if (users == null)
            return;

        for (int i = 0; i < messages.size(); i++) {
            String senderName = getSenderNameById(users, messages.get(i).getSenderId());

            if ((senderName != null) && !messages.get(i).getSenderName().equals(senderName)) {
                messages.get(i).setSenderName(senderName);
                notifyItemChanged(i);
            }
        }
    }

    private String getSenderNameById(List<User> users, int id) {
        for (User u : users)
            if (u.getId() == id)
                return u.getName();

        return null;
    }
}
