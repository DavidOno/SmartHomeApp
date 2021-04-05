package de.smarthome.server.adapter.viewholder.roomoverview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.model.impl.Function;
import de.smarthome.server.adapter.RoomOverviewAdapter;


public class SwitchViewHolder extends RoomOverviewAdapter.ViewHolder{
    private SwitchCompat binarySwitch;
    private TextView textView;
    private RoomOverviewAdapter adapter;
    private RoomOverviewAdapter.OnSwitchClickListener onSwitchClickListener;

    public SwitchViewHolder(@NonNull ViewGroup parent,
                            @NonNull RoomOverviewAdapter.OnSwitchClickListener onSwitchClickListener,
                            @NonNull RoomOverviewAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_switch, parent, false));

        textView = itemView.findViewById(R.id.textView_item);
        binarySwitch = itemView.findViewById(R.id.switch_item);

        this.onSwitchClickListener = onSwitchClickListener;
        this.adapter = adapter;

        binarySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SwitchViewHolder", "Switch pressed");
                int position = getAdapterPosition();
                if (onSwitchClickListener != null && position != RecyclerView.NO_POSITION) {
                    onSwitchClickListener.onItemClick(adapter.getFunctionAt(position), binarySwitch.isChecked());
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RoomOverviewAdapter.ViewHolder holder, int position, Function function, Optional<String> value) {
        this.textView.setText(function.getName());

        if(value.isPresent()){
            if(value.get().equals("1")){
                binarySwitch.setChecked(true);

            }else if (value.get().equals("0")){
                binarySwitch.setChecked(false);
            }
        }
    }
}
