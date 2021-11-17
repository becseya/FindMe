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

    List<Group> groups = new ArrayList<>();
    GroupClickListener clickListener;

    public GroupAdapter(GroupClickListener clickListener) {
        super();
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_group, parent, false);
        return new GroupViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        holder.bindValues(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void updateGroups(List<Group> fetchedGroups) {
        groups = fetchedGroups;
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position, View v) {
        notifyItemChanged(position); // animate click
        clickListener.onGroupClick(groups.get(position));
    }

    public interface GroupClickListener {
        void onGroupClick(Group group);
    }
}
