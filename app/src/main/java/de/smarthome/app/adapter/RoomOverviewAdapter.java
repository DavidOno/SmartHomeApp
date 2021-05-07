package de.smarthome.app.adapter;


import android.app.Application;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

    //TODO: Refactor
    private Map<String, String> mapFunctionIDAndValue = new LinkedHashMap<>();

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

    public void updateStatusValue(String changedStatusFunctionUID, String changedStatusFunctionValue){
        if(containsViewStatusFunction(changedStatusFunctionUID, changedStatusFunctionValue)){
            notifyItemChanged(getItemPosition());
        }
    }

    public void updateStatusValue2(Map<String, String> newInput){
        if(newInput.size() == 1){
            updateStatusValue(newInput.keySet().iterator().next(),
                    newInput.get(newInput.keySet().iterator().next()));
        }else{
            notifyDataSetChanged();
            for(String key: newInput.keySet()) {
                containsViewStatusFunction(key, newInput.get(key));
            }
        }
    }

    private int getItemPosition(){
        int position = 0;
        for (Function func : functionList){
            if(mapFunctionIDAndValue.containsKey(func.getID())){
                return position;
            }
            position++;
        }
        return -1;
    }

    private boolean containsViewStatusFunction(String changedStatusFunctionUID, String value) {
        for(Function function : functionList){
            //Has to be this check because: No Status function => value of map is null! so it contains the function
            if (functionMap.get(function) != null) {
                //TODO: remove after testing
                Function statusFunction = functionMap.get(function);

                for(Datapoint datapoint : functionMap.get(function).getDataPoints()){
                    String x = datapoint.getID();
                    if (changedStatusFunctionUID.equals(datapoint.getID())) {
                        mapFunctionIDAndValue.put(function.getID(), value);
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
        if(!mapFunctionIDAndValue.isEmpty()) {
            if(mapFunctionIDAndValue.containsKey(function.getID())){
                value = Optional.ofNullable(mapFunctionIDAndValue.get(function.getID()));

                removeStatusVariables(function.getID());
            }
        }
        return value;
    }

    private void removeStatusVariables(String uID) {
        mapFunctionIDAndValue.remove(uID);
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
