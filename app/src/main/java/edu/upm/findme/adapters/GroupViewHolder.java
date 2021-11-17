package edu.upm.findme.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.upm.findme.R;
import edu.upm.findme.model.Group;


public class GroupViewHolder extends RecyclerView.ViewHolder {

    final TextView name;
    final ImageView imgRemove;
    final ItemClickListener listener;

    public GroupViewHolder(View itemView, ItemClickListener listener) {
        super(itemView);
        this.name = itemView.findViewById(R.id.lbl_groupcard_name);
        this.imgRemove = itemView.findViewById(R.id.img_remove_group);
        this.listener = listener;

        itemView.setOnClickListener((view) -> {
            listener.onItemClick(getAdapterPosition(), view, false);
        });

        imgRemove.setOnClickListener((view) -> {
            listener.onItemClick(getAdapterPosition(), view, true);
        });
    }

    void bindValues(Group group, boolean removable) {
        name.setText(group.getName());
        if (!removable)
            imgRemove.setVisibility(View.GONE);
    }

    public interface ItemClickListener {
        void onItemClick(int position, View v, boolean remove);
    }
}
