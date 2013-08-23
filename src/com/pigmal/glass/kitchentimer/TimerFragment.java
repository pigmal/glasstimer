package com.pigmal.glass.kitchentimer;

import android.app.TimePickerDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimerFragment extends Fragment {
	static final String TAG = "Glass";
	private TextView mMinutesText;
	private TextView mSecondsText;
	private Button mStartButton;

	private Context mContext;
	private SoundPool mSoundPool;
	private int mSoundId;
	private boolean mIsCounting;
	private CountDownTimer mCountDownTimer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_timer, container, false);

		mContext = this.getActivity();
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

		mSoundId = mSoundPool.load(mContext, R.raw.alarm, 1);

		mMinutesText = (TextView) view.findViewById(R.id.text_minutes);
		mSecondsText = (TextView) view.findViewById(R.id.text_seconds);

		mStartButton = (Button) view.findViewById(R.id.button_start);
		mStartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mIsCounting) {
					stopCountDown();
				} else {
					new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
								@Override
								public void onTimeSet(TimePicker view, int minutes, int seconds) {
									startCountDown(minutes, seconds);
								}
							}, 0, 0, true)
					.show();
				}
			}
		});

		return view;
	}

	void stopCountDown() {
		mCountDownTimer.cancel();
		mStartButton.setText("Start");
		mIsCounting = false;
	}

	void startCountDown(int minutes, int seconds) {
		Log.v(TAG, "Start countdown timer");
		mMinutesText.setText(String.valueOf(minutes));
		mSecondsText.setText(String.valueOf(seconds));

		mIsCounting = true;
		mStartButton.setText("Stop");
		mCountDownTimer = new CountDownTimer((minutes * 60 + seconds) * 1000,
				1000) {
			public void onTick(long millisUntilFinished) {
				Log.v(TAG, "tick");
				long seconds = millisUntilFinished / 1000;
				int min = (int) (seconds / 60);
				int sec = (int) (seconds - min * 60);
				mMinutesText.setText(String.valueOf(min));
				mSecondsText.setText(String.valueOf(sec));
			}

			public void onFinish() {
				Log.v(TAG, "count down finished");
				mIsCounting = false;
				mStartButton.setText("Start");
				mSecondsText.setText(String.valueOf(0));
				Toast.makeText(mContext, "Finished", Toast.LENGTH_SHORT).show();
				mSoundPool.play(mSoundId, 1.0f, 1.0f, 1, 0, 1f);
			}
		};
		mCountDownTimer.start();
	}
}
