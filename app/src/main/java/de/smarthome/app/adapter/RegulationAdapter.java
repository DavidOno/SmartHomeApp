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

import de.smarthome.app.model.configs.BoundaryDataPoint;
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.configs.ChannelDatapoint;
import de.smarthome.app.adapter.viewholder.regulation.ReadViewHolder;
import de.smarthome.app.adapter.viewholder.regulation.SliderViewHolder;
import de.smarthome.app.adapter.viewholder.regulation.StepViewHolder;
import de.smarthome.app.adapter.viewholder.regulation.SwitchViewHolder;
import de.smarthome.app.viewmodel.RegulationViewModel;

public class RegulationAdapter extends RecyclerView.Adapter<RegulationAdapter.ViewHolder>{
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

    public void updateSingleDatapointStatusValue(String changedStatusFunctionUID, String changedStatusFunctionValue){
        if(hasStatusFunction(changedStatusFunctionUID, changedStatusFunctionValue)){
            notifyItemChanged(getItemPosition());
        }
    }

    public void updateMultipleDatapointStatusValues(Map<String, String> newInput) {
        if (newInput.size() == 1) {
            updateSingleDatapointStatusValue(newInput.keySet().iterator().next(),
                    newInput.get(newInput.keySet().iterator().next()));
        } else {
            notifyDataSetChanged();
            for (String key : newInput.keySet()) {
                hasStatusFunction(key, newInput.get(key));
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


    public abstract static class ViewHolder extends RecyclerView.ViewHolder {

        protected ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(ViewHolder holder, int position, Datapoint datapoint, Optional<String> value);

    }
}