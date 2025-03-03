package dam.pmdm.spyrothedragon.ui;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Character;
import dam.pmdm.spyrothedragon.adapters.CharactersAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentCharactersBinding;
import dam.pmdm.spyrothedragon.utils.Utilities;


public class CharactersFragment extends Fragment {

    private FragmentCharactersBinding binding;

    private RecyclerView recyclerView;
    private CharactersAdapter adapter;
    private List<Character> charactersList;
    private static final String PREFS_NAME = "appPrefs";
    private static final String TUTORIAL_FINISHED_KEY = "tutorialFinished";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View guideLayout = requireActivity().findViewById(R.id.tutorialLayout);
        TextView bocadillo = guideLayout.findViewById(R.id.bocadilloCharacter);
        String bocadilloCollectibles = getString(R.string.bocadillo_collectibles);
        //Comprueba si viene de la pantalla de coleccionables
        if (bocadillo.getText().equals(bocadilloCollectibles)) {
            mostrarGuiaAbout(guideLayout);
        }

        mostrarGuia();

        binding = FragmentCharactersBinding.inflate(inflater, container, false);

        //}

        // Inicializamos el RecyclerView y el adaptador
        recyclerView = binding.recyclerViewCharacters;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        charactersList = new ArrayList<>();
        adapter = new CharactersAdapter(charactersList);
        recyclerView.setAdapter(adapter);

        // Cargamos los personajes desde el XML
        loadCharacters();

        return binding.getRoot();
    }

    private void mostrarGuiaAbout( View guideLayout) {
        //Se esconden los elementos innecesarios
        TextView btnSaltar = guideLayout.findViewById(R.id.btnSaltar);
        btnSaltar.setVisibility(View.GONE);

        TextView btnSiguiente = guideLayout.findViewById(R.id.btnSiguiente);
        btnSiguiente.setVisibility(View.GONE);

        TextView bocadillo = guideLayout.findViewById(R.id.bocadilloCollectibles);
        bocadillo.setVisibility(View.GONE);

        TextView sign = guideLayout.findViewById(R.id.sign);
        sign.setVisibility(View.GONE);

        //Se hacen visibles los elementos necesarios
        TextView arrow = guideLayout.findViewById(R.id.arrow);
        arrow.setVisibility(View.VISIBLE);

        TextView bocadilloAbout = guideLayout.findViewById(R.id.bocadillo_about);
        bocadilloAbout.setVisibility(View.VISIBLE);

        TextView btnFinalizarTutorial = guideLayout.findViewById(R.id.btnFinalizarTutorial);
        btnFinalizarTutorial.setVisibility(View.VISIBLE);

        animarFlecha(arrow);
    }

    private void animarFlecha(TextView arrow) {
        // Suponiendo que 'signView' es tu ImageView o TextView con la flecha
        ObjectAnimator translateYUp = ObjectAnimator.ofFloat(arrow, "scaleX", 0.5f, 1f); // Mover hacia arriba
        ObjectAnimator translateYDown = ObjectAnimator.ofFloat(arrow, "scaleY", 0.5f, 1f); // Volver a la posición original

        // La animación sube y baja infinitamente
        translateYUp.setRepeatCount(3);
        translateYDown.setRepeatCount(3);

        // Se hace que las animaciones se ejecuten de manera secuencial (sube -> baja)
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translateYUp, translateYDown);
        animatorSet.setDuration(1000); // Duración de cada ciclo (subida y bajada)
        animatorSet.start();
    }

    private void mostrarGuia() {
        // Se comprueba el estado del tutorial
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean tutorialFinished = preferences.getBoolean(TUTORIAL_FINISHED_KEY, false);
        //if (!tutorialFinished) {
        View guideLayout = requireActivity().findViewById(R.id.tutorialLayout);
        if (guideLayout != null) {
            guideLayout.setVisibility(View.VISIBLE);
            // Se muestra el texto y se anima
            TextView bocadillo = guideLayout.findViewById(R.id.bocadilloCharacter);
            if (bocadillo != null) {
                //animateBocadillo(guideLayout);
            }

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

            View navView = requireActivity().findViewById(R.id.action_info);
            if (navView != null) {
                navView.setClickable(false);
                navView.setEnabled(false);
            }

            // Logica Omitir tutorial
            saltarGuia(guideLayout, preferences);

            // Logica Siguiente
            siguienteGuia(guideLayout);
        }
    }

    private void siguienteGuia(View guideLayout) {
        Button btnSiguiente = guideLayout.findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_characters_to_navigation_worlds,
                    null,
                    new NavOptions.Builder()
                            .setExitAnim(R.anim.slide_out_left)
                            .setEnterAnim(R.anim.slide_in_right)
                            .build());
        });

    }

    private void saltarGuia(View guideLayout, SharedPreferences preferences) {
        Button btnSaltar = guideLayout.findViewById(R.id.btnSaltar);
        if (btnSaltar != null) {
            btnSaltar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guideLayout.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    guideLayout.setVisibility(View.GONE);

                                }
                            });

                    // Octualiza el estado del tutorial
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(TUTORIAL_FINISHED_KEY, true);
                    editor.apply();
                }

            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadCharacters() {
        try {
            // Cargamos el archivo XML desde res/xml (NOTA: ahora se usa R.xml.characters)
            InputStream inputStream = getResources().openRawResource(R.raw.characters);

            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Character currentCharacter = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("character".equals(tagName)) {
                            currentCharacter = new Character();
                        } else if (currentCharacter != null) {
                            if ("name".equals(tagName)) {
                                currentCharacter.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentCharacter.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentCharacter.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("character".equals(tagName) && currentCharacter != null) {
                            charactersList.add(currentCharacter);
                        }
                        break;
                }

                eventType = parser.next();
            }

            adapter.notifyDataSetChanged(); // Notificamos al adaptador que los datos han cambiado
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCircle() {
        new Handler().postDelayed(() -> {
            TextView bocadillo = requireView().findViewById(R.id.bocadilloCharacter);
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.navView);
            View itemCharacters = bottomNavigationView.findViewById(R.id.nav_characters);

            bocadillo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // Ensure this only runs once
                    bocadillo.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    // Set the CircleDrawable to the nav_characters item
                    itemCharacters.setBackground(new CircleDrawable());
                }
            });
        }, 500);
    }

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public void redimensionarSign(View signView, int width) {
        // Obtén los parámetros actuales del layout
        ViewGroup.LayoutParams params = signView.getLayoutParams();

        // Si el View tiene un LayoutParams de tipo MarginLayoutParams, puedes ajustar los márgenes
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) params;

            // Establece el margen izquierdo

            marginLayoutParams.leftMargin = ((getScreenWidth(requireContext()) / 6 - width/2));
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

    public static void animateBocadillo(View guideLayout) {
        TextView bocadillo = guideLayout.findViewById(R.id.bocadilloCharacter);
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
                } else {
                    // Cuando la animación termine, habilitar el botón
                    btnSiguiente.setEnabled(true);
                }
            }
        }, delay);
    }
}