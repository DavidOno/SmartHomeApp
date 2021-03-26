package de.smarthome.server.adapter;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.smarthome.model.configs.ChannelConfig;
import de.smarthome.model.configs.ChannelDatapoint;
import de.smarthome.model.impl.Datapoint;
import de.smarthome.model.repository.Repository;
import de.smarthome.server.adapter.viewholder.regulation.DoubleSliderViewHolder;
import de.smarthome.server.adapter.viewholder.regulation.ReadViewHolder;
import de.smarthome.server.adapter.viewholder.regulation.SliderViewHolder;
import de.smarthome.server.adapter.viewholder.regulation.StepViewHolder;
import de.smarthome.server.adapter.viewholder.regulation.SwitchViewHolder;


public class RegulationAdapter extends RecyclerView.Adapter<RegulationAdapter.ViewHolder>{
    List<Datapoint> datapointList;

    public static final int STEP_VIEW_HOLDER = 0;
    public static final int SWITCH_VIEW_HOLDER = 1;
    public static final int SLIDER_VIEW_HOLDER = 2;
    public static final int DOUBLE_SLIDER_VIEW_HOLDER = 3;
    public static final int READ_VIEW_HOLDER = 4;

    private OnItemClickListener listener;
    private OnSwitchClickListener switchClickListener;

    private ChannelConfig channelConfig;
    private Repository repository;
    public void setDatapointList(List<Datapoint> datapoints) {
        this.datapointList = datapoints;
        repository = Repository.getInstance();
        channelConfig = repository.getSmartHomeChannelConfig();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == SWITCH_VIEW_HOLDER){
            return new SwitchViewHolder(
                    parent,
                    listener,
                    this
            );
        }else if(viewType == STEP_VIEW_HOLDER){
            return new StepViewHolder(
                    parent,
                    listener,
                    this
            );
        }else if(viewType == SLIDER_VIEW_HOLDER){
            return new SliderViewHolder(
                    parent,
                    listener,
                    this
            );
        }else if(viewType == DOUBLE_SLIDER_VIEW_HOLDER){
            return new DoubleSliderViewHolder(
                    parent,
                    listener,
                    this
            );
        }else if(viewType == READ_VIEW_HOLDER){
            return new ReadViewHolder(
                    parent,
                    listener,
                    this
            );
        }else {
            throw new IllegalArgumentException("Input type not known");
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Datapoint dp = datapointList.get(position);
        holder.onBindViewHolder(holder, position, dp);
    }

    @Override
    public int getItemCount() {
        return datapointList.size();
    }

    @Override
    public int getItemViewType(int position){
        //TODO: This code is terrible to read. It has to be reworked.
        return channelConfig.getRegulationItemViewType(
                channelConfig.findChannelByName(repository.getSelectedFunction()).getDatapoints().get(position));
    }


    public Datapoint getDatapointAt(int position) {
        return datapointList.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(Datapoint datapoint, String value);
    }

    public void setOnItemClickListener(RegulationAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnSwitchClickListener {
        void onItemClick(Datapoint datapoint, boolean isChecked);
    }

    public void setOnSwitchClickListener(RegulationAdapter.OnSwitchClickListener listener) {
        this.switchClickListener = listener;
    }

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(ViewHolder holder, int position, Datapoint datapoint);

    }
}
