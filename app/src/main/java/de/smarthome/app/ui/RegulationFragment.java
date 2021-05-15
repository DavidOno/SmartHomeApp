package de.smarthome.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.smarthome.R;
import de.smarthome.app.viewmodel.RegulationViewModel;
import de.smarthome.app.adapter.RegulationAdapter;

public class RegulationFragment extends Fragment {
    private static final String TAG = "RegulationFragment";
    private RegulationViewModel regulationViewModel;

    private RecyclerView recyclerViewRegulation;

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

        RegulationAdapter adapter = new RegulationAdapter();
        recyclerViewRegulation.setAdapter(adapter);

        regulationViewModel.getDataPointMap().observe(getViewLifecycleOwner(), dataPoints -> adapter.setDataPointList(dataPoints, getActivity().getApplication()));

        regulationViewModel.getStatusUpdateMap().observe(getViewLifecycleOwner(), stringStringMap -> {
            String uid = stringStringMap.keySet().iterator().next();
            String value = stringStringMap.get(uid);
            adapter.updateStatusValue(uid, value);
        });

        regulationViewModel.getStatusGetValueMap().observe(getViewLifecycleOwner(), adapter::updateMultipleStatusValues);

        regulationViewModel.getBoundaryMap().observe(getViewLifecycleOwner(), adapter::setBoundaryMap);

        adapter.setOnItemClickListener((datapoint, value) -> regulationViewModel.requestSetValue(datapoint.getID(), value));
    }
}