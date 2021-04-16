package de.smarthome.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import de.smarthome.R;
import de.smarthome.model.impl.Function;
import de.smarthome.server.adapter.RegulationAdapter;
import de.smarthome.server.adapter.RoomOverviewAdapter;

public class ChannelConfig {
    private final List<Channel> channels;

    public ChannelConfig(@JsonProperty("channels") List<Channel> channels) {
        this.channels = channels;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public boolean isFirstDataPointBinary(Function function){
        Channel channel = findChannelByName(function);

        if(channel.getDatapoints().size() >= 1){
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

    public int getRoomOverviewItemViewType(Channel channel) {
        if(channel.getDatapoints().size() == 1){
            if(channel.getDatapoints().get(0).getType().equals(DatapointType.BINARY)
                    && channel.getDatapoints().get(0).getAccess().equals(DatapointAccess.READ_WRITE)){
                return RoomOverviewAdapter.SWITCH_VIEW_HOLDER;
            }
        }else{
            if(channel.getDatapoints().get(0).getType().equals(DatapointType.BINARY)){
                if(channel.getDatapoints().get(0).getAccess().equals(DatapointAccess.READ_WRITE)){
                    return RoomOverviewAdapter.SWITCH_ARROW_HOLDER;
                }
            }else{
                if(channel.getDatapoints().get(0).getAccess().equals(DatapointAccess.READ_WRITE)){
                    return RoomOverviewAdapter.STATUS_VIEW_HOLDER;
                }
            }
        }
        return RoomOverviewAdapter.DEFAULT_VIEW_HOLDER;
    }


    public int getRegulationItemViewType(ChannelDatapoint channelDatapoint){
        switch (channelDatapoint.getType()){
            case BINARY:
                if(channelDatapoint.getAccess().equals(DatapointAccess.READ_WRITE)){
                    return RegulationAdapter.SWITCH_VIEW_HOLDER;
                }
                if(channelDatapoint.getAccess().equals(DatapointAccess.WRITE)){
                    return RegulationAdapter.STEP_VIEW_HOLDER;
                }
            case PERCENT:
                if(channelDatapoint.getAccess().equals(DatapointAccess.READ_WRITE)){
                    return RegulationAdapter.SLIDER_VIEW_HOLDER;
                }
                if(channelDatapoint.getAccess().equals(DatapointAccess.WRITE)){
                    return RegulationAdapter.STEP_VIEW_HOLDER;
                }
            case INTEGER:
            case FLOAT:
            case BYTE:
                if(channelDatapoint.getAccess().equals(DatapointAccess.READ_WRITE)){
                    return RegulationAdapter.SLIDER_VIEW_HOLDER;
                }
                if(channelDatapoint.getAccess().equals(DatapointAccess.WRITE)){
                    return RegulationAdapter.STEP_VIEW_HOLDER;
                }
            case STRING:
                return RegulationAdapter.READ_VIEW_HOLDER;

            default: throw new IllegalArgumentException("Type: "+ channelDatapoint.getType().toString() +" is unknown");
        }
    }

    @Override
    public String toString() {
        return "ChannelConfig{" +
                "channels=" + channels +
                '}';
    }
}
