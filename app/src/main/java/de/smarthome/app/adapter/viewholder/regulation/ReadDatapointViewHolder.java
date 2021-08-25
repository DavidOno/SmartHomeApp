package de.smarthome.app.adapter.viewholder.regulation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.adapter.RegulationAdapter;

/**
 * RecyclerViewHolder for the regulationAdapter.
 * Used to display a datapoint that has only has read access and no functionality
 */
public class ReadDatapointViewHolder extends RegulationAdapter.DatapointViewHolder {
    private TextView textViewName;
    private TextView textViewOutput;

    public ReadDatapointViewHolder(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_read, parent, false));
        findViewsByID();
    }

    private void findViewsByID() {
        textViewName = itemView.findViewById(R.id.textView_item);
        textViewOutput = itemView.findViewById(R.id.textView_read_output_item);
    }

    /**
     * Displays name of the given datapoint and a given value in the textViews
     * @param datapoint Datapoint to be displayed by the viewHolder
     * @param value Value to be displayed in a textView of the viewHolder
     */
    @Override
    public void onBindViewHolder(Datapoint datapoint, Optional<String> value) {
        textViewName.setText(datapoint.getName().replace("_", " "));
        value.ifPresent(s -> textViewOutput.setText(s));
    }
}