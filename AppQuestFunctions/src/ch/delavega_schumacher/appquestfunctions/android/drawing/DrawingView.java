package ch.delavega_schumacher.appquestfunctions.android.drawing;

import java.util.ArrayList;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Die DrawingView ist für die Darstellung und Verwaltung der Zeichenfläche
 * zuständig.
 */
public class DrawingView extends View {

	private static final int GRID_SIZE = 11;
	
	private Path drawPath = new Path();
	private Paint drawPaint = new Paint();
	private Paint linePaint = new Paint();
	private boolean isErasing = false;
	
	private ArrayList<Point> points = new ArrayList<Point>();

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);

		linePaint.setColor(0xFF666666);
		linePaint.setAntiAlias(true);
		linePaint.setStrokeWidth(1.0f);
		linePaint.setStyle(Paint.Style.STROKE);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		final int maxX = canvas.getWidth();
		final int maxY = canvas.getHeight();

		final int stepSizeX = (int) Math.ceil((double) maxX / GRID_SIZE);
		final int stepSizeY = (int) Math.ceil((double) maxY / GRID_SIZE);

		int currentPointX = stepSizeX;
		int currentPointY = stepSizeY;
		
		for(int step = 0; step < GRID_SIZE; step++)
		{
			// x Linien zeichnen, bei denen Starty immer = 0 ist
			canvas.drawLine(currentPointX, 0, currentPointX, maxY, linePaint);
			// y Linien zeichnen, bei denen Startx immer = 0 ist
			canvas.drawLine(0, currentPointY, maxX, currentPointY, linePaint);
			
			currentPointX = currentPointX + stepSizeX;
			currentPointY = currentPointY + stepSizeY;
			
			// TODO: Punkte direkt hier überprüfen
		}
		
		// überprüfen der einzelnen Punkte
		for(int pointH = 0; pointH < GRID_SIZE; pointH++)
		{
			for(int pointS = 0; pointS < GRID_SIZE; pointS++)
			{
				// Punkt 0 - 10/0 - 10
				for(Point point : points)
				{
					if(point.getPointXAxis(stepSizeX) == pointH && point.getPointXAxis(stepSizeY) == pointS)
					{
						// TODO der Punkt muss gezeichnet werden
					}
				}
			}
		}

		// Zeichnet einen Pfad der dem Finger folgt
		canvas.drawPath(drawPath, drawPaint);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			drawPath.moveTo(touchX, touchY);
			
			Point pNew = new Point(touchX, touchY, drawPaint.getColor());
			points.add(pNew);
			
			// TODO wir müssen uns die berührten Punkte zwischenspeichern

			break;
		case MotionEvent.ACTION_MOVE:
			drawPath.lineTo(touchX, touchY);

			// TODO wir müssen uns die berührten Punkte zwischenspeichern

			break;
		case MotionEvent.ACTION_UP:

			// TODO Jetzt können wir die zwischengespeicherten Punkte auf das
			// Gitter umrechnen und zeichnen, bzw. löschen, falls wir isErasing
			// true ist (optional)

			drawPath.reset();
			break;
		default:
			return false;
		}

		invalidate();
		return true;
	}

	public void startNew() {

		// TODO Gitter löschen

		invalidate();
	}
	
	public void setErase(boolean isErase) {
		isErasing = isErase;
		if (isErasing) {
			drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		} else {
			drawPaint.setXfermode(null);
		}
	}

	public boolean isErasing() {
		return isErasing;
	}

	public void setColor(String color) {
		invalidate();
		drawPaint.setColor(Color.parseColor(color));
	}
}
