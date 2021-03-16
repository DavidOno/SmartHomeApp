package de.smarthome.model.configs;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import de.smarthome.R;
import de.smarthome.model.impl.Function;
import de.smarthome.server.adapter.TestAdapter;
import de.smarthome.server.adapter.viewholder.SwitchViewHolder;

public class ChannelConfig {
    private final List<Channel> channels;

    public ChannelConfig(@JsonProperty("channels") List<Channel> channels) {
        this.channels = channels;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public boolean isOnlyBinary(Function function){
        Channel channel = findChannelByName(function);

        if(channel.getDatapoints().size() == 1){
            if(channel.getDatapoints().get(0).getType().equals(DatapointType.BINARY)
                    && channel.getDatapoints().get(0).getAccess().equals(DatapointAccess.READ_WRITE)){
                return true;
            }
        }
        return false;
    }

    public Channel findChannelByName(Function function){
        String searchedChannel = function.getChannelType();

        for(Channel channel: this.getChannels()){
            if(channel.getChannelID().equals(searchedChannel)){
                return channel;
            }
        }
        throw new IllegalArgumentException("Channel: "+ searchedChannel + " is unknown");
    }

    public int getRoomOverviewViewHolderUI(Channel channel) {
        if(channel.getDatapoints().size() == 1){
            if(channel.getDatapoints().get(0).getType().equals(DatapointType.BINARY)
                    && channel.getDatapoints().get(0).getAccess().equals(DatapointAccess.READ_WRITE)){
                return R.layout.item_switch;
            }
        }else{
            if(channel.getDatapoints().get(0).getType().equals(DatapointType.BINARY)){
                if(channel.getDatapoints().get(0).getAccess().equals(DatapointAccess.READ_WRITE)){
                    return R.layout.item_switch_arrow;
                }
            }
        }
        return R.layout.item_default;
    }

    public int getRoomOverviewItemViewType(Channel channel) {
        if(channel.getDatapoints().size() == 1){
            if(channel.getDatapoints().get(0).getType().equals(DatapointType.BINARY)
                    && channel.getDatapoints().get(0).getAccess().equals(DatapointAccess.READ_WRITE)){
                return TestAdapter.SWITCH_VIEW_HOLDER;
            }
        }else{
            if(channel.getDatapoints().get(0).getType().equals(DatapointType.BINARY)){
                if(channel.getDatapoints().get(0).getAccess().equals(DatapointAccess.READ_WRITE)){
                    return TestAdapter.SWITCH_ARROW_HOLDER;
                }
            }
        }
        return TestAdapter.DEFAULT_VIEW_HOLDER;
    }

    public int getRegulationViewHolderUI(ChannelDatapoint channelDatapoint){
        switch (channelDatapoint.getType()){
            case BINARY:
                return R.layout.item_switch;
            case PERCENT:
                return R.layout.item_slider;
            case INTEGER:
            case FLOAT:
            case STRING:
            case BYTE:
                return R.layout.item_read;

            default: throw new IllegalArgumentException("Type: "+ channelDatapoint.getType().toString() +" is unknown");
        }
    }

    public RecyclerView.ViewHolder getViewHolder(@NonNull ViewGroup parent,
                                                 @NonNull View itemView,
                                                 @NonNull TestAdapter.OnItemClickListener onItemClickListener,
                                                 @Nullable TestAdapter.OnSwitchClickListener onSwitchClickListener,
                                                 @NonNull TestAdapter adapter){

        return new SwitchViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_switch, parent, false),
                onItemClickListener,
                onSwitchClickListener,
                adapter
        );
    }

    @Override
    public String toString() {
        return "ChannelConfig{" +
                "channels=" + channels +
                '}';
    }
}
