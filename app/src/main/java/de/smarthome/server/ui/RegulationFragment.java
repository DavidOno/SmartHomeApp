package de.smarthome.server.ui;

import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;

import de.smarthome.R;
import de.smarthome.model.impl.Function;
import de.smarthome.model.viewmodel.RegulationViewModel;

public class RegulationFragment extends Fragment {
    private  final String TAG = "RegulationFragment";
    private RegulationViewModel regulationViewModel;

    private String roomName;
    private String functionUID;

    private Function usedFunction;

    private TextView textView;
    private Switch switchBinary;
    private Button buttonConfirm;
    private EditText editTextInput;

    public static RoomOverviewFragment newInstance(String param1, String param2) {
        RoomOverviewFragment fragment = new RoomOverviewFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regulation_number, container, false);

        regulationViewModel = new ViewModelProvider(requireActivity()).get(RegulationViewModel.class);
        roomName = RegulationFragmentArgs.fromBundle(getArguments()).getNameSelectedRoom();
        functionUID = RegulationFragmentArgs.fromBundle(getArguments()).getSelectedFunctionUID();

        usedFunction = regulationViewModel.getFunctionByUID(functionUID);

        if(usedFunction.getFunctionType().contains(".Covering")){
            view = inflater.inflate(R.layout.fragment_regulation_number, container, false);
            buttonConfirm = view.findViewById(R.id.button_input_regulation);
            editTextInput = view.findViewById(R.id.edit_text_regulation);

        }
        if(usedFunction.getFunctionType().contains(".HeatingCooling")){
            view = inflater.inflate(R.layout.fragment_regulation_percentage, container, false);
            buttonConfirm = view.findViewById(R.id.button_input_regulation);
            editTextInput = view.findViewById(R.id.edit_text_regulation);

        }
        if(usedFunction.getFunctionType().contains(".Switch")){
            view = inflater.inflate(R.layout.fragment_regulation_binary, container, false);
            switchBinary = view.findViewById(R.id.switch_regulation);
        }

        textView = view.findViewById(R.id.text_view_name_regulation);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(usedFunction.getFunctionType().contains(".Switch")){
            switchBinary.setOnClickListener(v -> setValueSwitch());
        }
        if(usedFunction.getFunctionType().contains(".HeatingCooling")){
            buttonConfirm.setOnClickListener(v -> setValue(editTextInput.getText().toString()));
        }
        if(usedFunction.getFunctionType().contains(".Covering")){
            buttonConfirm.setOnClickListener(v -> setValue(editTextInput.getText().toString()));
        }
    }

    private void setValueSwitch(){
        String value = "0";
        if (switchBinary.isChecked()){
            value = "1";
        }
        setValue(value);
    }

    private void setValue(String value){
        textView.setText(value);
        //TODO: NOT STABLE! CHANGE IS REQUIRED!
        regulationViewModel.requestSetValue(usedFunction.getDataPoints().get(0).getID(), value);
        //Log.d(TAG, usedFunction.toString());
        //Log.d(TAG, usedFunction.getDataPoints().toString());
    }
}