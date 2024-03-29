package de.smarthome.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;
import java.util.stream.Collectors;

import de.smarthome.R;
import de.smarthome.app.viewmodel.RegulationViewModel;
import de.smarthome.app.adapter.RegulationAdapter;

/**
 * This fragment contains a recyclerview for the display all datapoints in a selected function.
 * It also handles the interactions of the user with them.
 */
public class RegulationFragment extends Fragment {
    private static final String TAG = "RegulationFragment";
    private RegulationViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regulation, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = view.findViewById(R.id.recycler_view_regulation);
        viewModel = new ViewModelProvider(requireActivity()).get(RegulationViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(viewModel.getSelectedFunction() != null)
            requireActivity().setTitle(viewModel.getSelectedFunction().getName());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        RegulationAdapter adapter = new RegulationAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        setDataPointObserver(adapter);
        setStatusUpdateObserver(adapter);
        setStatusGetValueObserver(adapter);
        setBoundaryObserver(adapter);
        setOnClickListener(adapter);
    }

    private void setOnClickListener(RegulationAdapter adapter) {
        adapter.setOnItemClickListener((datapoint, value) -> viewModel.requestSetValue(datapoint.getID(), value));
    }

    private void setBoundaryObserver(RegulationAdapter adapter) {
        viewModel.getBoundaryMap().observe(getViewLifecycleOwner(), adapter::setBoundaryMap);
    }

    private void setStatusGetValueObserver(RegulationAdapter adapter) {
        viewModel.getStatusGetValueMap().observe(getViewLifecycleOwner(), stringStringMap -> {
            Map<String, String> newValueMap = stringStringMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            adapter.updateMultipleDatapointStatusValues(newValueMap);
        });
    }

    private void setDataPointObserver(RegulationAdapter adapter) {
        viewModel.getDataPointMap().observe(getViewLifecycleOwner(), datapointDatapointMap -> {
            viewModel.requestCurrentStatusValues();
            adapter.initialiseAdapter(datapointDatapointMap);
        });
    }

    private void setStatusUpdateObserver(RegulationAdapter adapter) {
        viewModel.getStatusUpdateMap().observe(getViewLifecycleOwner(), stringStringMap -> {
            String uid = stringStringMap.keySet().iterator().next();
            String value = stringStringMap.get(uid);
            adapter.updateSingleDatapointStatusValue(uid, value);
        });
    }
}