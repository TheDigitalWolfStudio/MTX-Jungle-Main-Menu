package com.jungle.main;

import com.badlogic.gdx.Game;
import com.jungle.assets.Assets;
import com.jungle.screens.JungleMainMenuScreen;
import com.moribitotech.mtx.settings.AppSettings;

public class MainStarter extends Game {

	@Override
	public void create() {
		//
		// Set up the application
		// #####################################
		AppSettings.setUp();

		//
		// Load assets before setting the screen
		// #####################################
		Assets.loadAll();

		//
		// Set the tests screen
		// #####################################
		setScreen(new JungleMainMenuScreen(this, "Main Menu"));
	}
}
