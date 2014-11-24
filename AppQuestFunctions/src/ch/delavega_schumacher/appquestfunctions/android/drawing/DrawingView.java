package ch.delavega_schumacher.appquestfunctions.android.drawing;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import ch.delavega_schumacher.appquestfunctions.Mathematics.Trigonometry.RectangularTriangle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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

	private int sizeStepX = 0;
	private int sizeStepY = 0;

	private Path drawPath = new Path();
	private Paint drawPaint = new Paint();
	private Paint pointfillerPaint = new Paint();
	private Paint linePaint = new Paint();
	private boolean isErasing = false;

	private Bitmap currentImage = null;

	private ArrayList<Point> points = new ArrayList<Point>();

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);

		pointfillerPaint.setStrokeWidth(0);

		linePaint.setColor(0xFF666666);
		linePaint.setAntiAlias(true);
		linePaint.setStrokeWidth(1.0f);
		linePaint.setStyle(Paint.Style.STROKE);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		final int maxX = canvas.getWidth();
		final int maxY = canvas.getHeight();

		sizeStepX = (int) Math.ceil((double) maxX / GRID_SIZE);
		sizeStepY = (int) Math.ceil((double) maxY / GRID_SIZE);

		int currentPointX = sizeStepX;
		int currentPointY = sizeStepY;

		for(int step = 0; step < GRID_SIZE; step++)
		{
			// x Linien zeichnen, bei denen Starty immer = 0 ist
			canvas.drawLine(currentPointX, 0, currentPointX, maxY, linePaint);
			// y Linien zeichnen, bei denen Startx immer = 0 ist
			canvas.drawLine(0, currentPointY, maxX, currentPointY, linePaint);

			currentPointX = currentPointX + sizeStepX;
			currentPointY = currentPointY + sizeStepY;

		}

		// überprüfen der einzelnen Punkte
		for(Point point : points)
		{
			pointfillerPaint.setColor(point.getColor()); // Pinsel, der nur zum letztendlichen Ausmalen verwendet wird

			canvas.drawRect(point.getPointXAxis(sizeStepX) * sizeStepX, point.getPointYAxis(sizeStepY) * sizeStepY, (point.getPointXAxis(sizeStepX) + 1) * sizeStepX, (point.getPointYAxis(sizeStepY) + 1) * sizeStepY, pointfillerPaint);
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

			Point tempPNew = new Point(touchX, touchY, drawPaint.getColor()); // wird nur gespeichert, wenn nicht isErasing = true ist
			removeFromPoints(tempPNew.getPointXAxis(sizeStepX), tempPNew.getPointXAxis(sizeStepY)); // absichern, dass ein Punkt nicht einfach doppelt gesetzt und somit übermalt wird

			if(!isErasing) {
				points.add(tempPNew);
			}

			break;
		case MotionEvent.ACTION_MOVE:
			drawPath.lineTo(touchX, touchY);

			// TODO wir müssen uns die berührten Punkte zwischenspeichern

			break;
		case MotionEvent.ACTION_UP:

			// TODO Jetzt können wir die zwischengespeicherten Punkte auf das

			// Gitter umrechnen und zeichnen, bzw. löschen, falls isErasing
			// true ist (optional)

			drawPath.reset();
			break;
		default:
			return false;
		}
		invalidate();
		return true;
	}

	// Fürs Teilen über Whatsapp oder dergleichiges
	public Bitmap getImage()
	{
		Bitmap currentPainting = null;

		this.setDrawingCacheEnabled(true);
		this.buildDrawingCache();

		currentPainting = this.getDrawingCache();

		return currentPainting;
	}

	public void removeFromPoints(int PointX, int PointY)
	{
		ArrayList<Point> tempListPoints = new ArrayList<Point>();     
		for(Point pointToDelete : points)
		{
			// angenommen unser Punkt, den wir löschen wollen ist 0 / 1 dann kommt 1 / 1 durch oder 1 / 0 aber 0 / 1 nicht 
			if (pointToDelete.getPointXAxis(this.sizeStepX) == PointX && pointToDelete.getPointYAxis(this.sizeStepY) == PointY) // wenn beide übereinstimmen
			{
				// nicht hinzufügen
			}
			else
			{
				tempListPoints.add(pointToDelete);
			}

		}
		points = tempListPoints;
	}

	public JSONArray getJSONPoints() throws JSONException
	{
		JSONArray PointArray = new JSONArray();

		for(Point PointObject : points)
		{
			PointArray.put(PointObject.getJSONPoint(sizeStepX, sizeStepY));
		}

		return PointArray;
	}

	public void startNew() {

		points = new ArrayList<Point>();

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
