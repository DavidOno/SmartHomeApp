package de.smarthome.app.adapter;


import android.app.Application;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.smarthome.app.model.configs.BoundaryDataPoint;
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.configs.ChannelDatapoint;
import de.smarthome.app.repository.Repository;
import de.smarthome.app.adapter.viewholder.regulation.ReadViewHolder;
import de.smarthome.app.adapter.viewholder.regulation.SliderViewHolder;
import de.smarthome.app.adapter.viewholder.regulation.StepViewHolder;
import de.smarthome.app.adapter.viewholder.regulation.SwitchViewHolder;


public class RegulationAdapter extends RecyclerView.Adapter<RegulationAdapter.ViewHolder>{
    private List<Datapoint> dataPointList;
    private Map<Datapoint, Datapoint> dataPointMap;
    private Map<Datapoint, BoundaryDataPoint> boundaryMap;

    public static final int STEP_VIEW_HOLDER = 0;
    public static final int SWITCH_VIEW_HOLDER = 1;
    public static final int INT_SLIDER_VIEW_HOLDER = 2;
    public static final int FLOAT_SLIDER_VIEW_HOLDER = 3;
    public static final int READ_VIEW_HOLDER = 4;

    private OnItemClickListener listener;

    private Map<String, String> statusValueMap = new LinkedHashMap<>();

    private ChannelConfig channelConfig;
    private Repository repository;

    public void setBoundaryMap(Map<Datapoint, BoundaryDataPoint> newData){
        boundaryMap = newData;
    }

    public Map<Datapoint, BoundaryDataPoint> getBoundaryMap() {
        return boundaryMap;
    }

    public void setDataPointList(Map<Datapoint, Datapoint> dataPoints, Application application) {
        dataPointList = new ArrayList<>(dataPoints.keySet());
        dataPointMap = dataPoints;

        repository = Repository.getInstance(application);
        channelConfig = repository.getSmartHomeChannelConfig();

        requestCurrentStatus();

        notifyDataSetChanged();
    }

    private void requestCurrentStatus(){
        List<String> requestList = new ArrayList<>();
        for(Datapoint dp : dataPointList){
            //Status Function always have the same amount of DataPoints BUT only a few of them get answers
            if(dataPointMap.get(dp) != null && !dataPointMap.get(dp).getID().equals(dp.getID())){
                requestList.add(dataPointMap.get(dp).getID());
            }
        }
        repository.requestGetValue(requestList);
    }

    public void updateStatusValue(String changedStatusFunctionUID, String changedStatusFunctionValue){
        if(containsViewStatusFunction(changedStatusFunctionUID, changedStatusFunctionValue)){
            notifyItemChanged(getItemPosition());
        }
    }

    public void updateMultipleStatusValues(Map<String, String> newInput) {
        if (newInput.size() == 1) {
            updateStatusValue(newInput.keySet().iterator().next(),
                    newInput.get(newInput.keySet().iterator().next()));
        } else {
            notifyDataSetChanged();
            for (String key : newInput.keySet()) {
                containsViewStatusFunction(key, newInput.get(key));
            }
        }
    }

    private int getItemPosition(){
        int position = 0;
        for (Datapoint dp : dataPointList){
            if(statusValueMap.containsKey(dp.getID())){
                return position;
            }
            position++;
        }
        return -1;
    }

    private boolean containsViewStatusFunction(String changedStatusFunctionUID, String value) {
        for(Datapoint datapoint : dataPointList){
            if (dataPointMap.get(datapoint) != null && changedStatusFunctionUID.equals(dataPointMap.get(datapoint).getID())) {
                statusValueMap.put(datapoint.getID(), value);
                return true;
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

        }else if(viewType == INT_SLIDER_VIEW_HOLDER){
            return new SliderViewHolder(
                    parent,
                    listener,
                    this,
                    INT_SLIDER_VIEW_HOLDER
            );

        }else if(viewType == FLOAT_SLIDER_VIEW_HOLDER){
            return new SliderViewHolder(
                    parent,
                    listener,
                    this,
                    FLOAT_SLIDER_VIEW_HOLDER
            );

        }else if(viewType == READ_VIEW_HOLDER){
            return new ReadViewHolder(
                    parent

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
        if(!statusValueMap.isEmpty() && statusValueMap.containsKey(datapoint.getID())){
                value = Optional.ofNullable(statusValueMap.get(datapoint.getID()));

                removeStatusVariables(datapoint.getID());
        }
        return value;
    }

    private void removeStatusVariables(String uID) {
        statusValueMap.remove(uID);
    }

    @Override
    public int getItemCount() {
        return dataPointList.size();
    }

    @Override
    public int getItemViewType(int position){
        Optional<ChannelDatapoint> channelDatapoint = channelConfig.findChannelByName(repository.getSelectedFunction()).getCorrespondingChannelDataPoint(dataPointList.get(position));
        return channelDatapoint.map(datapoint -> channelConfig.getRegulationItemViewType(datapoint)).orElse(-1);
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

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {

        protected ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(ViewHolder holder, int position, Datapoint datapoint, Optional<String> value);

    }
}
