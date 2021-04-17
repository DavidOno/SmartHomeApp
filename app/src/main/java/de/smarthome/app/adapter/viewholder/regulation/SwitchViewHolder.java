package de.smarthome.app.adapter.viewholder.regulation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.impl.Datapoint;
import de.smarthome.app.adapter.RegulationAdapter;


public class SwitchViewHolder extends RegulationAdapter.ViewHolder{
    private SwitchCompat binarySwitch;
    private TextView textView;
    private RegulationAdapter adapter;
    private RegulationAdapter.OnItemClickListener onSwitchClickListener;

    public SwitchViewHolder(@NonNull ViewGroup parent,
                            @NonNull RegulationAdapter.OnItemClickListener onSwitchClickListener,
                            @NonNull RegulationAdapter adapter) {
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
                    if(binarySwitch.isChecked()){
                        onSwitchClickListener.onItemClick(adapter.getDataPointAt(position), "1");
                    }else{
                        onSwitchClickListener.onItemClick(adapter.getDataPointAt(position), "0");
                    }
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RegulationAdapter.ViewHolder holder, int position, Datapoint datapoint, Optional<String> value) {
        textView.setText(datapoint.getName());

        if(value.isPresent()){
            if(value.get().equals("1")){
                binarySwitch.setChecked(true);
            }else if (value.get().equals("0")){
                binarySwitch.setChecked(false);
            }
        }
    }
}
