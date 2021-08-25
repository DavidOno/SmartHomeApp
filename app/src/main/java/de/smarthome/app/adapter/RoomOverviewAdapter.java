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

import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.Function;
import de.smarthome.app.adapter.viewholder.roomoverview.DefaultFunctionViewHolder;
import de.smarthome.app.adapter.viewholder.roomoverview.StatusFunctionViewHolder;
import de.smarthome.app.adapter.viewholder.roomoverview.SwitchFunctionViewHolder;
import de.smarthome.app.adapter.viewholder.roomoverview.SwitchArrowFunctionViewHolder;
import de.smarthome.app.viewmodel.RoomOverviewViewModel;

/**
 * Adapter for the display of functions in a recyclerView
 */
public class RoomOverviewAdapter extends RecyclerView.Adapter<RoomOverviewAdapter.FunctionViewHolder>{
    private List<Function> functionList;
    private Map<Function, Function> functionMap;
    private Map<String, String> statusValueMap = new LinkedHashMap<>();

    private OnItemClickListener listener;
    private OnSwitchClickListener switchClickListener;
    private RoomOverviewViewModel viewViewModel;

    public static final int DEFAULT_VIEW_HOLDER = 0;
    public static final int SWITCH_VIEW_HOLDER = 1;
    public static final int SWITCH_ARROW_HOLDER = 2;
    public static final int STATUS_VIEW_HOLDER = 3;

    public RoomOverviewAdapter(FragmentActivity parent){
        viewViewModel = new ViewModelProvider(parent).get(RoomOverviewViewModel.class);
    }

    /**
     * Initialises the dataset of the adapter and notifies the adapter that the dataset has changed
     */
    public void initialiseAdapter(Map<Function, Function> functions) {
        setFunctionList(functions);
        setFunctionMap(functions);
        notifyDataSetChanged();
    }

    private void setFunctionMap(Map<Function, Function> functions) {
        functionMap = functions;
    }

    private void setFunctionList(Map<Function, Function> functions) {
        functionList = new ArrayList<>(functions.keySet());
    }

    /**
     * Checks if the adapter contains the datapoint that has been changed
     * and notifies the adapter which item needs to be updated. Only notifies one item.
     */
    public void updateSingleFunctionStatusValue(String changedStatusFunctionUID, String changedStatusFunctionValue){
        if(hasStatusFunction(changedStatusFunctionUID, changedStatusFunctionValue)){
            notifyItemChanged(getItemPositionInStatusValueMap());
        }
    }

    /**
     * Checks if the adapter contains the datapoints that have been changed
     * and notifies the adapter which items need to be updated
     */
    public void updateMultipleFunctionStatusValues(Map<String, String> newInput){
        if(newInput.size() == 1){
            for(String key : newInput.keySet()){
                updateSingleFunctionStatusValue(key, newInput.get(key));
            }
        }else{
            notifyDataSetChanged();
            for(String key: newInput.keySet()) {
                hasStatusFunction(key, newInput.get(key));
            }
        }
    }

    private int getItemPositionInStatusValueMap(){
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
            //Has to be this check because: No Status function => value of map is null! so it contains something
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
    public FunctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SWITCH_VIEW_HOLDER){
            return new SwitchFunctionViewHolder(parent,switchClickListener,this);

        }else if(viewType == SWITCH_ARROW_HOLDER){
            return new SwitchArrowFunctionViewHolder(parent,listener,switchClickListener,this);

        }else if(viewType == STATUS_VIEW_HOLDER){
            return new StatusFunctionViewHolder(parent,listener,this);

        }else{
            return new DefaultFunctionViewHolder(parent,listener,this);
        }
    }

    /**
     * Gives the viewHolder the function and the current value
     */
    @Override
    public void onBindViewHolder(@NonNull FunctionViewHolder holder, int position) {
        Function function = functionList.get(position);

        Optional<String> value = Optional.empty();
        value = getStatusValueString(function, value);

        holder.onBindViewHolder(function, value);
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

    /**
     * Returns the amount of items in the functionList
     * @return size of functionList
     */
    @Override
    public int getItemCount() {
        return functionList.size();
    }

    @Override
    public int getItemViewType(int position){
        Function item = getFunctionAt(position);
        return viewViewModel.getChannelConfig().getRoomOverviewItemViewType(viewViewModel.getChannelConfig().findChannelByName(item));
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

    public abstract static class FunctionViewHolder extends RecyclerView.ViewHolder {

        protected FunctionViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(Function function, Optional<String> value);

    }
}