package dam.pmdm.spyrothedragon.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.databinding.FragmentWelcomeBinding;


public class WelcomeFragment extends Fragment {
    private FragmentWelcomeBinding binding;
    private static final String PREFS_NAME = "appPrefs";
    private static final String TUTORIAL_FINISHED_KEY = "tutorialFinished";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);

        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean tutorialFinished = preferences.getBoolean(TUTORIAL_FINISHED_KEY, false);

        // If the tutorial has been finished, navigate immediately to the next fragment
        if (tutorialFinished) {
            navController.navigate(R.id.action_navigation_welcome_to_navigation_characters,
                    null,
                    new NavOptions.Builder()
                            .setExitAnim(R.anim.circular_exit)
                            .build());
            return;
        }

        // Otherwise, remain in this fragment and wait for user action
        binding.btnComenzar.setOnClickListener(v -> {
            navController.navigate(R.id.action_navigation_welcome_to_navigation_characters,
                    null,
                    new NavOptions.Builder()
                            .setExitAnim(R.anim.circular_exit)
                            .build());
        });
    }
}