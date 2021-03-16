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


public class RoomOverviewAdapter extends RecyclerView.Adapter<RoomOverviewAdapter.FunctionHolder> {

    private List<Function> functionList = new ArrayList<>();
    private OnItemClickListener listener;


    @NonNull
    @Override
    public FunctionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = getFunctionView(parent, viewType);

        return new FunctionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FunctionHolder holder, int position) {
        Function currentFunctionPosition = functionList.get(position);

        holder.textViewName.setText(currentFunctionPosition.getName());

    }

    public View getFunctionView(@NonNull ViewGroup parent, int viewType){
        View itemView = null;

        /*switch (viewType){
            case 1:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_overview_switch, parent, false);
                break;
            case 2:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_overview_test, parent, false);
                break;
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_overview_default, parent, false);
                break;
        }*/


        return itemView;
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



    class FunctionHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        /**Creates the onItemClickListeners, onItemLingClickListeners and the UI for the list items
         */
        public FunctionHolder(View itemView) {
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