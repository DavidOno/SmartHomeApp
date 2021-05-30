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

import java.util.ArrayList;
import java.util.List;

import de.smarthome.R;
import de.smarthome.app.model.Function;
import de.smarthome.app.viewmodel.RoomOverviewViewModel;
import de.smarthome.app.adapter.RoomOverviewAdapter;

public class RoomOverviewFragment extends Fragment {
    private static final String TAG = "RoomOverviewFragment";
    private RoomOverviewViewModel roomOverviewViewModel;
    private RecyclerView recyclerViewRoom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_overview, container, false);

        recyclerViewRoom = view.findViewById(R.id.recycler_view_room_overview);
        roomOverviewViewModel = new ViewModelProvider(requireActivity()).get(RoomOverviewViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(roomOverviewViewModel.getSelectedLocation() != null)
            requireActivity().setTitle(roomOverviewViewModel.getSelectedLocation().getName());

        recyclerViewRoom.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewRoom.setHasFixedSize(true);

        RoomOverviewAdapter adapter = new RoomOverviewAdapter();
        recyclerViewRoom.setAdapter(adapter);

        roomOverviewViewModel.getFunctionMap().observe(getViewLifecycleOwner(), functions -> adapter.setFunctionList(functions, getActivity().getApplication()));

        roomOverviewViewModel.getStatusUpdateMap().observe(getViewLifecycleOwner(), stringStringMap -> {
            String uid = stringStringMap.keySet().iterator().next();
            String value = stringStringMap.get(uid);
            adapter.updateStatusValue(uid, value);
        });

        roomOverviewViewModel.getStatusGetValueMap().observe(getViewLifecycleOwner(), adapter::updateMultipleStatusValues);

        adapter.setOnItemClickListener(function -> {
            roomOverviewViewModel.setSelectedFunction(function);
            navigateToRegulationFragment();
        });

        adapter.setOnSwitchClickListener((function, isChecked) -> {
            //There is only a switch if binary is the first (!) input type. So the first DataPoint is needed.
            if(isChecked){
                roomOverviewViewModel.requestSetValue(function.getDataPoints().get(0).getID(), "1");
            }else{
                roomOverviewViewModel.requestSetValue(function.getDataPoints().get(0).getID(), "0");
            }
        });
    }

    public void navigateToRegulationFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_roomOverviewFragment_to_regulationFragment);
    }
}