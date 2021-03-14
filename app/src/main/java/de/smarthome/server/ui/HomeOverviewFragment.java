package de.smarthome.server.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.smarthome.R;
import de.smarthome.model.impl.Location;
import de.smarthome.model.viewmodel.HomeOverviewViewModel;
import de.smarthome.server.adapter.HomeOverviewAdapter;


public class HomeOverviewFragment extends Fragment {
    private  final String TAG = "HomeOverviewFragment";
    private RecyclerView recyclerViewHome;
    private HomeOverviewViewModel homeOverviewViewModel;

    private HomeOverviewAdapter adapter;
    public static HomeOverviewFragment newInstance(String param1, String param2) {
        HomeOverviewFragment fragment = new HomeOverviewFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_overview, container, false);

        recyclerViewHome  = view.findViewById(R.id.recycler_view_home_overview);
        recyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewHome.setHasFixedSize(true);

        adapter = new HomeOverviewAdapter();
        recyclerViewHome.setAdapter(adapter);

        homeOverviewViewModel = new ViewModelProvider(requireActivity()).get(HomeOverviewViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        homeOverviewViewModel.getRooms().observe(getViewLifecycleOwner(), new Observer<List<Location>>() {
            @Override
            public void onChanged(@Nullable List<Location> rooms) {
                adapter.setRoomList(rooms);;
            }
        });

        adapter.setOnItemClickListener(new HomeOverviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Location location) {
                homeOverviewViewModel.setSelectedLocation(location);
                String selectedRoomName = location.getName();
                navigateToRoomOverviewFragment(selectedRoomName);
            }

        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void navigateToRoomOverviewFragment(String selectedRoomName) {
        NavController navController = NavHostFragment.findNavController(this);

        HomeOverviewFragmentDirections.ActionRoomsFragmentToRoomOverviewFragment toRoomOverviewFragment =
                HomeOverviewFragmentDirections.actionRoomsFragmentToRoomOverviewFragment();

        toRoomOverviewFragment.setIDSelectedRoom("TestID set in HomeOverviewFragment");
        toRoomOverviewFragment.setNameSelectedRoom(selectedRoomName);

        navController.navigate(toRoomOverviewFragment);
    }
}