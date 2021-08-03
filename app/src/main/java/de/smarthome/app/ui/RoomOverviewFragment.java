package de.smarthome.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.smarthome.R;
import de.smarthome.app.utility.InternalStorageWriter;
import de.smarthome.app.viewmodel.RoomOverviewViewModel;
import de.smarthome.app.adapter.RoomOverviewAdapter;

public class RoomOverviewFragment extends Fragment {
    private static final String TAG = "RoomOverviewFragment";
    private RoomOverviewViewModel viewViewModel;
    private RecyclerView recyclerViewRoom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_overview, container, false);

        recyclerViewRoom = view.findViewById(R.id.recycler_view_room_overview);
        viewViewModel = new ViewModelProvider(requireActivity()).get(RoomOverviewViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(viewViewModel.getSelectedLocation() != null)
            requireActivity().setTitle(viewViewModel.getSelectedLocation().getName());

        recyclerViewRoom.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewRoom.setHasFixedSize(true);

        RoomOverviewAdapter adapter = new RoomOverviewAdapter(getActivity());
        recyclerViewRoom.setAdapter(adapter);

        setFunctionObserver(adapter);
        setStatusUpdateObserver(adapter);
        setGetValueMapObserver(adapter);

        setOnClickListener(adapter);
        setOnSwitchClickListener(adapter);
    }

    private void setFunctionObserver(RoomOverviewAdapter adapter) {
        viewViewModel.getFunctionMap().observe(getViewLifecycleOwner(), functionFunctionMap -> {
            InternalStorageWriter.writeFileOnInternalStorage(this.getContext(), "GIRA", "5. RoomOverview Observer\n\n");
            adapter.initialiseAdapter(functionFunctionMap);
        });
    }

    private void setStatusUpdateObserver(RoomOverviewAdapter adapter) {
        viewViewModel.getStatusUpdateMap().observe(getViewLifecycleOwner(), stringStringMap -> {
            String uid = stringStringMap.keySet().iterator().next();
            String value = stringStringMap.get(uid);
            adapter.updateSingleFunctionStatusValue(uid, value);
        });
    }

    private void setGetValueMapObserver(RoomOverviewAdapter adapter) {
        viewViewModel.getStatusGetValueMap().observe(getViewLifecycleOwner(), adapter::updateMultipleFunctionStatusValues);
    }

    private void setOnClickListener(RoomOverviewAdapter adapter) {
        adapter.setOnItemClickListener(function -> {
            InternalStorageWriter.writeFileOnInternalStorage(this.getContext(), "GIRA", "1. Function Selected\n");
            viewViewModel.setSelectedFunction(function);
            navigateToRegulationFragment();
        });
    }

    private void setOnSwitchClickListener(RoomOverviewAdapter adapter) {
        adapter.setOnSwitchClickListener((function, isChecked) -> {
            //There is only a switch if binary is the first (!) input type. So the first DataPoint is needed.
            if(isChecked){
                viewViewModel.requestSetValue(function.getDataPoints().get(0).getID(), "1");
            }else{
                viewViewModel.requestSetValue(function.getDataPoints().get(0).getID(), "0");
            }
        });
    }

    public void navigateToRegulationFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_roomOverviewFragment_to_regulationFragment);
    }
}