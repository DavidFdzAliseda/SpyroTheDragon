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
import android.view.WindowManager;
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
import dam.pmdm.spyrothedragon.adapters.CollectiblesAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentCollectiblesBinding;
import dam.pmdm.spyrothedragon.models.Collectible;
import dam.pmdm.spyrothedragon.utils.Utilities;

public class CollectiblesFragment extends Fragment {

    private FragmentCollectiblesBinding binding;
    private RecyclerView recyclerView;
    private CollectiblesAdapter adapter;
    private List<Collectible> collectiblesList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mostrarGuia();

        binding = FragmentCollectiblesBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerViewCollectibles;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        collectiblesList = new ArrayList<>();
        adapter = new CollectiblesAdapter(collectiblesList);
        recyclerView.setAdapter(adapter);

        loadCollectibles();
        return binding.getRoot();
    }

    private void mostrarGuia() {
        View guideLayout = requireActivity().findViewById(R.id.tutorialLayout);
        if (guideLayout != null) {
            guideLayout.setVisibility(View.VISIBLE);
            // Se muestra el texto y se anima
            TextView bocadillo = guideLayout.findViewById(R.id.bocadilloCollectibles);
            if (bocadillo != null) {
                bocadillo.setText(R.string.bocadillo_collectibles);
                //animateBocadillo(bocadillo);
            }
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
            //Boton siguiente
            Button btnSiguiente = guideLayout.findViewById(R.id.btnSiguiente);
            btnSiguiente.setOnClickListener(v -> {
                NavHostFragment.findNavController(this).navigate(R.id.action_navigation_collectibles_to_navigation_characters,
                        null,
                        new NavOptions.Builder()
                                .setExitAnim(R.anim.slide_out_right)
                                .setEnterAnim(R.anim.slide_in_left)
                                .build());
            });
        }
    }

    private void redimensionarSign(View signView, int width) {
        // Obtén los parámetros actuales del layout
        ViewGroup.LayoutParams params = signView.getLayoutParams();

        // Si el View tiene un LayoutParams de tipo MarginLayoutParams, puedes ajustar los márgenes
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) params;

            // Establece el margen izquierdo

            marginLayoutParams.leftMargin = getScreenWidth(requireContext())- (getScreenWidth(requireContext()) / 6) - width/2;
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

    private void loadCollectibles() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.collectibles);

            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Collectible currentCollectible = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("collectible".equals(tagName)) {
                            currentCollectible = new Collectible();
                        } else if (currentCollectible != null) {
                            if ("name".equals(tagName)) {
                                currentCollectible.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentCollectible.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentCollectible.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("collectible".equals(tagName) && currentCollectible != null) {
                            collectiblesList.add(currentCollectible);
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
        TextView bocadillo = guideLayout.findViewById(R.id.bocadilloCollectibles);
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
