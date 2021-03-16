package de.smarthome.server.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.smarthome.R;
import de.smarthome.model.impl.Function;
import de.smarthome.server.adapter.TestAdapter;

public class DefaultViewHolder extends TestAdapter.ViewHolder{
    private TextView textView;
    private TestAdapter adapter;
    private TestAdapter.OnItemClickListener onItemClickListener;

    public DefaultViewHolder(@NonNull View itemView,
                             @NonNull TestAdapter.OnItemClickListener onItemClickListener,
                             @NonNull TestAdapter adapter) {
        super(itemView);
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
    public void onBindViewHolder(TestAdapter.ViewHolder holder, int position, Function function) {
        this.textView.setText(function.getName());
    }
}
