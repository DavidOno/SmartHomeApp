package de.smarthome.server.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.smarthome.R;
import de.smarthome.model.impl.Function;


public class RegulationAdapter extends RecyclerView.Adapter<RegulationAdapter.UIHolder> {

    private List<Function> functionList = new ArrayList<>();
    private OnItemClickListener listener;


    @NonNull
    @Override
    public UIHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_switch, parent, false);

        return new UIHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UIHolder holder, int position) {
        Function currentWorkoutPosition = functionList.get(position);

        holder.textViewName.setText(currentWorkoutPosition.getName());

    }

    /**Returns the size of the workoutList
     * @return current size of the workoutList
     */
    @Override
    public int getItemCount() {
        return functionList.size();
    }


    public void setFunctionList(List<Function> functions) {
        functionList = functions;
        notifyDataSetChanged();
    }

    /**Returns the Workout in the workoutList at a given position
     * @param  position position in the list
     * @return Workout at the specified position
     */
    public Function getFunctionAt(int position) {
        return functionList.get(position);
    }



    class UIHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        /**Creates the onItemClickListeners, onItemLingClickListeners and the UI for the list items
         */
        public UIHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(functionList.get(position));
                    }
                }
            });
        }
    }

    /**Handles short onClick Events
     */
    public interface OnItemClickListener {
        void onItemClick(Function function);
    }


    /**Sets listener for short onClick Events
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}