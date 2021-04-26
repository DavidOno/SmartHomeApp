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

public class StatusViewHolder extends RoomOverviewAdapter.ViewHolder{
    private TextView textViewName;
    private TextView textViewStatus;
    private RoomOverviewAdapter adapter;
    private RoomOverviewAdapter.OnItemClickListener onItemClickListener;

    public StatusViewHolder(@NonNull ViewGroup parent,
                             @NonNull RoomOverviewAdapter.OnItemClickListener onItemClickListener,
                             @NonNull RoomOverviewAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false));

        textViewName = itemView.findViewById(R.id.textView_item);
        textViewStatus = itemView.findViewById(R.id.textView_item_status);

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
    public void onBindViewHolder(RoomOverviewAdapter.ViewHolder holder, int position, Function function,  Optional<String> value) {
        textViewName.setText(function.getName().replace("_", " "));

        value.ifPresent(s -> textViewStatus.setText(s));
    }
}
