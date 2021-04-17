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

    /**Returns the size of the workoutList
     * @return current size of the workoutList
     */
    @Override
    public int getItemCount() {
        return roomList.size();
    }


    public void setRoomList(List<Location> locations) {
        roomList = locations;
        notifyDataSetChanged();
    }

    /**Returns the Workout in the workoutList at a given position
     * @param  position position in the list
     * @return Workout at the specified position
     */
    public Location getRoomAt(int position) {
        return roomList.get(position);
    }



    class RoomHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        /**Creates the onItemClickListeners, onItemLingClickListeners and the UI for the list items
         */
        public RoomHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(roomList.get(position));
                    }
                }
            });
        }
    }

    /**Handles short onClick Events
     */
    public interface OnItemClickListener {
        void onItemClick(Location location);
    }


    /**Sets listener for short onClick Events
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}