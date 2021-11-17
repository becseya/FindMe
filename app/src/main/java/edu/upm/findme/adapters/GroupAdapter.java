package edu.upm.findme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.upm.findme.R;
import edu.upm.findme.model.Group;


public class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder> implements GroupViewHolder.ItemClickListener {

    final int userId;
    List<Group> groups = new ArrayList<>();
    GroupClickListener clickListener;

    public GroupAdapter(GroupClickListener clickListener, int userId) {
        super();
        this.clickListener = clickListener;
        this.userId = userId;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_group, parent, false);
        return new GroupViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        boolean removable = (group.getOwnerId() == userId);

        holder.bindValues(group, removable);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void updateGroups(List<Group> fetchedGroups) {
        groups = fetchedGroups;
        notifyDataSetChanged();
    }

    public void removeAtPosition(int position) {
        groups.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemClick(int position, View v, boolean remove) {
        if (!remove) {
            notifyItemChanged(position); // animate click
            clickListener.onGroupClick(groups.get(position));
        } else
            clickListener.onGroupRemove(groups.get(position), position);
    }

    public interface GroupClickListener {
        void onGroupClick(Group group);

        void onGroupRemove(Group group, int position);
    }
}
