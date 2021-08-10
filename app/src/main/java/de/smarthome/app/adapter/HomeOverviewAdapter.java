package de.smarthome.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.smarthome.R;
import de.smarthome.app.model.Location;

/**
 * Adapter for the display of locations in a recyclerView
 */
public class HomeOverviewAdapter extends RecyclerView.Adapter<HomeOverviewAdapter.RoomHolder> {
    private List<Location> roomList = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_overview, parent, false);
        return new RoomHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomHolder holder, int position) {
        Location currentWorkoutPosition = roomList.get(position);
        holder.textViewName.setText(currentWorkoutPosition.getName());
    }
    /**
     * Returns the amount of items in the roomList
     * @return size of roomList
     */
    @Override
    public int getItemCount() {
        return roomList.size();
    }

    /**
     * Sets the dataset of the adapter and notifies the adapter that the dataset has changed
     */
    public void setRoomList(List<Location> locations) {
        roomList = locations;
        notifyDataSetChanged();
    }

    public Location getRoomAt(int position) {
        return roomList.get(position);
    }


    /**
     * RecyclerViewHolder for the homeOverviewAdapter.
     * Used to display a location in a recyclerView
     */
    class RoomHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        public RoomHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(roomList.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Location location);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}