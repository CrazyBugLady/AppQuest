package ch.delavega_schumacher.appquestfunctions.android.drawing;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;

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

	private Bitmap currentPainting = null;

	private HashMap<String, Point> points = new HashMap<String, Point>();

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

		// überprüfen der einzelnen Punkte

		for(Entry<String, Point> pointEntry : points.entrySet()) {
			Point point = (Point)pointEntry.getValue();

			pointfillerPaint.setColor(point.getColor()); // Pinsel, der nur zum letztendlichen Ausmalen verwendet wird

			canvas.drawRect(point.getPointXAxis(sizeStepX) * sizeStepX, point.getPointYAxis(sizeStepY) * sizeStepY, (point.getPointXAxis(sizeStepX) + 1) * sizeStepX, (point.getPointYAxis(sizeStepY) + 1) * sizeStepY, pointfillerPaint);  
		}

		for(int step = 0; step < GRID_SIZE; step++)
		{
			// x Linien zeichnen, bei denen Starty immer = 0 ist
			canvas.drawLine(currentPointX, 0, currentPointX, maxY, linePaint);
			// y Linien zeichnen, bei denen Startx immer = 0 ist
			canvas.drawLine(0, currentPointY, maxX, currentPointY, linePaint);

			currentPointX = currentPointX + sizeStepX;
			currentPointY = currentPointY + sizeStepY;

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

			savePoint(touchX, touchY);
			break;
		case MotionEvent.ACTION_MOVE:
			drawPath.lineTo(touchX, touchY);

			savePoint(touchX, touchY);
			break;
		case MotionEvent.ACTION_UP:
			drawPath.reset();
			break;
		default:
			return false;
		}
		invalidate();
		return true;
	}

	public void savePoint(float touchX, float touchY)
	{	
		Point tempPNew = new Point(touchX, touchY, drawPaint.getColor()); // wird nur gespeichert, wenn nicht isErasing = true ist

		int X = tempPNew.getPointXAxis(sizeStepX);
		int Y = tempPNew.getPointYAxis(sizeStepY);

		if(X <= (GRID_SIZE-1) && X >= 0 && Y <= (GRID_SIZE - 1) && Y >= 0)
		{	
			points.put(X + "/" + Y, tempPNew);

			if(isErasing) {
				removeFromPoints(tempPNew);
			}
		}
	}

	// Fürs Teilen über Whatsapp oder dergleichiges
	// TODO weiterverwenden für Teilen auf Whatsapp oder dergleichigem
	public Bitmap getImage()
	{
		this.setDrawingCacheEnabled(true);
		this.buildDrawingCache();

		currentPainting = this.getDrawingCache();

		return currentPainting;
	}

	public void removeFromPoints(Point pointToRemove)
	{
		try 
		{
			for(Iterator<Map.Entry<String,Point>>it=points.entrySet().iterator();it.hasNext();){
				Map.Entry<String, Point> entry = it.next();
				if (entry.getValue() == pointToRemove) {
					it.remove();
				}
			}
		}
		catch(Exception ex)
		{

		}
	}

	public JSONArray getJSONPoints() throws JSONException
	{
		JSONArray PointArray = new JSONArray();

		for(int x = 0; x < GRID_SIZE; x++)
		{
			for(int y = 0; y < GRID_SIZE; y++)
			{
				try{
					Point point = points.get(x + "/" + y);
					PointArray.put(point.getJSONPoint(sizeStepX, sizeStepY));}
				catch(Exception ex)
				{
					// Just go to next shit you dense motherfucker
				}
			}

		}
		return PointArray;
	}

	public void startNew() {

		points = new HashMap<String, Point>();

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
