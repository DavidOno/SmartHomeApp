package de.smarthome.server.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import de.smarthome.R;
import de.smarthome.server.adapter.TestAdapter;

public class DefaultViewHolder extends TestAdapter.ViewHolder{
    public TextView textView;

    public DefaultViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.text_view_name);

    }


    @Override
    public void onClick(View v) {
    }
}
