package com.example.zootypers.ui;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zootypers.R;
import com.example.zootypers.core.ScoreEntry;

/**
 * Represents a Multiplayer Tab in the Leaderboard
 * @author ZooTypers
 *
 */
public class MultiplayerTab extends LeaderboardTab {
	
	/**
	 * creates a view for the fragment using the multiplayer_tab layout
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		// set the layout for the fragment and get the arguments for that are passed
		View multiplayerView = inflater.inflate(R.layout.multiplayer_tab, container, false);
		ScoreEntry[] seArray = (ScoreEntry[]) getArguments().getParcelableArray("scoreList");
		// set up the leaderboard
		setupLBList(multiplayerView, seArray);
		return multiplayerView;
	}
	
	
	/**
	 * Create a new instance of MultiplayerTab with the scores as a param
	 * @param seArray an array of scoreEntrys that have the score of each player
	 * @return
	 */
	public static MultiplayerTab newInstance(ScoreEntry[] seArray) {
		MultiplayerTab mpt = new MultiplayerTab();
		Bundle args = new Bundle();
		args.putParcelableArray("scoreList", seArray);
		mpt.setArguments(args);
		return mpt;
	}

	
}