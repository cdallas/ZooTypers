package com.example.zootypers.ui;

import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.zootypers.R;
import com.example.zootypers.core.SinglePlayerModel;
import com.example.zootypers.util.States;
import com.example.zootypers.util.States.difficulty;

@SuppressLint("NewApi")
/**
 *
 * UI / Activity and controller for single player game screen.
 * @author cdallas, littlpunk, kobyran
 *
 */
public class SinglePlayer extends Player {

	// used for the communicating with model
	private SinglePlayerModel model;

	// for the popup window
	private PopupWindow ppw;
	public LayoutParams popUpParams;
	public LinearLayout popUpLayout;
	private long pausedTime = START_TIME;

	// for the game timer
	protected GameTimer gameTimer;
	public static boolean paused = false;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Get animal & background selected by user
		setContentView(R.layout.activity_pregame_selection);
		Drawable animal = ((ImageButton) findViewById
				(getIntent().getIntExtra("anm", 0))).getDrawable();
		bg = getIntent().getIntExtra("bg", 0);
		Drawable background = ((ImageButton) findViewById(bg)).getDrawable();

		// Get difficulty
		int diff = getIntent().getIntExtra("diff", 2);
		difficulty d = States.difficulty.MEDIUM;
		if (diff == 1) {
			d = States.difficulty.EASY;
		} else if (diff == 3) {
			d = States.difficulty.HARD;
		}

		// start model
		model = new SinglePlayerModel(d, this.getAssets(), NUM_WORDS);
		model.addObserver(this);

		// change screen view
		setContentView(R.layout.activity_single_player);
		initialDisplay(animal, background);

		// create and start timer
		gameTimer = new GameTimer(START_TIME, INTERVAL);
		gameTimer.start();

	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.single_player, menu);
		return true;
	}

	@Override
	/**
	 * When the user types a letter, this listens for it.
	 */
	public final boolean onKeyDown(final int key, final KeyEvent event){
		if (key == KeyEvent.KEYCODE_BACK && !paused) {
			pauseGame(findViewById(R.id.pause_button));
			return true;
		}

		char charTyped = event.getDisplayLabel();
		charTyped = Character.toLowerCase(charTyped);
		model.typedLetter(charTyped);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!paused)
			pauseGame(findViewById(R.id.pause_button));
	}

	/**
	 * Displays the initial screen of the single player game.
	 * @param animalID Drawable referring to the id of the selected animal image,
	 * e.g. R.drawable.elephant_color.
	 * @param backgroudID Drawable referring to the id of the selected background image.
	 * @param words An array of the words to display. Must have a length of 5.
	 */
	public void initialDisplay(Drawable animalID, Drawable backgroundID) {
		// display animal
		ImageView animalImage = (ImageView) findViewById(R.id.animal_image);
		animalImage.setImageDrawable(animalID);

		// display background
		ViewGroup layout = (ViewGroup) findViewById(R.id.game_layout);
		layout.setBackground(backgroundID);

		model.populateDisplayedList();

		displayTime(START_TIME / INTERVAL);

		displayScore(0);
	}

	/**
	 * Called when the timer runs out; goes to the post game screen.
	 */
	public void goToPostGame() {
		Intent intent = new Intent(this, PostGameScreen.class);
		// pass score
		intent.putExtra("score", model.getScore());
		intent.putExtra("bg", bg);
		startActivity(intent);
	}

	/**
	 * When the pause button is pressed, pauses the game and shows a pop-up window.
	 * @param view The button clicked.
	 */
	public void pauseGame(View view) {
		// save & stop time
		pausedTime = currentTime;
		gameTimer.cancel();

		// disable buttons & keyboard
		findViewById(R.id.keyboard_open_button).setEnabled(false);
		findViewById(R.id.pause_button).setEnabled(false);

		// create popup window
		LayoutInflater layoutInflater =
				(LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.pause_popup, null);
		ppw = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ViewGroup parentLayout = (ViewGroup) findViewById(R.id.game_layout);
		ppw.showAtLocation(parentLayout, Gravity.CENTER, 10, 20);
		ppw.update(350, 500);

		paused = true;
	}

	/**
	 * When the user clicks the continue button while paused, continues the game.
	 * @param view The button clicked.
	 */
	public void pausedContinue(View view){
		// re-enable buttons & keyboard
		findViewById(R.id.keyboard_open_button).setEnabled(true);
		findViewById(R.id.pause_button).setEnabled(true);
		// keyboardButton(findViewById(R.id.keyboard_open_button));

		gameTimer = new GameTimer(pausedTime, INTERVAL);
		gameTimer.start();
		ppw.dismiss();
		paused = false;
	}

	/**
	 * When the user clicks the new game button while paused, starts a new game.
	 * @param view The button clicked.
	 */
	public void pausedNewGame(View view) {
		final Intent restartIntent = new Intent(this, PreGameSelection.class);
		paused = false;
		startActivity(restartIntent);
	}

	/**
	 * When the user clicks the main menu button while paused, goes to the title screen.
	 * @param view The button clicked.
	 */
	public void pausedMainMenu(View view) {
		final Intent mainMenuIntent = new Intent(this, TitlePage.class);
		paused = false;
		startActivity(mainMenuIntent);
	}

	/**
	 * Timer for game.
	 * @author ZooTypers
	 */
	public class GameTimer extends CountDownTimer {
		/**
		 * @param startTime Amount of time player starts with.
		 * @param interval Amount of time between ticks.
		 */
		public GameTimer(final long startTime, final long interval) {
			super(startTime, interval);
		}

		@Override
		public final void onFinish() {
			// TODO add game over message before going to post game
			goToPostGame();
		}

		@Override
		public final void onTick(final long millisUntilFinished) {
			currentTime = millisUntilFinished;
			displayTime(TimeUnit.MILLISECONDS.toSeconds(currentTime));
		}
	}

	@Override
	public void error(com.example.zootypers.util.States.error err) {
		// TODO Auto-generated method stub
	}
}