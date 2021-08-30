package de.smarthome.app.adapter.viewholder.regulation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.adapter.RegulationAdapter;

/**
 * RecyclerViewHolder for the regulationAdapter.
 * Used to display a datapoint that is type binary and has read-write access.
 */
public class SwitchDatapointViewHolder extends RegulationAdapter.DatapointViewHolder {
    private SwitchCompat binarySwitch;
    private TextView textViewName;

    public SwitchDatapointViewHolder(@NonNull ViewGroup parent,
                                     @NonNull RegulationAdapter.OnItemClickListener onSwitchClickListener,
                                     @NonNull RegulationAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_switch, parent, false));

        findViewsByID();
        setSwitchOnClickListener(onSwitchClickListener, adapter);
    }

    private void setSwitchOnClickListener(@NonNull RegulationAdapter.OnItemClickListener onSwitchClickListener,
                                          @NonNull RegulationAdapter adapter) {
        binarySwitch.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (onSwitchClickListener != null && position != RecyclerView.NO_POSITION) {
                if(binarySwitch.isChecked()){
                    onSwitchClickListener.onItemClick(adapter.getDataPointAt(position), "1");
                }else{
                    onSwitchClickListener.onItemClick(adapter.getDataPointAt(position), "0");
                }
            }
        });
    }

    private void findViewsByID() {
        textViewName = itemView.findViewById(R.id.textView_item);
        binarySwitch = itemView.findViewById(R.id.switch_item);
    }

    /**
     * Displays name of the given datapoint in a textView and a given value on the switch.
     * @param datapoint Datapoint to be displayed by the viewHolder
     * @param value Value to be displayed in the switch of the viewHolder
     */
    @Override
    public void onBindViewHolder(Datapoint datapoint, Optional<String> value) {
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