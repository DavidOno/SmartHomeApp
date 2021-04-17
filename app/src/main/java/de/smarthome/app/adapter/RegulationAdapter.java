package de.smarthome.app.adapter;


import android.app.Application;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.model.impl.Datapoint;
import de.smarthome.app.repository.Repository;
import de.smarthome.app.adapter.viewholder.regulation.DoubleSliderViewHolder;
import de.smarthome.app.adapter.viewholder.regulation.ReadViewHolder;
import de.smarthome.app.adapter.viewholder.regulation.SliderViewHolder;
import de.smarthome.app.adapter.viewholder.regulation.StepViewHolder;
import de.smarthome.app.adapter.viewholder.regulation.SwitchViewHolder;


public class RegulationAdapter extends RecyclerView.Adapter<RegulationAdapter.ViewHolder>{
    List<Datapoint> dataPointList;
    Map<Datapoint, Datapoint> dataPointMap;

    public static final int STEP_VIEW_HOLDER = 0;
    public static final int SWITCH_VIEW_HOLDER = 1;
    public static final int SLIDER_VIEW_HOLDER = 2;
    public static final int DOUBLE_SLIDER_VIEW_HOLDER = 3;
    public static final int READ_VIEW_HOLDER = 4;

    private OnItemClickListener listener;
    private OnSwitchClickListener switchClickListener;

    private String statusFunctionDataPointUID = null;
    private String statusFunctionDataPointValue = null;
    private Datapoint dataPointToBeUpdated = null;

    private ChannelConfig channelConfig;
    private Repository repository;

    public void setDataPointList(Map<Datapoint, Datapoint> dataPoints, Application application) {
        dataPointList = new ArrayList<>(dataPoints.keySet());
        dataPointMap = dataPoints;

        repository = Repository.getInstance(application);
        channelConfig = repository.getSmartHomeChannelConfig();

        requestCurrentStatus();

        notifyDataSetChanged();
    }

    private void requestCurrentStatus(){
        //TODO: Check if the server response is for the status method if not function has to get the status id
        for(Datapoint dp : dataPointList){
            if(dataPointMap.get(dp) != null){
                repository.requestGetValue(dp.getID());
            }
        }
    }

    public void updateStatusValue(String changedStatusFunctionUID, String changedStatusFunctionValue){
        if(containsViewStatusFunction(changedStatusFunctionUID)){
            setStatusVariables(changedStatusFunctionUID, changedStatusFunctionValue);
            notifyItemChanged(getItemPosition());
        }
    }

    private int getItemPosition(){
        int position = 0;
        for (Datapoint dp : dataPointList){
            if(dp.equals(dataPointToBeUpdated)){
                return position;
            }
            position++;
        }
        return -1;
    }

    private boolean containsViewStatusFunction(String changedStatusFunctionUID) {
        for(Datapoint datapoint : dataPointList){
            if (dataPointMap.get(datapoint) != null) {
                Datapoint statusDataPoint = dataPointMap.get(datapoint);

                if (changedStatusFunctionUID.equals(statusDataPoint.getID())) {
                    dataPointToBeUpdated  = datapoint;
                    return true;
                }

            }
        }
        return false;
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
        Datapoint dp = getDataPointAt(position);

        Optional<String> value = Optional.empty();
        value = getStatusValueString(dp, value);

        holder.onBindViewHolder(holder, position, dp, value);
    }

    private Optional<String> getStatusValueString(Datapoint datapoint, Optional<String> value) {
        if(dataPointToBeUpdated != null) {
            if(dataPointToBeUpdated.equals(datapoint)){
                value = Optional.ofNullable(statusFunctionDataPointValue);

                setStatusVariables(null, null);
            }
        }
        return value;
    }

    private void setStatusVariables(String uID, String value) {
        statusFunctionDataPointUID = uID;
        statusFunctionDataPointValue = value;
    }

    @Override
    public int getItemCount() {
        return dataPointList.size();
    }

    @Override
    public int getItemViewType(int position){
        //TODO: Optional<> is used but not checked
        return channelConfig.getRegulationItemViewType(
                channelConfig.findChannelByName(repository.getSelectedFunction()).getCorrespondingChannelDataPoint(dataPointList.get(position)).get());
    }


    public Datapoint getDataPointAt(int position) {
        return dataPointList.get(position);
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

        public abstract void onBindViewHolder(ViewHolder holder, int position, Datapoint datapoint, Optional<String> value);

    }
}
