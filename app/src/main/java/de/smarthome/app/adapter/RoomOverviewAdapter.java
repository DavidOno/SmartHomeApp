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

    private Map<String, String> statusValueMap = new LinkedHashMap<>();

    private Repository repository;
    private ChannelConfig channelConfig;

    public void initialiseAdapter(Map<Function, Function> functions, Application application) {
        setFunctionList(functions);
        setFunctionMap(functions);

        repository = Repository.getInstance(application);
        channelConfig = repository.getChannelConfig();

        requestFunctionCurrentStatus();
        notifyDataSetChanged();
    }

    private void setFunctionMap(Map<Function, Function> functions) {
        functionMap = functions;
    }

    private void setFunctionList(Map<Function, Function> functions) {
        functionList = new ArrayList<>(functions.keySet());
    }

    private void requestFunctionCurrentStatus(){
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
            repository.requestGetValue(requestList);
        }
    }

    public void updateSingleFunctionStatusValue(String changedStatusFunctionUID, String changedStatusFunctionValue){
        if(hasStatusFunction(changedStatusFunctionUID, changedStatusFunctionValue)){
            notifyItemChanged(getItemPosition());
        }
    }

    public void updateMultipleFunctionStatusValues(Map<String, String> newInput){
        if(newInput.size() == 1){
            updateSingleFunctionStatusValue(newInput.keySet().iterator().next(),
                    newInput.get(newInput.keySet().iterator().next()));
        }else{
            notifyDataSetChanged();
            for(String key: newInput.keySet()) {
                hasStatusFunction(key, newInput.get(key));
            }
        }
    }

    private int getItemPosition(){
        int position = 0;
        for (Function func : functionList){
            if(statusValueMap.containsKey(func.getID())){
                return position;
            }
            position++;
        }
        return -1;
    }

    private boolean hasStatusFunction(String changedStatusFunctionUID, String value) {
        for(Function function : functionList){
            //Has to be this check because: No Status function => value of map is null! so it contains the function
            if (functionMap.get(function) != null) {

                for(Datapoint datapoint : functionMap.get(function).getDataPoints()){
                    if (changedStatusFunctionUID.equals(datapoint.getID())) {
                        statusValueMap.put(function.getID(), value);
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
        }else{
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
        if(!statusValueMap.isEmpty() &&
                statusValueMap.containsKey(function.getID())){
            value = Optional.ofNullable(statusValueMap.get(function.getID()));
            removeStatusVariables(function.getID());
        }
        return value;
    }

    private void removeStatusVariables(String uID) {
        statusValueMap.remove(uID);
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

        protected ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(ViewHolder holder, int position, Function function, Optional<String> value);

    }
}