package de.smarthome.app.adapter.viewholder.roomoverview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.Function;
import de.smarthome.app.adapter.RoomOverviewAdapter;

public class DefaultViewHolder extends RoomOverviewAdapter.ViewHolder{
    private TextView textView;
    private RoomOverviewAdapter adapter;
    private RoomOverviewAdapter.OnItemClickListener onItemClickListener;

    public DefaultViewHolder(@NonNull ViewGroup parent,
                             @NonNull RoomOverviewAdapter.OnItemClickListener onItemClickListener,
                             @NonNull RoomOverviewAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_default, parent, false));

        textView = itemView.findViewById(R.id.textView_item);

        this.onItemClickListener = onItemClickListener;
        this.adapter = adapter;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(adapter.getFunctionAt(position));
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RoomOverviewAdapter.ViewHolder holder, int position, Function function, Optional<String> value ) {
        this.textView.setText(function.getName());
    }
}
