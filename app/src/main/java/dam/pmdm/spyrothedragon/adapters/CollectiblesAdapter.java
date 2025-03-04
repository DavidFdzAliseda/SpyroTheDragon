package dam.pmdm.spyrothedragon.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Collectible;

public class CollectiblesAdapter extends RecyclerView.Adapter<CollectiblesAdapter.CollectiblesViewHolder> {

    private List<Collectible> list;

    private int gemClickCount = 0;
    private Handler handler = new Handler();


    public CollectiblesAdapter(List<Collectible> collectibleList) {
        this.list = collectibleList;
    }

    @Override
    public CollectiblesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CollectiblesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CollectiblesViewHolder holder, int position) {
        Collectible collectible = list.get(position);
        holder.nameTextView.setText(collectible.getName());

        // Cargar la imagen (simulado con un recurso drawable)
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(collectible.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);

        // Detectar los clics en la gema (segundo elemento de la lista)
        if (position == 1) {
            holder.itemView.setOnClickListener(v -> {
                gemClickCount++;

                // Reiniciar el contador si no se hace clic en 1.5 segundos
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> gemClickCount = 0, 1500);

                // Si se hace clic 4 veces, reproducir el video
                if (gemClickCount == 4) {
                    showVideo(holder.itemView.getContext(), holder.itemView);
                    gemClickCount = 0;
                }
            });
        }

    }

   private void showVideo(Context context, View itemView) {
        FrameLayout videoContainer = itemView.getRootView().findViewById(R.id.videoContainer);
        VideoView videoView = itemView.getRootView().findViewById(R.id.videoView);

        if (videoContainer != null && videoView != null) {
            videoContainer.setVisibility(View.VISIBLE); // Mostrar el contenedor del video

            Uri videoUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.video);
            videoView.setVideoURI(videoUri);

            videoView.setOnCompletionListener(mp -> videoContainer.setVisibility(View.GONE)); // Ocultar el video al terminar

            videoView.start();
        }
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CollectiblesViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;

        public CollectiblesViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }

}
