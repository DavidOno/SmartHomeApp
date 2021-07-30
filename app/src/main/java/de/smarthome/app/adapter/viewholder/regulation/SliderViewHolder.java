package de.smarthome.app.adapter.viewholder.regulation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.adapter.RegulationAdapter;

public class SliderViewHolder extends RegulationAdapter.ViewHolder{
    private TextView textViewName;
    private Slider slider;
    private RegulationAdapter adapter;

    public SliderViewHolder(@NonNull ViewGroup parent,
                            @NonNull RegulationAdapter.OnItemClickListener onItemClickListener,
                            @NonNull RegulationAdapter adapter,
                            @NonNull int type) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, parent, false));

        findViewsByID();
        setSliderStepSize(type);
        this.adapter = adapter;
        setSliderOnTouchListener(onItemClickListener, adapter);
    }

    private void setSliderStepSize(int type) {
        if(type == RegulationAdapter.INT_SLIDER_VIEW_HOLDER){
            slider.setStepSize(1);
        }
    }

    private void setSliderOnTouchListener(@NonNull RegulationAdapter.OnItemClickListener onItemClickListener,
                                          @NonNull RegulationAdapter adapter) {
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

    private void findViewsByID() {
        textViewName = itemView.findViewById(R.id.textView_item);
        slider = itemView.findViewById(R.id.sliderRange_item);
    }

    @Override
    public void onBindViewHolder(RegulationAdapter.ViewHolder holder, int position, Datapoint datapoint, Optional<String> value) {
        textViewName.setText(datapoint.getName().replace("_", " "));

        setCorrectValue(datapoint, value);
    }

    private void setCorrectValue(Datapoint datapoint, Optional<String> value) {
        if(adapter.getBoundaryMap().get(datapoint) != null){
            float min = -1;
            float max = -1;

            min = setSliderMin(datapoint, min);
            max = setSliderMax(datapoint, max);
            setSliderWithinBoundary(value, min, max);
        }else{
            if(value.isPresent()){
                float inputValue = Float.parseFloat(value.get());
                slider.setValue(inputValue);
            }
        }
    }

    private void setSliderWithinBoundary(Optional<String> value, float min, float max) {
        //Needed if Boundary gets changed and Server sends to high/low value for slider
        if(value.isPresent()){
            Float inputValue = Float.parseFloat(value.get());
            if(inputValue.compareTo(min) < 0 && min != -1){
                slider.setValue(min);
            }else if(inputValue.compareTo(max) > 0 && max != -1){
                slider.setValue(max);
            }else{
                slider.setValue(inputValue);
            }
        }
    }

    private float setSliderMax(Datapoint datapoint, float max) {
        if(adapter.getBoundaryMap().get(datapoint).getMax() != null){
            max = Float.parseFloat(adapter.getBoundaryMap().get(datapoint).getMax());
            slider.setValueTo(max);
        }
        return max;
    }

    private float setSliderMin(Datapoint datapoint, float min) {
        if(adapter.getBoundaryMap().get(datapoint).getMin() != null){
            min = Float.parseFloat(adapter.getBoundaryMap().get(datapoint).getMin());
            slider.setValueFrom(min);
        }
        return min;
    }
}