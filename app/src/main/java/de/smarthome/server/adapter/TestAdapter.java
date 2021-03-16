package de.smarthome.server.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.smarthome.R;
import de.smarthome.model.configs.ChannelConfig;
import de.smarthome.model.impl.Function;
import de.smarthome.model.repository.Repository;
import de.smarthome.server.adapter.viewholder.DefaultViewHolder;
import de.smarthome.server.adapter.viewholder.SwitchViewHolder;
import de.smarthome.server.adapter.viewholder.SwitchArrowViewHolder;


public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder>{
    List<Function> functionList;
    public static final int DEFAULT_VIEW_HOLDER = 0;
    public static final int SWITCH_VIEW_HOLDER = 1;
    public static final int SWITCH_ARROW_HOLDER = 2;

    private OnItemClickListener listener;
    private OnSwitchClickListener switchClickListener;

    private ChannelConfig channelConfig;

    public void setFunctionList(List<Function> functions) {
        functionList = functions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        channelConfig = Repository.getInstance().getChannelConfig();

        if(viewType == SWITCH_VIEW_HOLDER){
            return new SwitchViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_switch, parent, false),
                    listener,
                    switchClickListener,
                    this
            );
        }else if(viewType == SWITCH_ARROW_HOLDER){
            return new SwitchArrowViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_switch_arrow, parent, false),
                    listener,
                    switchClickListener,
                    this

            );
        }else {
            return new DefaultViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_default, parent, false),
                    listener,
                    this
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Function f = functionList.get(position);
        holder.onBindViewHolder(holder, position, f);
      /*  if(getItemViewType(position) == SWITCH_VIEW_HOLDER){
            Function f =  functionList.get(position);
            ((SwitchViewHolder) holder).textView.setText(f.getName());

        }else if(getItemViewType(position)  == TEST_VIEW_HOLDER){
            Function f =  functionList.get(position);
            holder.onBindViewHolder(holder, position, f);
        }else {
            Function f =  functionList.get(position);
            ((DefaultViewHolder) holder).textView.setText(f.getName());
        }*/
    }

    @Override
    public int getItemCount() {
        return functionList.size();
    }

    //TODO: Add all different ChannelTypes + compare mit ChannelConfig
    @Override
    public int getItemViewType(int position){
        Function item = getFunctionAt(position);
        channelConfig = Repository.getInstance().getChannelConfig();
        return channelConfig.getRoomOverviewItemViewType(channelConfig.findChannelByName(item));
          /* ) {
            return SWITCH_VIEW_HOLDER;
        } else if (item.getChannelType().contains(".BlindWithPos")) {
            return SWITCH_ARROW_HOLDER;
        } else {
            return DEFAULT_VIEW_HOLDER;
        }*/
    }


    public Function getFunctionAt(int position) {
        return functionList.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(Function function);
    }

    public void setOnItemClickListener(TestAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnSwitchClickListener {
        void onItemClick(Function function, boolean isChecked);
    }

    public void setOnSwitchClickListener(TestAdapter.OnSwitchClickListener listener) {
        this.switchClickListener = listener;
    }

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(ViewHolder holder, int position, Function function);

    }
}
