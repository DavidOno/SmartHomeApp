package de.smarthome.app.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import de.smarthome.app.model.Function;
import de.smarthome.app.adapter.RegulationAdapter;
import de.smarthome.app.adapter.RoomOverviewAdapter;

/**
 * This class represents the channelconfig from the callbackserver.
 * It holds all of the channels.
 */
public class ChannelConfig {
    private final List<Channel> channels;

    public ChannelConfig(@JsonProperty("channels") List<Channel> channels) {
        this.channels = channels;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    /**
     * Returns true if the first datapoint of the given function is binary and has read-write access
     * @param function Function to be checked
     * @return true if is binary and read-write access, false if not
     */
    public boolean isFirstDataPointBinary(Function function){
        Channel channel = findChannelByName(function);

        if(!channel.getDatapoints().isEmpty()){
            return channel.getDatapoints().get(0).getType().equals(DatapointType.BINARY)
                    && channel.getDatapoints().get(0).getAccess().equals(DatapointAccess.READ_WRITE);
        }
        return false;
    }

    /**
     * Returns true if the first datapoint has only read access
     * @param function Function to be checked
     * @return true if has only read access
     */
    public boolean hasFirstDataPointOnlyRead(Function function){
        Channel channel = findChannelByName(function);

        if(!channel.getDatapoints().isEmpty()) {
            return channel.getDatapoints().get(0).getAccess().equals(DatapointAccess.READ);
        }
        return false;
    }

    /**
     * Returns the channel corresponding to the channelType of the given function
     * @param function Function with channelType
     * @return channel of function
     */
    public Channel findChannelByName(Function function){
        String searchedChannel = function.getChannelType();

        for(Channel channel: this.getChannels()){
            if(channel.getChannelID().equals(searchedChannel)){
                return channel;
            }
        }
        throw new IllegalArgumentException("Channel: "+ searchedChannel + " is unknown");
    }

    /**
     * Returns the constant of the RoomOverviewAdapter corresponding to the required viewHolder
     * @param channel Channel to be checked
     * @return int value corresponding to roomOverviewAdapter viewHolder
     */
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
                if(channel.getDatapoints().get(0).getAccess().equals(DatapointAccess.READ)){
                    return RoomOverviewAdapter.STATUS_VIEW_HOLDER;
                }
            }
        }
        return RoomOverviewAdapter.DEFAULT_VIEW_HOLDER;
    }

    /**
     * Returns the constant of the RoomOverviewAdapter corresponding to the required viewHolder
     * @param channelDatapoint ChannelDatapoint to be checked
     * @return int value corresponding to regulationAdapter viewHolder
     */
    public int getRegulationItemViewType(ChannelDatapoint channelDatapoint){
        switch (channelDatapoint.getType()){
            case BINARY:
                if(channelDatapoint.getAccess().equals(DatapointAccess.READ_WRITE)){
                    return RegulationAdapter.SWITCH_VIEW_HOLDER;
                }
                if(channelDatapoint.getAccess().equals(DatapointAccess.WRITE)){
                    return RegulationAdapter.STEP_VIEW_HOLDER;
                }
                break;
            case FLOAT:
                if(channelDatapoint.getAccess().equals(DatapointAccess.READ_WRITE)){
                    return RegulationAdapter.FLOAT_SLIDER_VIEW_HOLDER;
                }
                if(channelDatapoint.getAccess().equals(DatapointAccess.WRITE)){
                    return RegulationAdapter.STEP_VIEW_HOLDER;
                }
                if(channelDatapoint.getAccess().equals(DatapointAccess.READ)){
                    return RegulationAdapter.READ_VIEW_HOLDER;
                }
                break;
            case PERCENT:
            case INTEGER:
            case BYTE:
                if(channelDatapoint.getAccess().equals(DatapointAccess.READ_WRITE)){
                    return RegulationAdapter.INT_SLIDER_VIEW_HOLDER;
                }
                if(channelDatapoint.getAccess().equals(DatapointAccess.WRITE)){
                    return RegulationAdapter.STEP_VIEW_HOLDER;
                }
                if(channelDatapoint.getAccess().equals(DatapointAccess.READ)){
                    return RegulationAdapter.READ_VIEW_HOLDER;
                }
                break;
            case STRING:
                return RegulationAdapter.READ_VIEW_HOLDER;

            default: throw new IllegalArgumentException("Type: "+ channelDatapoint.getType().toString() +" is unknown");
        }
        return -1;
    }

    @Override
    public String toString() {
        return "ChannelConfig{" +
                "channels=" + channels +
                '}';
    }
}
