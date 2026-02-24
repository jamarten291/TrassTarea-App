package pmdm.jmh.app_gestion_tareas.ui.tarea;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnimatedBackgroundView extends View {

    // Tipos de formas
    private static final int TIPO_CIRCULO   = 0;
    private static final int TIPO_CUADRADO  = 1;
    private static final int TIPO_TRIANGULO = 2;
    private static final int TIPO_ESTRELLA  = 3;

    // Configuración general
    /** Tamaño base de cada forma en píxeles */
    private static final float TAMANO = 80f;

    /** Intervalo de refresco del canvas (≈60 fps) */
    private static final long INTERVALO_MS = 16L;

    // Colores semitransparentes (alfa = 140, ~55% de opacidad)
    private static final int[] COLORES = {
            Color.argb(140, 231,  76,  60),   // Rojo
            Color.argb(140,  52, 152, 219),   // Azul
            Color.argb(140,  46, 204, 113),   // Verde
            Color.argb(140, 155,  89, 182),   // Morado
            Color.argb(140, 241, 196,  15),   // Amarillo
            Color.argb(140, 230, 126,  34),   // Naranja
            Color.argb(140,  26, 188, 156),   // Turquesa
            Color.argb(140, 236,  72, 153),   // Rosa
            Color.argb(140, 149, 165, 166),   // Gris azulado
            Color.argb(140,  39, 174,  96),   // Verde oscuro
            Color.argb(140, 211,  84,   0),   // Naranja oscuro
            Color.argb(140, 142,  68, 173),   // Púrpura
    };

    // Clase interna: Forma
    private static class Forma {
        int tipo;

        // Posición actual del centro
        float x, y;

        // Velocidad en px por tick
        float velX, velY;
        int color;
        Paint paint;

        Forma(int tipo, float x, float y, float velX, float velY, int color) {
            this.tipo  = tipo;
            this.x     = x;
            this.y     = y;
            this.velX  = velX;
            this.velY  = velY;
            this.color = color;
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.paint.setColor(color);
            this.paint.setStyle(Paint.Style.FILL);
        }
    }

    // Campos de la vista
    private final List<Forma> formas = new ArrayList<>();
    private ExecutorService executor;
    private volatile boolean corriendo = false;
    private final Random random = new Random();

    // Constructores
    public AnimatedBackgroundView(Context context) {
        super(context);
        init();
    }

    public AnimatedBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // Inicialización
    private void init() {
        // Pool de 12 hilos: uno por cada forma
        executor = Executors.newFixedThreadPool(12);
    }

    // onSizeChanged: se llama cuando la vista tiene dimensiones reales
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        if (w > 0 && h > 0) {
            crearFormas(w, h);
            iniciarAnimacion();
        }
    }

    // Creación de las 12 formas con posición y dirección aleatorias
    private void crearFormas(int anchoTotal, int altoTotal) {
        formas.clear();

        // Tipos: 3 de cada uno → 12 en total
        int[] tipos = {
                TIPO_CIRCULO,   TIPO_CIRCULO,   TIPO_CIRCULO,
                TIPO_CUADRADO,  TIPO_CUADRADO,  TIPO_CUADRADO,
                TIPO_TRIANGULO, TIPO_TRIANGULO, TIPO_TRIANGULO,
                TIPO_ESTRELLA,  TIPO_ESTRELLA,  TIPO_ESTRELLA
        };

        // Margen para que la forma no nazca fuera de pantalla
        float margen = TAMANO;

        for (int i = 0; i < tipos.length; i++) {
            // Posición inicial aleatoria dentro de los límites
            float x = margen + random.nextFloat() * (anchoTotal - 2 * margen);
            float y = margen + random.nextFloat() * (altoTotal  - 2 * margen);

            // Dirección completamente aleatoria (0°-360°)
            double angulo = random.nextDouble() * 2 * Math.PI;

            // Velocidad única por forma (entre 2 y 6 px/tick)
            float velocidad = 2f + random.nextFloat() * 4f;

            float velX = (float) (Math.cos(angulo) * velocidad);
            float velY = (float) (Math.sin(angulo) * velocidad);

            formas.add(new Forma(tipos[i], x, y, velX, velY, COLORES[i]));
        }
    }

    // Animación: lanza un hilo por forma
    private void iniciarAnimacion() {
        corriendo = true;
        for (Forma forma : formas) {
            executor.submit(() -> animarForma(forma));
        }
    }

    /** Bucle de movimiento para una sola forma (se ejecuta en su propio hilo) */
    private void animarForma(Forma forma) {
        while (corriendo) {
            moverForma(forma);
            postInvalidate(); // pide redibujar el canvas desde hilo secundario
            try {
                Thread.sleep(INTERVALO_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /** Actualiza la posición y gestiona el rebote en los bordes */
    private synchronized void moverForma(Forma forma) {
        int ancho = getWidth();
        int alto  = getHeight();

        forma.x += forma.velX;
        forma.y += forma.velY;

        float mitad = TAMANO / 2f;

        // Rebote horizontal
        if (forma.x - mitad < 0) {
            forma.x = mitad;
            forma.velX = Math.abs(forma.velX);
        } else if (forma.x + mitad > ancho) {
            forma.x = ancho - mitad;
            forma.velX = -Math.abs(forma.velX);
        }

        // Rebote vertical
        if (forma.y - mitad < 0) {
            forma.y = mitad;
            forma.velY = Math.abs(forma.velY);
        } else if (forma.y + mitad > alto) {
            forma.y = alto - mitad;
            forma.velY = -Math.abs(forma.velY);
        }
    }

    // onDraw: dibuja todas las formas
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Forma forma : formas) {
            switch (forma.tipo) {
                case TIPO_CIRCULO: dibujarCirculo(canvas, forma); break;
                case TIPO_CUADRADO: dibujarCuadrado(canvas, forma); break;
                case TIPO_TRIANGULO: dibujarTriangulo(canvas, forma); break;
                case TIPO_ESTRELLA: dibujarEstrella(canvas, forma); break;
            }
        }
    }

    // Métodos de dibujo individuales
    private void dibujarCirculo(Canvas canvas, Forma f) {
        canvas.drawCircle(f.x, f.y, TAMANO / 2f, f.paint);
    }

    private void dibujarCuadrado(Canvas canvas, Forma f) {
        float mitad = TAMANO / 2f;
        canvas.drawRect(f.x - mitad, f.y - mitad, f.x + mitad, f.y + mitad, f.paint);
    }

    private void dibujarTriangulo(Canvas canvas, Forma f) {
        float mitad = TAMANO / 2f;
        Path path = new Path();
        path.moveTo(f.x,          f.y - mitad);       // vértice superior
        path.lineTo(f.x + mitad,  f.y + mitad);       // vértice inferior derecho
        path.lineTo(f.x - mitad,  f.y + mitad);       // vértice inferior izquierdo
        path.close();
        canvas.drawPath(path, f.paint);
    }

    private void dibujarEstrella(Canvas canvas, Forma f) {
        int puntas    = 5;
        float radioEx = TAMANO / 2f;       // radio exterior
        float radioIn = radioEx * 0.4f;    // radio interior

        Path path = new Path();
        for (int i = 0; i < puntas * 2; i++) {
            // Alternar radio exterior e interior
            float radio = (i % 2 == 0) ? radioEx : radioIn;
            // Ángulo: empezamos en -90° (punta arriba) y rotamos
            double angulo = Math.PI / puntas * i - Math.PI / 2;
            float px = f.x + (float) (Math.cos(angulo) * radio);
            float py = f.y + (float) (Math.sin(angulo) * radio);
            if (i == 0) path.moveTo(px, py);
            else        path.lineTo(px, py);
        }
        path.close();
        canvas.drawPath(path, f.paint);
    }

    // Ciclo de vida: detener hilos cuando la vista se destruye

    /** Llama a este method desde onDestroy() o onPause() de la actividad */
    public void detener() {
        corriendo = false;
        executor.shutdownNow();
    }

    /** Llama a este method desde onResume() de la actividad si fue pausada */
    public void reanudar() {
        if (!corriendo && getWidth() > 0) {
            executor = Executors.newFixedThreadPool(12);
            iniciarAnimacion();
        }
    }
}