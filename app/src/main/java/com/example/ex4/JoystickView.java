package com.example.ex4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/*
    JoystickView - this class is responseble on the joystick place , and send this place to the client
 */
public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    // the center of the screan
    private float centerX;
    private float centerY;
    // radius of the base of the joystick
    private float baseRadius;
    private float hatRadius; // the radius of the joystick
    private JoystickListener joystickListener;
    private TcpClient client;
    private static final String AILERON = "set controls/flight/aileron ";
    private static final String ELEVATOR = "set controls/flight/elevator ";

    /*
    set the parame by the screan
     */
    private void setupDimensions() {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / 3;
        hatRadius = Math.min(getWidth(), getHeight()) / 5;
    }

    // constractors
    public JoystickView(Context context, String ip, int port) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof JoystickListener)
            joystickListener = (JoystickListener) context;
        client = new TcpClient(ip, port);
    }

    public JoystickView(Context c, AttributeSet a, int style) {
        super(c, a, style);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (c instanceof JoystickListener)
            joystickListener = (JoystickListener) c;
    }

    public JoystickView(Context c, AttributeSet a) {
        super(c, a);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (c instanceof JoystickListener)
            joystickListener = (JoystickListener) c;
    }

    /*
    draw the joystick by his new place
     */
    private void drawJoystick(float newX, float newY) {
        if (getHolder().getSurface().isValid()) {
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            colors.setARGB(255, 194, 198, 206); // color of the base joystick gray
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors);
            colors.setARGB(255, 0, 0, 255); // color of the joystick blue
            myCanvas.drawCircle(newX, newY, hatRadius, colors);
            getHolder().unlockCanvasAndPost(myCanvas);
        }
    }

    // send the values by the place
    private void sendToServer(float x, float y) {
        float disX = (x - centerX) / baseRadius;
        float disY = (y - centerY) / baseRadius;
        String aileron = AILERON + Float.toString(disX) + "\r\n";
        client.sendMessage(aileron);
        String elevator = ELEVATOR + Float.toString(disY) + "\r\n";
        client.sendMessage(elevator);
    }

    /*
    Checks where to draw the joystick according to user touch values
     */
    public boolean onTouch(View view, MotionEvent e) {
        if (view.equals(this)) {
            if (e.getAction() != e.ACTION_UP) { // if the user move the joystick
                float displacement = (float) Math.sqrt(Math.pow(e.getX() - centerX, 2)
                        + Math.pow(e.getY() - centerY, 2));
                if (displacement < baseRadius) {
                    drawJoystick(e.getX(), e.getY());
                    sendToServer(e.getX(), e.getY());
                } else {
                    float ratio = baseRadius / displacement;
                    float conX = centerX + (e.getX() - centerX) * ratio;
                    float conY = centerY + (e.getY() - centerY) * ratio;
                    drawJoystick(conX, conY);
                    sendToServer(conX, conY);
                    joystickListener.onJoystickMoved((conX - centerX) / baseRadius,
                            (conY - centerY) / baseRadius, getId());
                }
            } else {
                drawJoystick(centerX, centerY);
                sendToServer(centerX, centerY);
            }
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        client.stopClient();
    }

    public interface JoystickListener {
        void onJoystickMoved(float xPercent, float yPercent, int id);
    }
}
