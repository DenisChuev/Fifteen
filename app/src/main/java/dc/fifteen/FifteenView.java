package dc.fifteen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class FifteenView extends SurfaceView implements SurfaceHolder.Callback {

    private int indent_w, indent_h, sizeCell, sizeField = 4;
    private Paint paint;
    private Context context;
    private DrawThread thread;
    private boolean firstStart = true;
    Game game = new Game(sizeField);

    public FifteenView(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;
        paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (firstStart) updateSizes();
        thread = new DrawThread();
        thread.running = true;
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        drawField(canvas);
    }

    private void drawField(Canvas canvas) {
        for (int i = 0; i < sizeField; i++)
            for (int j = 0; j < sizeField; j++) {
                paint.setStyle(Paint.Style.STROKE);
                int x0 = indent_w + j * sizeCell;
                int y0 = indent_h + i * sizeCell;
                int x1 = indent_w + (j + 1) * sizeCell;
                int y1 = indent_h + (i + 1) * sizeCell;
                canvas.drawRect(x0, y0, x1, y1, paint);
                paint.setStyle(Paint.Style.FILL);
                if (game.field[i][j] != 0)
                    canvas.drawText(String.valueOf(game.field[i][j]),
                            (x0 + x1) / 2, (y0 + y1) / 2 + sizeCell / 5, paint);
            }
    }

    private void updateSizes() {
        int width = getWidth();
        int height = getHeight();
        sizeCell = width / sizeField;
        indent_h = (height - width) / 2;
        indent_w = (width - sizeCell * sizeField) / 2;
        paint.setTextSize(sizeCell / 2);
        firstStart = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (x < indent_w || x > sizeCell * sizeField + indent_w ||
                y < indent_h || y > sizeCell * sizeField + indent_h)
            return super.onTouchEvent(event);
        int col = (x - indent_w) / sizeCell;
        int row = (y - indent_h) / sizeCell;
        game.makeMove(row, col);
        if (game.isWon()) {
            Toast toast = Toast.makeText(context, context.getString(R.string.win)
                    + " " + game.getMoves(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            game.init();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        thread.running = false;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException ignored) {
            }
        }
    }

    private class DrawThread extends Thread {

        private boolean running = false;

        @SuppressLint("WrongCall")
        @Override
        public void run() {
            while (running) {
                Canvas canvas = null;
                try {
                    if ((canvas = getHolder().lockCanvas()) == null) continue;
                    synchronized (getHolder()) {
                        onDraw(canvas);
                    }

                } finally {
                    if (canvas != null) getHolder().unlockCanvasAndPost(canvas);
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}