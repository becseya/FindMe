package edu.upm.findme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.upm.findme.R;
import edu.upm.findme.model.User;
import edu.upm.findme.model.UserDetails;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    List<UserDetails> users = new ArrayList<>();

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

    public void updateUsers(List<User> fetchedUsers, Map<Integer, UserDetails.Status> statuses) {
        users.clear();

        for (User u : fetchedUsers)
            users.add(new UserDetails(u));

        updateUserStatuses(false, statuses);

        notifyDataSetChanged();
    }

    public void updateUserStatuses(Map<Integer, UserDetails.Status> statuses) {
        updateUserStatuses(true, statuses);
    }

    void updateUserStatuses(boolean notifyAdapter, Map<Integer, UserDetails.Status> statuses) {
        for (int i = 0; i < users.size(); i++) {
            UserDetails u = users.get(i);

            if (statuses.containsKey(u.getId())) {
                UserDetails.Status status = statuses.get(u.getId());

                if (status != u.getStatus()) {
                    u.setStatus(status);

                    if (notifyAdapter)
                        notifyItemChanged(i);
                }
            }
        }
    }
}
