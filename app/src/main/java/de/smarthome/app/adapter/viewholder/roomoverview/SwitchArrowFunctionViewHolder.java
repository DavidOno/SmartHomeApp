package de.smarthome.app.adapter.viewholder.roomoverview;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.Function;
import de.smarthome.app.adapter.RoomOverviewAdapter;

/**
 * RecyclerViewHolder for the roomOverviewAdapter.
 * Used to display a function which first datapoint is type binary with read/write access.
 */
public class SwitchArrowFunctionViewHolder extends RoomOverviewAdapter.FunctionViewHolder {
    private SwitchCompat binarySwitch;
    private TextView textViewName;

    public SwitchArrowFunctionViewHolder(@NonNull ViewGroup parent,
                                         @NonNull RoomOverviewAdapter.OnItemClickListener onItemClickListener,
                                         @NonNull RoomOverviewAdapter.OnSwitchClickListener onSwitchClickListener,
                                         @NonNull RoomOverviewAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_switch_arrow, parent, false));

        findViewById();
        setOnClickListenerSwitch(onSwitchClickListener, adapter);
        setOnClickListener(onItemClickListener, adapter);

    }

    private void setOnClickListener(@NonNull RoomOverviewAdapter.OnItemClickListener onItemClickListener, @NonNull RoomOverviewAdapter adapter) {
        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemClick(adapter.getFunctionAt(position));
            }
        });
    }

    private void setOnClickListenerSwitch(@NonNull RoomOverviewAdapter.OnSwitchClickListener onSwitchClickListener, @NonNull RoomOverviewAdapter adapter) {
        binarySwitch.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (onSwitchClickListener != null && position != RecyclerView.NO_POSITION) {
                onSwitchClickListener.onItemClick(adapter.getFunctionAt(position),  binarySwitch.isChecked());
            }
        });
    }

    private void findViewById() {
        textViewName = itemView.findViewById(R.id.textView_item);
        binarySwitch = itemView.findViewById(R.id.switch_item);
    }

    /**
     * Displays name of the given function in a textView and a given value on the switch.
     * @param function Function to be displayed by the viewHolder
     * @param value Value to be displayed in the switch of the viewHolder
     */
    @Override
    public void onBindViewHolder(Function function, Optional<String> value) {
        textViewName.setText(function.getName().replace("_", " "));
        setSwitchByValue(value);
    }

    private void setSwitchByValue(Optional<String> value) {
        if(value.isPresent()) {
            if (value.get().equals("true")) {
                binarySwitch.setChecked(true);

            } else if (value.get().equals("false")) {
                binarySwitch.setChecked(false);
            }
        }
    }
}