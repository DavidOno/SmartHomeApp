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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.smarthome.R;
import de.smarthome.app.viewmodel.HomeOverviewViewModel;
import de.smarthome.app.adapter.HomeOverviewAdapter;

/**
 * This fragment contains a recyclerview that displays all location in the uiconfig and handles their selection.
 */
public class HomeOverviewFragment extends Fragment {
    private static final String TAG = "HomeOverviewFragment";
    private RecyclerView recyclerView;
    private HomeOverviewViewModel viewModel;
    private HomeOverviewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeOverviewViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_overview, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_home_overview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new HomeOverviewAdapter();
        recyclerView.setAdapter(adapter);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setLocationListObserver();
        setOnClickListener();
        requireActivity().setTitle(R.string.title_home_overview_fragment);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setOnClickListener() {
        adapter.setOnItemClickListener(location -> {
            if(viewModel.isChannelConfigLoaded()) {
                viewModel.initSelectedLocation(location);
                navigateToRoomOverviewFragment();
            }
        });
    }

    private void setLocationListObserver() {
        viewModel.getLocationList().observe(getViewLifecycleOwner(), rooms -> {
            adapter.setLocationList(rooms);
            requireActivity().setTitle(rooms.get(0).getName());
        });
    }

    private void navigateToRoomOverviewFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(HomeOverviewFragmentDirections.actionHomeOverviewFragmentToRoomOverviewFragment());
    }
}