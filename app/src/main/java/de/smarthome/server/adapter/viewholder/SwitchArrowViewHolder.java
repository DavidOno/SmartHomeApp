package de.smarthome.server.adapter.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import de.smarthome.R;
import de.smarthome.model.impl.Function;
import de.smarthome.server.adapter.TestAdapter;


public class SwitchArrowViewHolder extends TestAdapter.ViewHolder{
    private SwitchCompat binarySwitch;
    private TextView textView;
    private TestAdapter adapter;
    private TestAdapter.OnItemClickListener onItemClickListener;
    private TestAdapter.OnSwitchClickListener onSwitchClickListener;

    public SwitchArrowViewHolder(@NonNull View itemView,
                                 @NonNull TestAdapter.OnItemClickListener onItemClickListener,
                                 @NonNull TestAdapter.OnSwitchClickListener onSwitchClickListener,
                                 @NonNull TestAdapter adapter) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView_item);
        binarySwitch = itemView.findViewById(R.id.switch_item);

        this.onItemClickListener = onItemClickListener;
        this.onSwitchClickListener = onSwitchClickListener;
        this.adapter = adapter;

        binarySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SwitchViewHolder", "Switch pressed");
                int position = getAdapterPosition();
                if (onSwitchClickListener != null && position != RecyclerView.NO_POSITION) {
                    onSwitchClickListener.onItemClick(adapter.getFunctionAt(position),  binarySwitch.isChecked());
                }
            }
        });

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

