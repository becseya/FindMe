package edu.upm.findme.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.upm.findme.R;
import edu.upm.findme.model.Group;


public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    final TextView name;
    final ItemClickListener listener;

    public GroupViewHolder(View itemView, ItemClickListener listener) {
        super(itemView);
        this.name = itemView.findViewById(R.id.lbl_groupcard_name);
        this.listener = listener;
        itemView.setOnClickListener(this);
    }

    void bindValues(Group group) {
        name.setText(group.getName());
    }

    @Override
    public void onClick(View view) {
        listener.onItemClick(getAdapterPosition(), view);
    }

    public interface ItemClickListener {
        void onItemClick(int position, View v);
    }
}
