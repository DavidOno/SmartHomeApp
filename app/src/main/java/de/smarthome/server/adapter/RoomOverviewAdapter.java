package de.smarthome.server.adapter;


import android.app.Application;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.smarthome.model.configs.ChannelConfig;
import de.smarthome.model.impl.Datapoint;
import de.smarthome.model.impl.Function;
import de.smarthome.model.repository.Repository;
import de.smarthome.server.adapter.viewholder.roomoverview.DefaultViewHolder;
import de.smarthome.server.adapter.viewholder.roomoverview.StatusViewHolder;
import de.smarthome.server.adapter.viewholder.roomoverview.SwitchViewHolder;
import de.smarthome.server.adapter.viewholder.roomoverview.SwitchArrowViewHolder;


public class RoomOverviewAdapter extends RecyclerView.Adapter<RoomOverviewAdapter.ViewHolder>{
    List<Function> functionList;
    Map<Function, Function> functionMap;

    public static final int DEFAULT_VIEW_HOLDER = 0;
    public static final int SWITCH_VIEW_HOLDER = 1;
    public static final int SWITCH_ARROW_HOLDER = 2;
    public static final int STATUS_VIEW_HOLDER = 3;

    private OnItemClickListener listener;
    private OnSwitchClickListener switchClickListener;

    private String statusFunctionUID = null;
    private String statusFunctionValue = null;
    private Function functionToBeUpdated = null;

    private ChannelConfig channelConfig;

    public void setFunctionList(Map<Function, Function> functions, Application application) {
        functionList = new ArrayList<>(functions.keySet());
        functionMap = functions;

        channelConfig = Repository.getInstance(application).getSmartHomeChannelConfig();

        notifyDataSetChanged();
    }

    public void updateStatusValue(String changedStatusFunctionUID, String changedStatusFunctionValue){
        if(containsViewStatusFunction(changedStatusFunctionUID)){
            setStatusVariables(changedStatusFunctionUID, changedStatusFunctionValue);
            //TODO: Add to RegulationOverviewAdapter and get correct position
            notifyItemChanged(0);
        }
    }

    private boolean containsViewStatusFunction(String changedStatusFunctionUID) {
        for(Function function : functionList){
            if (functionMap.get(function) != null) {
                Function statusFunction = functionMap.get(function);

                //TODO: Right now in RoomOverview it can only be the first dataPoint (because only binary is shown)
                for(Datapoint datapoint : statusFunction.getDataPoints()){
                    if (changedStatusFunctionUID.equals(datapoint.getID())) {
                        functionToBeUpdated = function;
                        return true;
                    }
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
                    switchClickListener,
                    this
            );
        }else if(viewType == SWITCH_ARROW_HOLDER){
            return new SwitchArrowViewHolder(
                    parent,
                    listener,
                    switchClickListener,
                    this

            );
        }else if(viewType == STATUS_VIEW_HOLDER){
            return new StatusViewHolder(
                    parent,
                    listener,
                    this

            );
        }else{  //if(viewType == DEFAULT_VIEW_HOLDER){
            return new DefaultViewHolder(
                    parent,
                    listener,
                    this
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Function function = functionList.get(position);

        Optional<String> value = Optional.empty();
        value = getStatusValueString(function, value);

        holder.onBindViewHolder(holder, position, function, value);
    }

    private Optional<String> getStatusValueString(Function function, Optional<String> value) {
        if(functionToBeUpdated != null) {
            if(functionToBeUpdated.equals(function)){
                value = Optional.ofNullable(statusFunctionValue);

                setStatusVariables(null, null);
            }
        }
        return value;
    }

    private void setStatusVariables(String uID, String value) {
        statusFunctionUID = uID;
        statusFunctionValue = value;
    }

    @Override
    public int getItemCount() {
        return functionList.size();
    }

    @Override
    public int getItemViewType(int position){
        Function item = getFunctionAt(position);
        return channelConfig.getRoomOverviewItemViewType(channelConfig.findChannelByName(item));
    }


    public Function getFunctionAt(int position) {
        return functionList.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(Function function);
    }

    public void setOnItemClickListener(RoomOverviewAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnSwitchClickListener {
        void onItemClick(Function function, boolean isChecked);
    }

    public void setOnSwitchClickListener(RoomOverviewAdapter.OnSwitchClickListener listener) {
        this.switchClickListener = listener;
    }

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(ViewHolder holder, int position, Function function, Optional<String> value);

    }
}
