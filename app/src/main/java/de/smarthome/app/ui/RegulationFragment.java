package de.smarthome.app.ui;

import android.os.Bundle;
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
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.Function;
import de.smarthome.app.model.configs.Boundary;
import de.smarthome.app.model.configs.BoundaryDataPoint;
import de.smarthome.app.viewmodel.RegulationViewModel;
import de.smarthome.app.adapter.RegulationAdapter;

public class RegulationFragment extends Fragment {
    private final String TAG = "RegulationFragment";
    private RegulationViewModel regulationViewModel;

    private RecyclerView recyclerViewRegulation;

    private RegulationAdapter adapter;

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
        requireActivity().setTitle(regulationViewModel.getSelectedFunction().getName());

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
                String uid = stringStringMap.keySet().iterator().next();
                String value = stringStringMap.get(uid);
                adapter.updateStatusValue(uid, value);
            }
        });

        regulationViewModel.getStatusList2().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> stringStringMap) {
                adapter.updateStatusValue2(stringStringMap);

            }
        });

        //TODO:REFACTOR!
        regulationViewModel.getTest().observe(getViewLifecycleOwner(), new Observer<Map<Datapoint, BoundaryDataPoint>>() {
            @Override
            public void onChanged(Map<Datapoint, BoundaryDataPoint> boundaryMap) {
                adapter.setBoundaryList(boundaryMap);
            }
        });

        adapter.setOnItemClickListener(new RegulationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Datapoint datapoint, String value) {
                regulationViewModel.requestSetValue(datapoint.getID(), value);
            }
        });
    }
}