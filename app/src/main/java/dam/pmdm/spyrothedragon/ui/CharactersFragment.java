package dam.pmdm.spyrothedragon.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.content.Context;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Character;
import dam.pmdm.spyrothedragon.adapters.CharactersAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentCharactersBinding;


public class CharactersFragment extends Fragment {

    private FragmentCharactersBinding binding;

    private RecyclerView recyclerView;
    private CharactersAdapter adapter;
    private List<Character> charactersList;
    private static final String PREFS_NAME = "appPrefs";
    private static final String TUTORIAL_FINISHED_KEY = "tutorialFinished";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCharactersBinding.inflate(inflater, container, false);

        // Check tutorial state and set the guide visible if not finished
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean tutorialFinished = preferences.getBoolean(TUTORIAL_FINISHED_KEY, false);
        //if (!tutorialFinished) {
            View guideLayout = requireActivity().findViewById(R.id.tutorialLayout);
            if (guideLayout != null) {
                guideLayout.setVisibility(View.VISIBLE);
                // Retrieve the TextView and start the typewriter animation
                TextView bocadillo = guideLayout.findViewById(R.id.bocadillo);
                if (bocadillo != null) {
                    animateBocadillo(bocadillo);
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

                // Retrieve and set the OnClickListener for the "Omitir tutorial" button
                Button btnSaltar = guideLayout.findViewById(R.id.btnSaltar);
                if (btnSaltar != null) {
                    btnSaltar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            guideLayout.animate()
                                    .alpha(0f)
                                    .setDuration(300) // Duration in milliseconds
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            guideLayout.setVisibility(View.GONE);
                                            // Optionally update preferences here to mark tutorial as finished
                                        }
                                    });
                            // Optionally update the preference to mark the tutorial as finished
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean(TUTORIAL_FINISHED_KEY, true);
                            editor.apply();
                        }
                    });
                }
            }
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

    private void animateBocadillo(TextView bocadillo) {
        final String fullText = bocadillo.getText().toString(); // Save complete message
        bocadillo.setText(""); // Clear for the animation
        final Handler handler = new Handler();
        final int delay = 25; // Delay between characters in milliseconds
        final int[] index = {0};

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index[0] < fullText.length()) {
                    // Append one more character and update the TextView
                    bocadillo.setText(fullText.substring(0, index[0] + 1));
                    index[0]++;
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }

    // Method to animate the arrow
    // Method to animate the arrow
    private void animateArrow(ImageView arrow, View navCharacters) {
        if (navCharacters != null) {
            arrow.setX(navCharacters.getX());
            arrow.setY(navCharacters.getY() - arrow.getHeight());
        }
    }

    private void setCircle() {
        new Handler().postDelayed(() -> {
            TextView bocadillo = requireView().findViewById(R.id.bocadillo);
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

            // Aplica los nuevos parámetros
            signView.setLayoutParams(marginLayoutParams);
        }
    }
}