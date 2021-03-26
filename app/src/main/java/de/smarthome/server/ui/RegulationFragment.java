package de.smarthome.server.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.smarthome.R;
import de.smarthome.model.configs.ChannelDatapoint;
import de.smarthome.model.impl.Datapoint;
import de.smarthome.model.impl.Function;
import de.smarthome.model.viewmodel.RegulationViewModel;
import de.smarthome.server.adapter.RegulationAdapter;

public class RegulationFragment extends Fragment {
    private final String TAG = "RegulationFragment";
    private RegulationViewModel regulationViewModel;

    private RecyclerView recyclerViewRegulation;

    private RegulationAdapter adapter;

    public static RoomOverviewFragment newInstance() {
        return new RoomOverviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regulation, container, false);

        recyclerViewRegulation = view.findViewById(R.id.recycler_view_regulation);
        regulationViewModel = new ViewModelProvider(requireActivity()).get(RegulationViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewRegulation.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewRegulation.setHasFixedSize(true);

        adapter = new RegulationAdapter();
        recyclerViewRegulation.setAdapter(adapter);

        regulationViewModel.getDatapoints().observe(getViewLifecycleOwner(), new Observer<List<Datapoint>>() {
            @Override
            public void onChanged(@NonNull List<Datapoint> dataPoints) {
                adapter.setDatapointList(dataPoints);
            }
        });

        adapter.setOnItemClickListener(new RegulationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Datapoint datapoint, String value) {
                //TODO: Check with David if this is acceptable or if it should be different listeners (compare: RoomOverviewFragment)
                if(value.equals("true")) {
                    Log.d("Hello", "Holder value:" + 1 + ", UID " + datapoint.getID());
                    regulationViewModel.requestSetValue(datapoint.getID(), "1");
                }else if(value.equals("false")){
                    Log.d("Hello", "Holder value:" + 0 + ", UID " + datapoint.getID());
                    regulationViewModel.requestSetValue(datapoint.getID(), "0");
                }else{
                    Log.d("Hello", "Holder value:" + value + ", UID " + datapoint.getID());
                    regulationViewModel.requestSetValue(datapoint.getID(), value);
                }
            }
        });
    }
}