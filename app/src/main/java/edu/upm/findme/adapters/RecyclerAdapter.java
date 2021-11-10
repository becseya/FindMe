package edu.upm.findme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.upm.findme.R;
import edu.upm.findme.model.User;

public class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

    List<User> usersList = null;

    public RecyclerAdapter() {
        super();
    }

    @NonNull
    @Override
    public edu.upm.findme.adapters.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull edu.upm.findme.adapters.MyViewHolder holder, int position) {
        holder.bindValues(usersList.get(position));
    }

    @Override
    public int getItemCount() {
        return (usersList == null) ? 0 : usersList.size();
    }

    public void updateUsers(List<User> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }
}
