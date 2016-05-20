package org.rul.bluetoothserial.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by rgonzalez on 19/05/2016.
 */
public class Control  extends View {


    public Control(Context context) {
        super(context);
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawRGB(255, 255, 200);
        int ancho = canvas.getWidth();
        int alto = canvas.getHeight();
        int altoUnidad = alto / 12;
        int anchoUnidad = ancho / 10;
        Paint pincel1 = new Paint();
        pincel1.setARGB(255, 255, 0, 0);
        canvas.drawLine(0, 30, ancho, 30, pincel1);
        pincel1.setStrokeWidth(4);
        canvas.drawLine(0, 60, ancho, 60, pincel1);
    }
}
