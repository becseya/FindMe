package edu.upm.findme.adapters;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.upm.findme.R;
import edu.upm.findme.model.UserDetails;

public class UserViewHolder extends RecyclerView.ViewHolder {

    TextView txtName;
    TextView txtPhone;
    GradientDrawable statusCircle;

    public UserViewHolder(View itemView) {
        super(itemView);
        this.txtName = itemView.findViewById(R.id.lblUsercardName);
        this.txtPhone = itemView.findViewById(R.id.lblUsercardPhone);
        this.statusCircle = (GradientDrawable) itemView.findViewById(R.id.userStatus).getBackground();
    }

    void bindValues(UserDetails user) {
        txtName.setText(user.getName());
        txtPhone.setText(user.getPhoneNumber());
        statusCircle.setColor(getColorByStatus(user.getStatus(), itemView.getResources()));
    }

    int getColorByStatus(UserDetails.Status status, Resources resources) {
        switch (status) {
            case OFFLINE:
                return resources.getColor(R.color.offline_grey);
            case ONLINE:
                return resources.getColor(R.color.online_orange);
            case LIVE:
                return resources.getColor(R.color.online_green);
        }
        return 0;
    }
}
