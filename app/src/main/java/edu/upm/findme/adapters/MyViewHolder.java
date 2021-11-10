package edu.upm.findme.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.upm.findme.R;
import edu.upm.findme.model.User;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView txtName;
    TextView txtPhone;

    public MyViewHolder(View itemView) {
        super(itemView);
        this.txtName = itemView.findViewById(R.id.userName);
        this.txtPhone = itemView.findViewById(R.id.userPhone);
    }

    void bindValues(User user) {
        txtName.setText(user.getName());
        txtPhone.setText(user.getPhoneNumber());
    }

}
