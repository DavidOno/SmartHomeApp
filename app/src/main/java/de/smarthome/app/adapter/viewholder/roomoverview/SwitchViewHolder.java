package de.smarthome.app.adapter.viewholder.roomoverview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.Function;
import de.smarthome.app.adapter.RoomOverviewAdapter;


public class SwitchViewHolder extends RoomOverviewAdapter.ViewHolder{
    private SwitchCompat binarySwitch;
    private TextView textViewName;

    public SwitchViewHolder(@NonNull ViewGroup parent,
                            @NonNull RoomOverviewAdapter.OnSwitchClickListener onSwitchClickListener,
                            @NonNull RoomOverviewAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_switch, parent, false));

        textViewName = itemView.findViewById(R.id.textView_item);
        binarySwitch = itemView.findViewById(R.id.switch_item);

        binarySwitch.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (onSwitchClickListener != null && position != RecyclerView.NO_POSITION) {
                onSwitchClickListener.onItemClick(adapter.getFunctionAt(position), binarySwitch.isChecked());
            }
        });
    }

    @Override
    public void onBindViewHolder(RoomOverviewAdapter.ViewHolder holder, int position, Function function, Optional<String> value) {
        textViewName.setText(function.getName().replace("_", " "));

        if(value.isPresent()){
            if (value.get().equals("true")) {
                binarySwitch.setChecked(true);

            } else if (value.get().equals("false")) {
                binarySwitch.setChecked(false);
            }
        }
    }
}
