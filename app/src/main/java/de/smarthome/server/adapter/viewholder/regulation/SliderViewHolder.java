package de.smarthome.server.adapter.viewholder.regulation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.model.impl.Datapoint;
import de.smarthome.server.adapter.RegulationAdapter;

public class SliderViewHolder extends RegulationAdapter.ViewHolder{
    private TextView textView;
    private Slider slider;
    private RegulationAdapter adapter;
    private RegulationAdapter.OnItemClickListener onItemClickListener;

    public SliderViewHolder(@NonNull ViewGroup parent,
                          @NonNull RegulationAdapter.OnItemClickListener onItemClickListener,
                          @NonNull RegulationAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, parent, false));
        textView = itemView.findViewById(R.id.textView_item);

        slider = itemView.findViewById(R.id.sliderRange_item);

        this.onItemClickListener = onItemClickListener;
        this.adapter = adapter;

        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                    //not required
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int position = getAdapterPosition();
                if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(adapter.getDataPointAt(position), String.valueOf(slider.getValue()));
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RegulationAdapter.ViewHolder holder, int position, Datapoint datapoint, Optional<String> value) {
        textView.setText(datapoint.getName());

        value.ifPresent(s -> slider.setValue(Integer.parseInt(s)));
    }
}
