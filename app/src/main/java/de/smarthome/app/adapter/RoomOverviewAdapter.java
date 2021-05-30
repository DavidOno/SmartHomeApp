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
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.Function;
import de.smarthome.app.repository.Repository;
import de.smarthome.app.adapter.viewholder.roomoverview.DefaultViewHolder;
import de.smarthome.app.adapter.viewholder.roomoverview.StatusViewHolder;
import de.smarthome.app.adapter.viewholder.roomoverview.SwitchViewHolder;
import de.smarthome.app.adapter.viewholder.roomoverview.SwitchArrowViewHolder;


public class RoomOverviewAdapter extends RecyclerView.Adapter<RoomOverviewAdapter.ViewHolder>{
    List<Function> functionList;
    Map<Function, Function> functionMap;

    List<String> requestList = new ArrayList<>();
    public static final int DEFAULT_VIEW_HOLDER = 0;
    public static final int SWITCH_VIEW_HOLDER = 1;
    public static final int SWITCH_ARROW_HOLDER = 2;
    public static final int STATUS_VIEW_HOLDER = 3;

    private OnItemClickListener listener;
    private OnSwitchClickListener switchClickListener;

    private String statusFunctionUID = null;
    private String statusFunctionValue = null;
    private Function functionToBeUpdated = null;

    private Repository repository;
    private ChannelConfig channelConfig;

    public void setFunctionList(Map<Function, Function> functions, Application application) {
        functionList = new ArrayList<>(functions.keySet());
        functionMap = functions;

        repository = Repository.getInstance(application);
        channelConfig = repository.getSmartHomeChannelConfig();

        requestCurrentStatus();

        notifyDataSetChanged();
    }

    private void requestCurrentStatus(){
        //TODO: Check if the server response is for the status method if not function has to get the status id
        for(Function func : functionList){
            if(channelConfig.isFirstDataPointBinary(func)){
                if(functionMap.get(func) != null){
                    requestList.add(functionMap.get(func).getDataPoints().get(0).getID());
                }
            }else{
                //Check if the function has a StatusViewHolder
                if(STATUS_VIEW_HOLDER == channelConfig.getRoomOverviewItemViewType(channelConfig.findChannelByName(func))){
                    requestList.add(functionMap.get(func).getDataPoints().get(0).getID());
                }
            }
        }
        if(!requestList.isEmpty()){
            if(requestList.size() == 1){
                //repository.requestGetValue(requestList.get(0));
            }else{
                //repository.requestGetValue2(requestList);
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
        for (Function func : functionList){
            if(func.equals(functionToBeUpdated)){
                return position;
            }
            position++;
        }
        return -1;
    }

    private boolean containsViewStatusFunction(String changedStatusFunctionUID) {
        for(Function function : functionList){
            if (functionMap.get(function) != null) {
                Function statusFunction = functionMap.get(function);

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

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(ViewHolder holder, int position, Function function, Optional<String> value);

    }
}
