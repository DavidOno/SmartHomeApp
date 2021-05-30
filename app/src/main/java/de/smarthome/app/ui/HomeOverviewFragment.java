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
import de.smarthome.app.viewmodel.HomeOverviewViewModel;
import de.smarthome.app.adapter.HomeOverviewAdapter;

public class HomeOverviewFragment extends Fragment {
    private static final String TAG = "HomeOverviewFragment";
    private RecyclerView recyclerViewHome;
    private HomeOverviewViewModel homeOverviewViewModel;

    private HomeOverviewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeOverviewViewModel = new ViewModelProvider(requireActivity()).get(HomeOverviewViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_overview, container, false);

        recyclerViewHome  = view.findViewById(R.id.recycler_view_home_overview);
        recyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewHome.setHasFixedSize(true);

        adapter = new HomeOverviewAdapter();
        recyclerViewHome.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        homeOverviewViewModel.getLocationList().observe(getViewLifecycleOwner(), rooms -> {
            adapter.setRoomList(rooms);
            requireActivity().setTitle(rooms.get(0).getName());
        });

        adapter.setOnItemClickListener(location -> {
            homeOverviewViewModel.setSelectedLocation(location);
            navigateToRoomOverviewFragment();
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void navigateToRoomOverviewFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_homeOverviewFragment_to_roomOverviewFragment);
    }
}