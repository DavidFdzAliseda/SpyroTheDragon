package dam.pmdm.spyrothedragon.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;

import dam.pmdm.spyrothedragon.R;

public class FireAnimationView extends View {
    private Paint paint;
    private float dynamicFlameHeight = 50; // Altura inicial de la llama animada
    private float baseFlameHeight = 150;  // Altura FIJA de la base naranja
    private float flameHeight = 300; // Altura inicial del fuego
    private ValueAnimator animator;
    private Bitmap backgroundBitmap; // Imagen de fondo
    private Rect backgroundRect; // Rectángulo para ajustar la imagen
    private MediaPlayer mediaPlayer; //Controlador de sonido
    private boolean isSoundPlaying = false; //Controla si el sonido está activo


    public FireAnimationView(Context context) {
        super(context);
        init(context);
    }

    public FireAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        mediaPlayer = MediaPlayer.create(context, R.raw.fuego);

        mediaPlayer.setLooping(true); // 🔁 Para que se repita mientras haya fuego


        setBackgroundColor(Color.TRANSPARENT);

        // Cargar imagen de fondo desde drawable
        Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.spyro);

        // Redimensionar la imagen para que ocupe toda la vista
        backgroundBitmap = Bitmap.createScaledBitmap(originalBitmap, 1200, 1200, true);

        // Definir el rectángulo donde se dibujará la imagen
        backgroundRect = new Rect(0, 0, 1200, 1200);

        // Animación que varía solo la altura de la llama roja y amarilla
        animator = ValueAnimator.ofFloat(50, 200, 100);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(animation -> {
            dynamicFlameHeight = (float) animation.getAnimatedValue();
            invalidate(); // Redibujar la vista
        });
        animator.start();
        if (!isSoundPlaying) {
            mediaPlayer.start(); // ▶ Iniciar el sonido
            isSoundPlaying = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);

        // Dibujar la imagen de fondo escalada (Spyro)
        if (backgroundBitmap != null) {
            canvas.drawBitmap(backgroundBitmap, null, backgroundRect, null);
        }

        // Ubicación base de la boca
        float dragonMouthX = width / 2f;
        float dragonMouthY = height / 3f;

        // Ajuste de posición
        float adjustX = 0;
        float adjustY = 50;

        // Aplicar ajustes
        dragonMouthX += adjustX;
        dragonMouthY += adjustY;

        // Guardar el estado del canvas
        canvas.save();

        // Mover el fuego a la boca del dragón con los ajustes
        canvas.translate(dragonMouthX+5, dragonMouthY-150);

        // Rotar solo el fuego
        //canvas.rotate(180);

        //Dibujar la base del fuego (naranja)
        paint.setColor(Color.rgb(255, 140, 0));
        canvas.drawOval(-width / 8f, 0, width / 8f, flameHeight / 2f, paint);

        //Llama interna (rojo) - Crece dinámicamente
        paint.setColor(Color.RED);
        canvas.drawOval(-width / 10f, -0, width / 10f, baseFlameHeight + dynamicFlameHeight, paint);

        //Parte más brillante del fuego (amarillo) - Crece dinámicamente
        paint.setColor(Color.YELLOW);
        canvas.drawOval(-width * 0.05f, -0 * 1.2f, width * 0.05f, baseFlameHeight + dynamicFlameHeight * 1.2f, paint);

        // Restaurar el estado original del canvas
        canvas.restore();
    }

    public void stopAnimation() {
        animator.cancel();
        mediaPlayer.pause();
    }
}
