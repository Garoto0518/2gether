package com.garoto.esrihackgtmaps;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class LiostOnlineViewHolder extends RecyclerView.ViewHolder {

    public TextView txtEmail;
    public LiostOnlineViewHolder(View itemView){
        super(itemView);
        txtEmail = (TextView)itemView.findViewById(R.id.txt_email);
    }
}
