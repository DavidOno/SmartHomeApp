package de.smarthome.server.adapter.viewholder.regulation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;

import de.smarthome.R;
import de.smarthome.model.configs.ChannelDatapoint;
import de.smarthome.model.impl.Datapoint;
import de.smarthome.model.impl.Function;
import de.smarthome.server.adapter.RegulationAdapter;

public class DoubleSliderViewHolder extends RegulationAdapter.ViewHolder{
    private TextView textView;
    private Slider slider;
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
                    onItemClickListener.onItemClick(adapter.getDatapointAt(position), String.valueOf(slider.getValue()));
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RegulationAdapter.ViewHolder holder, int position, Datapoint datapoint) {
        this.textView.setText(datapoint.getName());
    }
}