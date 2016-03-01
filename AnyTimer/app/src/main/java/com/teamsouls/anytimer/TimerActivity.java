package com.teamsouls.anytimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class TimerActivity extends Activity {

	private TextView timeLabel;

	private Button btnSetTime;

	private Button btnStartStop;

	private TextView txtEventName;

	private static String RUNNING = "running";

	private static String STOPPED = "stopped";

	private String applicationState = STOPPED;

	private long hrs, secs, mins;

	private String minutes, seconds, hours;

	NotificationManager mNotificationManager;

	private static final int HELLO_ID = 1;

	Notification notification;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timerlayout);

		afterInit();
	}

	private MalibuCountDownTimer countDownTimer;

	private void afterInit() {
		Typeface font = Typeface.createFromAsset(getAssets(),
				"LiquidCrystal-Normal.otf");
		txtEventName = (TextView) findViewById(R.id.eventName);
		txtEventName.setTypeface(font);
		timeLabel = (TextView) findViewById(R.id.txtTimer);
		timeLabel.setTypeface(font);
		btnStartStop = (Button) findViewById(R.id.btnStartStop);
		btnStartStop.setTypeface(font);
		btnSetTime = (Button) findViewById(R.id.btnSet);
		btnSetTime.setTypeface(font);
		setLabelText("00:01:00");

		btnStartStop.setOnTouchListener(onClickBtnStartStop);
		btnSetTime.setOnTouchListener(onClickbtnSelectTime);
		txtEventName.setOnTouchListener(onClickEventName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.timermenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu2:
			Intent intent = new Intent(this, SoulsLapTimerActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.menu3:
			aboutUsPop();
			break;
		}
		return true;
	}

	private OnTouchListener onClickbtnSelectTime = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == (MotionEvent.ACTION_DOWN)) {
				if (countDownTimer != null)
					countDownTimer.cancel();
				btnStartStop
						.setBackgroundResource(R.drawable.startbuttonstates);
				showTimerPopUp();
				return true;
			}

			return false;
		}
	};

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
					btnStartStop.setText("");
					stopCounting();

				} else if (applicationState == STOPPED) {
					startCounting();
				}
				return true;
			}
			if (event.getAction() == (MotionEvent.ACTION_UP)) {

				if (applicationState == STOPPED) {
					btnStartStop
							.setBackgroundResource(R.drawable.startbuttonstates);
				} else if (applicationState == RUNNING) {
					btnStartStop
							.setBackgroundResource(R.drawable.stopbuttonstates);
				}
				return true;
			}
			return false;

		}
	};

	public void startCounting() {
		String[] timerText = timeLabel.getText().toString().split(":");
		Long hrs = Long.valueOf(timerText[0]);
		Long min = Long.valueOf(timerText[1]);
		Long sec = Long.valueOf(timerText[2]);
		Long totalMSec = (hrs * 1000 * 60 * 60) + (min * 1000 * 60)
				+ (sec * 1000);
		countDownTimer = new MalibuCountDownTimer(totalMSec, 100);
		countDownTimer.start();
		applicationState = RUNNING;
	}

	public void stopCounting() {
		countDownTimer.cancel();
		applicationState = STOPPED;
	}

	public void setLabelText(String string) {
		timeLabel.setText(string);
	}

	public class MalibuCountDownTimer extends CountDownTimer

	{
		public MalibuCountDownTimer(long startTime, long interval)

		{
			super(startTime, interval);
		}

		@Override
		public void onFinish()

		{
			notif("Timer Complete");
			setLabelText("00:00:00");
			alertRingTone();

		}

		@Override
		public void onTick(long millisUntilFinished) {

			calcTime(millisUntilFinished);
		}

	}

	private void calcTime(long millisUntilFinished) {
		secs = (long) (millisUntilFinished / 1000);
		mins = (long) ((millisUntilFinished / 1000) / 60);
		hrs = (long) (((millisUntilFinished / 1000) / 60) / 60);
		hours = String.valueOf(hrs);
		if (hrs == 0) {
			hours = "00";
		}
		if (hrs < 10 && hrs > 0) {
			hours = "0" + hours;
		}
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

		timeLabel.setText(hours + ":" + minutes + ":" + seconds);
	}

	Ringtone r;

	private void alertRingTone() {
		Uri notification = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		r.play();
		btnStartStop.setBackgroundResource(R.drawable.startbuttonstates);
		showOkAlert("Timer", "Timer Complete");
	}

	private void showOkAlert(String title, String msg) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(title);
		alert.setMessage(msg);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				clearAlarm();
				return;
			}
		});
		alert.setOnKeyListener(new DialogInterface.OnKeyListener() {
		        @Override
		        public boolean onKey (DialogInterface dialog, int keyCode, KeyEvent event) {
		            if (keyCode == KeyEvent.KEYCODE_BACK && 
		                event.getAction() == KeyEvent.ACTION_UP ) {
		            	clearAlarm();
		                return false;
		            }
		            return false;
		        }
		    });
		alert.show();
	}

	private void clearAlarm(){
		if (r != null)
			r.stop();
    	if(mNotificationManager!=null)
			mNotificationManager.cancel(HELLO_ID);
    	setLabelText("00:01:00");
	}
	
	private void notif(String value) {

		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.icon;
		CharSequence tickerText = "Timer Completed";
		long when = System.currentTimeMillis();
		notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		CharSequence contentTitle = value;
		CharSequence contentText = "Timer";
		Intent notificationIntent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		mNotificationManager.notify(HELLO_ID, notification);
	};

	 

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

	Intent emailIntent;

	private void email(String toEmail, String emailText) {
		emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { toEmail });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				new String[] {});
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailText);
		TimerActivity.this.startActivity(Intent.createChooser(emailIntent,
				"Send mail..."));
	}

	private EditText ed1;
	private EditText ed2;
	private EditText ed3;

	private void showTimerPopUp() {
		AlertDialog.Builder alertadd = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final View view = factory.inflate(R.layout.numberpicerayout, null);
		ed1 = (EditText) view.findViewById(R.id.editText1);
		ed2 = (EditText) view.findViewById(R.id.editText2);
		ed3 = (EditText) view.findViewById(R.id.editText3);
		String[] timerText = timeLabel.getText().toString().split(":");
		ed1.setText(timerText[0] + "");
		ed2.setText(timerText[1] + "");
		ed3.setText(timerText[2] + "");
		alertadd.setTitle("Time Picker");
		alertadd.setView(view);
		alertadd.setPositiveButton("Set",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						int edi1 = Integer.valueOf(ed1.getText().toString());
						if (edi1 > 24)
							edi1 = 24;
						int edi2 = Integer.valueOf(ed2.getText().toString());
						if (edi2 > 59)
							edi2 = 59;
						int edi3 = Integer.valueOf(ed3.getText().toString());
						if (edi3 > 59)
							edi3 = 59;
						calcTime(Long.valueOf(edi1 * 1000 * 60 * 60)
								+ Long.valueOf(edi2 * 1000 * 60)
								+ Long.valueOf(edi3 * 1000));
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

	 
}