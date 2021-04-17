package de.smarthome.app.adapter.viewholder.regulation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.RangeSlider;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.adapter.RegulationAdapter;

public class DoubleSliderViewHolder extends RegulationAdapter.ViewHolder{
    private TextView textView;
    private RangeSlider slider;
    private RegulationAdapter adapter;
    private RegulationAdapter.OnItemClickListener onItemClickListener;

    public DoubleSliderViewHolder(@NonNull ViewGroup parent,
                                    @NonNull RegulationAdapter.OnItemClickListener onItemClickListener,
                                    @NonNull RegulationAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_double_slider, parent, false));
        textView = itemView.findViewById(R.id.textView_item);

        slider = itemView.findViewById(R.id.sliderRange_double_item);

        this.onItemClickListener = onItemClickListener;
        this.adapter = adapter;

        slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    //TODO: Right now all sliders are usable! Need to know witch one should be used for inputting new value!!
                    onItemClickListener.onItemClick(adapter.getDataPointAt(position), String.valueOf(slider.getValues().get(0)));
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RegulationAdapter.ViewHolder holder, int position, Datapoint datapoint, Optional<String> value) {
        this.textView.setText(datapoint.getName());

        //value.ifPresent(s -> slider.setValues(Integer.parseInt(s)));
    }
}