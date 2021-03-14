package de.smarthome.server.adapter.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import de.smarthome.R;
import de.smarthome.server.adapter.TestAdapter;


public class SwitchViewHolder extends TestAdapter.ViewHolder{
    public Switch aSwitch;
    public TextView textView;

    public SwitchViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.text_view_name);
        aSwitch = itemView.findViewById(R.id.item_room_overview_switch);

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Log.d("SwitchViewHolder", "Swicht pressed");
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
