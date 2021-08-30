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
 * Adapter for the display of locations in a recyclerView.
 */
public class HomeOverviewAdapter extends RecyclerView.Adapter<HomeOverviewAdapter.LocationHolder> {
    private List<Location> locationList = new ArrayList<>();
    private OnItemClickListener listener;

    /**
     * Returns a locationviewholder object.
     * @param parent ViewGroup holding the recyclerview
     * @param viewType not used
     * @return locationviewholder object
     */
    @NonNull
    @Override
    public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_overview, parent, false);
        return new LocationHolder(itemView);
    }

    /**
     * Gives the viewHolder the location.
     * @param holder ViewHolder of the item
     * @param position Position of the item
     */
    @Override
    public void onBindViewHolder(@NonNull LocationHolder holder, int position) {
        Location currentWorkoutPosition = locationList.get(position);
        holder.textViewName.setText(currentWorkoutPosition.getName());
    }

    /**
     * Returns the amount of items in the recyclerview.
     * @return size of locationList
     */
    @Override
    public int getItemCount() {
        return locationList.size();
    }

    /**
     * Sets the dataset of the adapter and notifies the adapter that the dataset has changed
     */
    public void setLocationList(List<Location> locations) {
        locationList = locations;
        notifyDataSetChanged();
    }

    /**
     * Returns the location in the recyclerview at the given position.
     * @param position Position of the location
     * @return Location at the given position
     */
    public Location getLocationAt(int position) {
        return locationList.get(position);
    }


    /**
     * RecyclerViewHolder for the homeoverviewadapter.
     * Used to display a location in a recyclerView.
     */
    class LocationHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        public LocationHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(locationList.get(position));
                }
            });
        }
    }

    /**
     * This interface specifies how onitemclicklisteners behave in the viewholders of the homeoverviewadapter.
     */
    public interface OnItemClickListener {

        /**
         * Handles the onclick action of the user.
         * @param location Location that has been clicked on
         */
        void onItemClick(Location location);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}