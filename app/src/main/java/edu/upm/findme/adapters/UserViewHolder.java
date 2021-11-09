package edu.upm.findme.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.upm.findme.R;
import edu.upm.findme.model.User;

public class UserViewHolder extends RecyclerView.ViewHolder {

    TextView txtName;
    TextView txtPhone;

    public UserViewHolder(View itemView) {
        super(itemView);
        this.txtName = itemView.findViewById(R.id.lblUsercardName);
        this.txtPhone = itemView.findViewById(R.id.lblUsercardPhone);
    }

    void bindValues(User user) {
        txtName.setText(user.getName());
        txtPhone.setText(user.getPhoneNumber());
    }
}
