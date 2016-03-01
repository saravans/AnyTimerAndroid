package com.teamsouls.anytimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class SoulsLapTimerActivity extends Activity {

	private TextView timeLabel;

	public TextView txtLogLabel;

	public TextView txtLogId;

	public TextView txtLogData;

	private Button btnSplit;

	private Button btnStartStop;

	private TextView txtEventName;

	private static long DELAY = 1;

	private static String RUNNING = "running";

	private static String STOPPED = "stopped";

	private String applicationState = STOPPED;

	private long startTimePoint;

	private long timerValue;

	private String hours, minutes, seconds, milliseconds;

	private long secs, mins, hrs;

	private int lapCount;

	private Handler tasksHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);
		afterInit();
	}

	private void afterInit() {
		Typeface font = Typeface.createFromAsset(getAssets(),
				"LiquidCrystal-Normal.otf");
		txtEventName = (TextView) findViewById(R.id.eventName);
		txtEventName.setTypeface(font);
		timeLabel = (TextView) findViewById(R.id.txtTimer);
		timeLabel.setTypeface(font);
		btnStartStop = (Button) findViewById(R.id.btnStartStop);
		btnStartStop.setTypeface(font);
		txtLogLabel = (TextView) findViewById(R.id.txtLogLabel);
		txtLogId = (TextView) findViewById(R.id.txtLogId);
		txtLogData = (TextView) findViewById(R.id.txtLogData);
		btnSplit = (Button) findViewById(R.id.btnSplit);
		btnSplit.setTypeface(font);
		btnSplit.setEnabled(false);
		setLabelText("00:00:00");
		startTimePoint = Long.valueOf(0);
		 btnStartStop.setOnTouchListener(onClickBtnStartStop);
		 btnSplit.setOnTouchListener(onClickBtnSplit);
		txtEventName.setOnTouchListener(onClickEventName);
	}

	private void showAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Event Name");
		alert.setMessage("Enter Event Name");

		final EditText input = new EditText(this);
		alert.setView(input);
		input.setText(txtEventName.getText());
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				txtEventName.setText(value);
				txtEventName.setOnTouchListener(onClickEventName);
				return;
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						txtEventName.setOnTouchListener(onClickEventName);
						return;
					}
				});
		alert.show();

	};

	public void aboutUsPop() {
		AlertDialog.Builder alertadd = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final View view = factory.inflate(R.layout.aboutus, null);
		alertadd.setTitle("About Us");
		alertadd.setView(view);
		alertadd.setPositiveButton("Feedback",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						email("digitalstopwatch@gmail.com", "");
						return;
					}
				});

		alertadd.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});

		alertadd.show();
	}

	private OnTouchListener onClickEventName = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == (MotionEvent.ACTION_DOWN)) {
				txtEventName.setOnTouchListener(null);
				showAlert();
				return true;
			}

			return false;
		}
	};

	private OnTouchListener onClickBtnStartStop = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == (MotionEvent.ACTION_DOWN)) {

				if (applicationState == RUNNING) {
					stopCounting();
					timerValue = time;
					btnStartStop.setText("");
				} else if (applicationState == STOPPED) {
					startCounting();
					btnStartStop.setText("");
				}
				return true;
			}
			if (event.getAction() == (MotionEvent.ACTION_UP)) {

				if (applicationState == STOPPED) {
					btnStartStop
							.setBackgroundResource(R.drawable.startbuttonstates);
					btnSplit.setBackgroundResource(R.drawable.resetbuttonstates);
				} else if (applicationState == RUNNING) {
					btnSplit.setEnabled(true);
					btnStartStop
							.setBackgroundResource(R.drawable.stopbuttonstates);
					btnSplit.setBackgroundResource(R.drawable.lapbuttonstates);
				}
				return true;
			}

			return false;
		}
	};

	private String addLapTime(String value) {
		String[] lapTimes = value.split("\n");
		Long totalMSec = Long.valueOf(0);
		int length = value.split("\n").length;
		if (length > 1) {
			String[] lapDigits1 = lapTimes[length - 1].split(":");
			String[] lapDigits2 = lapTimes[length - 2].split(":");
			totalMSec = ((Long.valueOf(lapDigits1[0]) * 1000 * 60)
					+ (Long.valueOf(lapDigits1[1]) * 1000) + Long
					.valueOf(lapDigits1[2]) * 10)
					- ((Long.valueOf(lapDigits2[0]) * 1000 * 60)
							+ (Long.valueOf(lapDigits2[1]) * 1000) + Long
							.valueOf(lapDigits2[2]) * 10);
			System.out.println(totalMSec + "");
		} else {
			String[] lapDigits = lapTimes[0].split(":");
			Long min = Long.valueOf(lapDigits[0]);
			Long sec = Long.valueOf(lapDigits[1]);
			Long msec = Long.valueOf(lapDigits[2]);
			totalMSec = (min * 1000 * 60) + (sec * 1000) + msec * 10;
		}
		return calcMiliSecToSec(totalMSec);
	}

	private Boolean isReset = false;
	private OnTouchListener onClickBtnSplit = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == (MotionEvent.ACTION_DOWN)) {
				btnSplit.setSelected(true);
				if (applicationState == RUNNING) {
					lapCount = lapCount + 1;
					txtLogLabel.append("Lap Name " + lapCount + " \n");
					txtLogData.append(timerS + "\n");
					txtLogId.append(addLapTime(txtLogData.getText().toString())
							+ "\n");

				} else if (applicationState == STOPPED) {
					btnSplit.setBackgroundResource(R.drawable.lapbuttonstates);
					btnSplit.setEnabled(false);
					lapCount = 0;
					timeLabel.setText("00:00:00");
					txtLogLabel.setText("");
					txtLogId.setText("");
					txtLogData.setText("");
					isReset = true;
				}
				return true;
			}

			return false;
		}
	};

	public void startCounting() {
		applicationState = RUNNING;
		tasksHandler = new Handler();
		tasksHandler.postDelayed(timeTickRunnable, DELAY);
		startTimePoint = System.currentTimeMillis();
	}

	public void stopCounting() {
		if (tasksHandler != null)
			tasksHandler.removeCallbacks(timeTickRunnable);
		tasksHandler = null;
		applicationState = STOPPED;

	}

	public void setLabelText(String string) {
		timeLabel.setText(string);
	}

	private Runnable timeTickRunnable = new Runnable() {
		public void run() {
			if (applicationState == RUNNING) {
				setLabelText(updateTimer());
				tasksHandler.postDelayed(timeTickRunnable, DELAY);
			}
		}
	};
	private long time;

	private String updateTimer() {
		time = System.currentTimeMillis() - startTimePoint;
		if (isReset) {
			timerValue = Long.valueOf("0");
			isReset = false;
		}
		if (!isReset) {
			time = time + timerValue;
			isReset = false;
		}
		return calcMiliSecToSec(time);
	}

	private String calcMiliSecToSec(Long time) {
		secs = (long) (time / 1000);
		mins = (long) ((time / 1000) / 60);
		hrs = (long) (((time / 1000) / 60) / 60);

		secs = secs % 60;
		seconds = String.valueOf(secs);
		if (secs == 0) {
			seconds = "00";
		}
		if (secs < 10 && secs > 0) {
			seconds = "0" + seconds;
		}

		mins = mins % 60;
		minutes = String.valueOf(mins);
		if (mins == 0) {
			minutes = "00";
		}
		if (mins < 10 && mins > 0) {
			minutes = "0" + minutes;
		}

		hours = String.valueOf(hrs);
		if (hrs == 0) {
			hours = "00";
		}
		if (hrs < 10 && hrs > 0) {
			hours = "0" + hours;
		}

		milliseconds = String.valueOf((long) time);
		if (milliseconds.length() <= 1) {
			milliseconds = "0" + milliseconds;
		}
		if (milliseconds.length() > 2) {
			milliseconds = milliseconds.substring(milliseconds.length() - 3,
					milliseconds.length() - 1);
		}

		timerS = (minutes + ":" + seconds + ":" + milliseconds);
		return timerS;
	}

	private void createEmail() {
		Date d = new Date();
		CharSequence s = DateFormat.format("dd-MMM-yyyy", d.getTime());
		String strDate = (txtEventName.getText() + "  " + s + " " + DateFormat
				.getTimeFormat(this/* Context */).format(d.getTime()));
		String[] lapn = txtLogLabel.getText().toString().split("\n");
		String[] lapid = txtLogId.getText().toString().split("\n");
		String[] lapdata = txtLogData.getText().toString().split("\n");
		int length = txtLogLabel.getText().toString().split("\n").length;
		TextView textLop = new TextView(this);
		textLop.setText("Lap Name     Lap          Lap Data\n");
		for (int i = 0; i < length; i++) {
			textLop.append(lapn[i] + " " + lapid[i] + " " + lapdata[i] + "\n");
		}
		if (textLop.getText().toString().length() <= 38) {
			showOkAlert("Email", "No Lap Content to Mail");
		} else
			email("", strDate + "\n" + textLop.getText());

	}

	private void showOkAlert(String title, String msg) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(title);
		alert.setMessage(msg);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				return;
			}
		});
		alert.show();
	}

	private String timerS;
	Intent emailIntent;

	private void email(String toEmail, String emailText) {
		emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { toEmail });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				new String[] {});
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailText);
		SoulsLapTimerActivity.this.startActivity(Intent.createChooser(
				emailIntent, "Send mail..."));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu2:
			Intent intent = new Intent(this, TimerActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.menu1:
			createEmail();
			break;
		case R.id.menu3:
			aboutUsPop();
			break;
		}
		return true;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showSaveAlert("Exit","Do you want to exit?");
			return true;
		}
		return false;
	}
	private void showSaveAlert(String title, String msg) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(title);
		alert.setMessage(msg);
		
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
				return;
			}
		});
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				return;
			}
		});
		alert.show();
	}
	
}