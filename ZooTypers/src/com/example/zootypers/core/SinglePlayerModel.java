package com.example.zootypers.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.io.IOUtils;

import android.content.res.AssetManager;

import com.example.zootypers.util.States;

/** 
 * 
 * The Model class for Single Player store a list of words for the UI to display.
 * It keeps track of word and letter the user has typed and updates the view accordingly.
 * 
 * @author winglam, nhlien93, dyxliang
 * 
 */

public class SinglePlayerModel extends PlayerModel {
	// keep track of the user's current score
	private int score;

	// allows files in assets to be accessed.
	private AssetManager am;

	/**
	 * Constructs a new SinglePlayerModel that takes in the ID of an animal and background,
	 * and also what the difficulty level is. The constructor will initialize the words list
	 * and fills in what words the view should display on the screen.
	 * 
	 * @param animalID, the string ID of a animal that is selected by the user
	 * @param backgroudID, the string ID of a background that is selected by the user
	 * @param diff, the difficulty level that is selected by the user
	 */
	public SinglePlayerModel(final States.difficulty diff, AssetManager am, int wordsDis) {
		super(wordsDis);
		this.am = am;
		// generates the words list according to difficulty chosen
		getWordsList(diff);

		//initialize all the fields to default starting values
		score = 0;
	}

	/*
	 * Reads different files according to the difficulty passed in,
	 * parsed the words in the chosen file into wordsList, and shuffles
	 * the words in the list.
	 * 
	 * @param diff, the difficulty level that the user has chosen
	 */
	private void getWordsList(final States.difficulty diff) {
		String file;
		if (diff == States.difficulty.EASY) {
			file = "4words.txt";
		} else if (diff == States.difficulty.MEDIUM) {
			file = "5words.txt";
		} else {
			file = "6words.txt";
		}

		// read entire file as string, parsed into array by new line
		try {
			InputStream stream = am.open(file);
			String contents = IOUtils.toString(stream, "UTF-8");
			String[] tempArr = contents.split(System.getProperty("line.separator"));
			wordsList = Arrays.asList(tempArr);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Shuffle the elements in the array
		Collections.shuffle(wordsList);
	}

	/**
	 * The typedLetter method handles what words and letter the user has
	 * typed so far and notify the view to highlight typed letter or fetch 
	 * a new word from the wordsList for the view to display accordingly.
	 * 
	 * @param letter, the letter that the user typed on the Android soft-keyboard
	 */
	public final void typedLetter(final char letter) {
		// currently not locked on to a word
		if (currWordIndex == -1) {
			for (int i = 0; i < wordsDisplayed.length; i++) {
				// if any of the first character in wordsDisplayed matched letter
				if (wordsList.get(wordsDisplayed[i]).charAt(0) == letter) {
					currWordIndex = i;
					currLetterIndex = 1;
					setChanged();
					notifyObservers(States.update.HIGHLIGHT);
					return;
				}
			}
			// locked on to a word being typed (letter == the index of current letter index in the word)
		} else if (wordsList.get(wordsDisplayed[currWordIndex]).charAt(currLetterIndex) == letter) {

			// store length of current word
			int wordLen = wordsList.get(wordsDisplayed[currWordIndex]).trim().length();

			// word is completed after final letter is typed
			if ((currLetterIndex + 1) >= wordLen) {
				score += wordLen;
				updateWordsDisplayed();
				currLetterIndex = -1;
				currWordIndex = -1;
			} else {
				currLetterIndex += 1;
				setChanged();
				notifyObservers(States.update.HIGHLIGHT);
			}
			return;
		}

		// wrong letter typed
		setChanged();
		notifyObservers(States.update.WRONG_LETTER);
	}

	/**
	 * @return current score of the player
	 */
	public final int getScore() {
		return score;
	}
}
