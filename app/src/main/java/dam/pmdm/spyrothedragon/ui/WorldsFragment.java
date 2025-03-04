package dam.pmdm.spyrothedragon.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.adapters.WorldsAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentWorldsBinding;
import dam.pmdm.spyrothedragon.models.World;

public class WorldsFragment extends Fragment {

    private FragmentWorldsBinding binding;
    private RecyclerView recyclerView;
    private WorldsAdapter adapter;
    private List<World> worldsList;
    private static final String PREFS_NAME = "appPrefs";
    private static final String TUTORIAL_FINISHED_KEY = "tutorialFinished";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean tutorialFinished = preferences.getBoolean(TUTORIAL_FINISHED_KEY, false);

        if (!tutorialFinished) {
            mostrarGuia();
        }

        binding = FragmentWorldsBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerViewWorlds;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        worldsList = new ArrayList<>();
        adapter = new WorldsAdapter(worldsList);
        recyclerView.setAdapter(adapter);

        loadWorlds();
        return binding.getRoot();
    }

    private void mostrarGuia() {
        View guideLayout = requireActivity().findViewById(R.id.tutorialLayout);
        if (guideLayout != null) {

            //Se reubica la señal de la seccion
            View signView = guideLayout.findViewById(R.id.sign); // Obtén el elemento por su ID
            if (signView != null) {
                signView.post(new Runnable() {
                    @Override
                    public void run() {
                        // Obtener el ancho en píxeles
                        int width = signView.getWidth();
                        redimensionarSign(signView, width);
                    }
                });
            }

            // Logica Siguiente
            siguienteGuia(guideLayout);
        }
    }

    private void siguienteGuia(View guideLayout) {
        //Boton siguiente
        Button btnSiguiente = guideLayout.findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(v -> {
            TextView bocadillo = guideLayout.findViewById(R.id.bocadillo);
            animacionBocadillo(bocadillo);

            MediaPlayer mediaPlayer = MediaPlayer.create(v.getContext(), R.raw.bocadillo);
            mediaPlayer.start();

            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_worlds_to_navigation_collectibles,
                    null,
                    new NavOptions.Builder()
                            .setExitAnim(R.anim.slide_out_left)
                            .setEnterAnim(R.anim.slide_in_right)
                            .build());
        });
    }

    private void animacionBocadillo(TextView bocadillo) {
        Animation salir = AnimationUtils.loadAnimation(bocadillo.getContext(), R.anim.slide_out_left);
        Animation entrar = AnimationUtils.loadAnimation(bocadillo.getContext(), R.anim.slide_in_right);

        salir.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Cambiar texto después de salir
                bocadillo.setText(R.string.bocadillo_collectibles);
                bocadillo.startAnimation(entrar);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        bocadillo.startAnimation(salir);
    }

    private void redimensionarSign(View signView, int width) {
    // Obtén los parámetros actuales del layout
        ViewGroup.LayoutParams params = signView.getLayoutParams();

        // Si el View tiene un LayoutParams de tipo MarginLayoutParams, puedes ajustar los márgenes
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) params;

            // Establece el margen izquierdo

            marginLayoutParams.leftMargin = ((getScreenWidth(requireContext()) / 2 - width/2));
            marginLayoutParams.bottomMargin = -50;
            // Aplica los nuevos parámetros
            signView.setLayoutParams(marginLayoutParams);
        }

        //Animacion de circulos
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(signView, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(signView, "scaleY", 0.5f, 1f);

        scaleX.setRepeatCount(2);
        scaleY.setRepeatCount(2);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(1000);
        animatorSet.start();
    }

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadWorlds() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.worlds);

            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            World currentWorld = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("world".equals(tagName)) {
                            currentWorld = new World();
                        } else if (currentWorld != null) {
                            if ("name".equals(tagName)) {
                                currentWorld.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentWorld.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentWorld.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("world".equals(tagName) && currentWorld != null) {
                            worldsList.add(currentWorld);
                        }
                        break;
                }

                eventType = parser.next();
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void animateBocadillo(View guideLayout) {
        TextView bocadillo = guideLayout.findViewById(R.id.bocadillo);
        final String fullText = bocadillo.getText().toString(); // Save complete message
        bocadillo.setText(""); // Clear for the animation
        final Handler handler = new Handler();
        final int delay = 25; // Delay between characters in milliseconds
        final int[] index = {0};
        TextView btnSiguiente = guideLayout.findViewById(R.id.btnSiguiente);
        btnSiguiente.setEnabled(false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index[0] < fullText.length()) {
                    // Append one more character and update the TextView
                    bocadillo.setText(fullText.substring(0, index[0] + 1));
                    index[0]++;
                    handler.postDelayed(this, delay);
                }else{
                    btnSiguiente.setEnabled(true);
                }
            }
        }, delay);
    }
}
