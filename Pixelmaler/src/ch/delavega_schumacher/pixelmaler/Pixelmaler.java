package ch.delavega_schumacher.pixelmaler;

import ch.delavega_schumacher.appquestfunctions.Logging.Logbook;
import ch.delavega_schumacher.appquestfunctions.android.Application;
import ch.delavega_schumacher.appquestfunctions.android.drawing.DrawingView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ImageButton;

public class Pixelmaler extends Activity {

	private DrawingView drawingView;
	private ImageButton currentBrush;
	
	private Logbook log = Logbook.getInstance();
	private Application application = Application.getInstance();

	public void eraseClicked(View view) {
		if (view != currentBrush) {
			ImageButton imgView = (ImageButton) view;
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.selected));
			currentBrush.setImageDrawable(null);
			currentBrush = (ImageButton) view;
		}

		drawingView.setErase(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pixelmaler);
		drawingView = (DrawingView) findViewById(R.id.drawing);

		currentBrush = (ImageButton) findViewById(R.id.defaultColor);
		currentBrush.setImageDrawable(getResources().getDrawable(R.drawable.selected));
		String color = currentBrush.getTag().toString();
		drawingView.setColor(color);
	}

	private void onCreateNewDrawingAction() {
		AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
		newDialog.setTitle("New Drawing");
		newDialog.setMessage("Start a new drawing?");
		newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				drawingView.startNew();
				dialog.dismiss();
			}
		});
		newDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		newDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.add("New");
		menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				onCreateNewDrawingAction();
				return true;
			}
		});

		menuItem = menu.add("Log");
		menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				log();
				return false;
			}
		});

		return true;
	}

	public void paintClicked(View view) {
		if (view != currentBrush) {
			ImageButton imgView = (ImageButton) view;
			String color = view.getTag().toString();
			drawingView.setColor(color);
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.selected));
			currentBrush.setImageDrawable(null);
			currentBrush = (ImageButton) view;
		}
		drawingView.setErase(false);
	}

	private void log() {
		try
		{
			String taskname, logMessage;
		
			taskname = "Pixelmaler";
			logMessage = "Insert the points";
			
			Intent Logger = log.log(this, taskname, logMessage);
			startActivity(Logger);
		}
		catch(Exception ex)
		{
			application.showErrors(this, "The Logging didn't work for some reason, please contact the administrator.");
		}
	}

}
