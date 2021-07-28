package de.smarthome.app.adapter.viewholder.regulation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.skydoves.colorpickerpreference.ColorPickerView;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.adapter.RegulationAdapter;
import de.smarthome.app.model.Datapoint;

public class ColorpickerViewHolder extends RegulationAdapter.ViewHolder {

    private TextView textViewName;
    private ColorPickerView colorPickerView;

    public ColorpickerViewHolder(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_colorpicker, parent, false));
        colorPickerView = itemView.findViewById(R.id.colorPickerView);
//        textViewName = itemView.findViewById(R.id.)

        colorPickerView.setColorListener(colorEnvelope -> {
            System.out.println(colorEnvelope.getColor());
            System.out.println(colorEnvelope.getColorHtml());
            System.out.println(colorEnvelope.getColorRGB());
        });
    }

    @Override
    public void onBindViewHolder(RegulationAdapter.ViewHolder holder, int position, Datapoint datapoint, Optional<String> value) {
        colorPickerView.setPreferenceName(datapoint.getName().replace("_", " "));
    }
}
