package de.smarthome.server.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

import de.smarthome.R;
import de.smarthome.model.impl.Datapoint;
import de.smarthome.model.impl.Function;
import de.smarthome.model.responses.CallbackValueInput;
import de.smarthome.model.viewmodel.RegulationViewModel;
import de.smarthome.server.adapter.RegulationAdapter;
import de.smarthome.server.adapter.RoomOverviewAdapter;

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

        regulationViewModel.getDataPoints().observe(getViewLifecycleOwner(), new Observer<Map<Datapoint, Datapoint>>() {
            @Override
            public void onChanged(Map<Datapoint, Datapoint> dataPoints) {
                adapter.setDataPointList(dataPoints, getActivity().getApplication());
            }
        });

        regulationViewModel.getStatusList().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> stringStringMap) {
                //TODO: REWORK LOOKS TERRIBLE!
                adapter.updateStatusValue(stringStringMap.keySet().iterator().next(),
                        stringStringMap.get(stringStringMap.keySet().iterator().next()));
            }
        });

        //CallbackValueInput input = new CallbackValueInput(1, "2", "aajx", "70", null);

        adapter.setOnItemClickListener(new RegulationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Datapoint datapoint, String value) {
                regulationViewModel.requestSetValue(datapoint.getID(), value);
                //regulationViewModel.repository.update(input);
            }
        });
    }
}