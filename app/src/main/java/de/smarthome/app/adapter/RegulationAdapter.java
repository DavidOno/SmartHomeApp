package de.smarthome.app.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.smarthome.app.adapter.viewholder.regulation.ReadDatapointViewHolder;
import de.smarthome.app.adapter.viewholder.regulation.SliderDatapointViewHolder;
import de.smarthome.app.adapter.viewholder.regulation.StepDatapointViewHolder;
import de.smarthome.app.model.configs.BoundaryDataPoint;
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.configs.ChannelDatapoint;
import de.smarthome.app.adapter.viewholder.regulation.SwitchDatapointViewHolder;
import de.smarthome.app.viewmodel.RegulationViewModel;

/**
 * Adapter for the display of datapoints in a recyclerView
 */
public class RegulationAdapter extends RecyclerView.Adapter<RegulationAdapter.DatapointViewHolder>{
    private List<Datapoint> dataPointList;
    private Map<Datapoint, Datapoint> dataPointMap;
    private Map<Datapoint, BoundaryDataPoint> boundaryMap;
    private Map<String, String> statusValueMap = new LinkedHashMap<>();

    private OnItemClickListener listener;
    private RegulationViewModel viewModel;

    public static final int STEP_VIEW_HOLDER = 0;
    public static final int SWITCH_VIEW_HOLDER = 1;
    public static final int INT_SLIDER_VIEW_HOLDER = 2;
    public static final int FLOAT_SLIDER_VIEW_HOLDER = 3;
    public static final int READ_VIEW_HOLDER = 4;

    public RegulationAdapter(FragmentActivity parent){
        viewModel = new ViewModelProvider(parent).get(RegulationViewModel.class);
    }

    public void setBoundaryMap(Map<Datapoint, BoundaryDataPoint> newData){
        boundaryMap = newData;
    }

    public Map<Datapoint, BoundaryDataPoint> getBoundaryMap() {
        return boundaryMap;
    }

    /**
     * Initialises the dataset of the adapter and notifies the adapter that the dataset has changed
     */
    public void initialiseAdapter(Map<Datapoint, Datapoint> dataPoints) {
        setDataPointList(dataPoints);
        setDataPointMap(dataPoints);
        notifyDataSetChanged();
    }

    private void setDataPointMap(Map<Datapoint, Datapoint> dataPoints) {
        dataPointMap = dataPoints;
    }

    private void setDataPointList(Map<Datapoint, Datapoint> dataPoints) {
        dataPointList = new ArrayList<>(dataPoints.keySet());
    }

    /**
     * Checks if the adapter contains the datapoint that has been changed
     * and notifies the adapter which item needs to be updated. Only notifies one item.
     */
    public void updateSingleDatapointStatusValue(String changedStatusFunctionUID, String changedStatusFunctionValue){
        if(hasStatusFunction(changedStatusFunctionUID, changedStatusFunctionValue)){
            notifyItemChanged(getItemPositionInStatusValueMap());
        }
    }

    /**
     * Checks if the adapter contains the datapoints that have been changed
     * and notifies the adapter which items need to be updated.
     */
    public void updateMultipleDatapointStatusValues(Map<String, String> newInput) {
        if (newInput.size() == 1) {
            for(String key : newInput.keySet()){
                updateSingleDatapointStatusValue(key, newInput.get(key));
            }
        } else {
            notifyDataSetChanged();
            for (String key : newInput.keySet()) {
                hasStatusFunction(key, newInput.get(key));
            }
        }
    }

    private int getItemPositionInStatusValueMap(){
        int position = 0;
        for (Datapoint dp : dataPointList){
            if(statusValueMap.containsKey(dp.getID())){
                return position;
            }
            position++;
        }
        return -1;
    }

    private boolean hasStatusFunction(String changedStatusFunctionUID, String value) {
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
    public DatapointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SWITCH_VIEW_HOLDER){
            return new SwitchDatapointViewHolder(parent, listener,this);

        }else if(viewType == STEP_VIEW_HOLDER){
            return new StepDatapointViewHolder(parent, listener,this);

        }else if(viewType == INT_SLIDER_VIEW_HOLDER){
            return new SliderDatapointViewHolder(parent, listener,this, INT_SLIDER_VIEW_HOLDER);

        }else if(viewType == FLOAT_SLIDER_VIEW_HOLDER){
            return new SliderDatapointViewHolder(parent, listener,this, FLOAT_SLIDER_VIEW_HOLDER);

        }else if(viewType == READ_VIEW_HOLDER){
            return new ReadDatapointViewHolder(parent);

        }else {
            throw new IllegalArgumentException("Input type not known");
        }
    }

    /**
     * Gives the viewHolder the datapoint and the current value
     */
    @Override
    public void onBindViewHolder(@NonNull DatapointViewHolder holder, int position) {
        Datapoint dp = getDataPointAt(position);

        Optional<String> value = Optional.empty();
        value = getStatusValueString(dp, value);

        holder.onBindViewHolder(dp, value);
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

    /**
     * Returns the amount of items in the dataPointList
     * @return size of dataPointList
     */
    @Override
    public int getItemCount() {
        return dataPointList.size();
    }

    @Override
    public int getItemViewType(int position){
        Optional<ChannelDatapoint> channelDatapoint = viewModel.getChannelConfig().findChannelByName(viewModel.getSelectedFunction()).getCorrespondingChannelDataPoint(dataPointList.get(position));
        return channelDatapoint.map(datapoint -> viewModel.getChannelConfig().getRegulationItemViewType(datapoint)).orElse(-1);
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

    public abstract static class DatapointViewHolder extends RecyclerView.ViewHolder {

        protected DatapointViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(Datapoint datapoint, Optional<String> value);

    }
}