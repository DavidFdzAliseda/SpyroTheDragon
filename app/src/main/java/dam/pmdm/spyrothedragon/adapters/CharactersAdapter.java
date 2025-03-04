package dam.pmdm.spyrothedragon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Character;
import dam.pmdm.spyrothedragon.utils.FireAnimationView;

public class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder> {

    private List<Character> list;

    public CharactersAdapter(List<Character> charactersList) {
        this.list = charactersList;
    }

    @Override
    public CharactersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CharactersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharactersViewHolder holder, int position) {
        Character character = list.get(position);
        holder.nameTextView.setText(character.getName());

        // Cargar la imagen (simulado con un recurso drawable)
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(character.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);

        // Agregar el OnLongClickListener solo al primer elemento
        if (position == 0) {
            holder.itemView.setOnLongClickListener(v -> {
                Context context = v.getContext();
                // Crear un Dialogo para mostrar la animación
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                FireAnimationView fireView = new FireAnimationView(context);
                builder.setView(fireView);
                builder.setCancelable(true); // Permitir cerrar tocando fuera

                android.app.AlertDialog dialog = builder.create();
                dialog.show();

                // Cerrar la animación después de 2 segundos
                new android.os.Handler().postDelayed(() -> {
                    fireView.stopAnimation();
                    dialog.dismiss();
                }, 5000);

                return true; // Indica que el evento fue manejado
            });
        } else {
            holder.itemView.setOnLongClickListener(null); // Eliminar listener en otros elementos
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CharactersViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView imageImageView;

        public CharactersViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }

}
