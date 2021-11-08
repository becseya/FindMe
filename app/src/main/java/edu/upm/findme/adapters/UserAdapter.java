package edu.upm.findme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.upm.findme.R;
import edu.upm.findme.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    List<User> users = null;

    public UserAdapter() {
        super();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bindValues(users.get(position));
    }

    @Override
    public int getItemCount() {
        return (users == null) ? 0 : users.size();
    }

    public void updateUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }
}
