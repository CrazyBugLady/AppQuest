package ch.delavega_schumacher.appquestfunctions.android.drawing;

import org.json.JSONException;
import org.json.JSONObject;

public class Point {
	private float touchX;
	private float touchY;
	private int color;
	
	public Point(float touchX, float touchY, int color)
	{
		this.touchX = touchX;
		this.touchY = touchY;
		this.color = color;
	}
	
	public int getPointXAxis(int xSize)
	{
		return (int) Math.ceil((double) touchX / xSize);
	}
	
	public int getPointYAxis(int ySize)
	{
		return (int) Math.ceil((double) touchY / ySize);
	}
	
	public JSONObject getJSONPoint(int xSize, int ySize) throws JSONException
	{
		JSONObject PointObject = new JSONObject();
		PointObject.put("color", color);
		PointObject.put("x", getPointXAxis(xSize));
		PointObject.put("y", getPointYAxis(ySize));
		
		return PointObject;
	}
}
