package de.smarthome.app.adapter.viewholder.regulation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.adapter.RegulationAdapter;

public class SwitchViewHolder extends RegulationAdapter.ViewHolder{
    private SwitchCompat binarySwitch;
    private TextView textViewName;

    public SwitchViewHolder(@NonNull ViewGroup parent,
                            @NonNull RegulationAdapter.OnItemClickListener onSwitchClickListener,
                            @NonNull RegulationAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_switch, parent, false));

        textViewName = itemView.findViewById(R.id.textView_item);
        binarySwitch = itemView.findViewById(R.id.switch_item);

        binarySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        textViewName.setText(datapoint.getName().replace("_", " "));

        if(value.isPresent()){
            if(value.get().equals("true")){
                binarySwitch.setChecked(true);
            }else if (value.get().equals("false")){
                binarySwitch.setChecked(false);
            }
        }
    }
}