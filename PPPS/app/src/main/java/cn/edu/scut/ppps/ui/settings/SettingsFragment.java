package cn.edu.scut.ppps.ui.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cn.edu.scut.ppps.MainActivity;
import cn.edu.scut.ppps.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private MainActivity mainActivity;
    private SettingsViewModel settingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        settingsViewModel.setMainActivity(mainActivity);
        settingsViewModel.setTexts();

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView cloudName1 = binding.cloudName1;
        final TextView cloudProvider1 = binding.cloudProvider1;
        final TextView cloudName2 = binding.cloudName2;
        final TextView cloudProvider2 = binding.cloudProvider2;
        final TextView imageNum = binding.imageNum;
        settingsViewModel.getCloudName1().observe(getViewLifecycleOwner(), cloudName1::setText);
        settingsViewModel.getCloudProvider1().observe(getViewLifecycleOwner(), cloudProvider1::setText);
        settingsViewModel.getCloudName2().observe(getViewLifecycleOwner(), cloudName2::setText);
        settingsViewModel.getCloudProvider2().observe(getViewLifecycleOwner(), cloudProvider2::setText);
        settingsViewModel.getImageNum().observe(getViewLifecycleOwner(), imageNum::setText);
        Button button = (Button) binding.settingsButton;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.callSettings();
            }
        });
        return root;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.turnOffFloatingButton();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mainActivity.turnOnFloatingButton();
    }
}