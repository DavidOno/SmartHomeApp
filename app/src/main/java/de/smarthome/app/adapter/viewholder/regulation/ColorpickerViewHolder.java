package de.smarthome.app.adapter.viewholder.regulation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.adapter.RegulationAdapter;
import de.smarthome.app.model.Datapoint;
//import petrov.kristiyan.colorpicker.ColorPicker;

public class ColorpickerViewHolder extends RegulationAdapter.ViewHolder {

//    private Button buttonColorpicker;
//    private

    public ColorpickerViewHolder(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_colorpicker, parent, false));
//        buttonColorpicker = itemView.findViewById(R.id.button_colorpicker);
    }

    @Override
    public void onBindViewHolder(RegulationAdapter.ViewHolder holder, int position, Datapoint datapoint, Optional<String> value) {

    }
}
