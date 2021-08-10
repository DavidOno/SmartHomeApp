package de.smarthome.app.adapter.viewholder.roomoverview;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.Function;
import de.smarthome.app.adapter.RoomOverviewAdapter;

/**
 * RecyclerViewHolder for the roomOverviewAdapter.
 * Used to display a function
 */
public class DefaultViewHolder extends RoomOverviewAdapter.ViewHolder{
    private TextView textViewName;

    public DefaultViewHolder(@NonNull ViewGroup parent,
                             @NonNull RoomOverviewAdapter.OnItemClickListener onItemClickListener,
                             @NonNull RoomOverviewAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_default, parent, false));

        textViewName = itemView.findViewById(R.id.textView_item);
        setOnClickListener(onItemClickListener, adapter);
    }

    private void setOnClickListener(@NonNull RoomOverviewAdapter.OnItemClickListener onItemClickListener, @NonNull RoomOverviewAdapter adapter) {
        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemClick(adapter.getFunctionAt(position));
            }
        });
    }

    /**
     * Displays name of the given function in a textView
     * @param function Function to be displayed by the viewHolder
     * @param value Value to be displayed, but is never be used in this type of viewHolder
     */
    @Override
    public void onBindViewHolder(Function function, Optional<String> value ) {
        textViewName.setText(function.getName().replace("_", " "));
    }
}