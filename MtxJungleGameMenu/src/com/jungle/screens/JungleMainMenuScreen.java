package com.jungle.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.jungle.assets.Assets;
import com.jungle.buttons.JungleGameButton;
import com.jungle.screens.helpers.JungleMainMenuScreenButtons;
import com.jungle.screens.helpers.JungleMainMenuScreenEnvironment;
import com.jungle.screens.helpers.JungleMainMenuScreenInstructions;
import com.moribitotech.mtx.AbstractScreen;
import com.moribitotech.mtx.IScreen;
import com.moribitotech.mtx.InputIntent;
import com.moribitotech.mtx.InputIntent.DirectionIntent;
import com.moribitotech.mtx.effects.EffectCreator;
import com.moribitotech.mtx.models.base.EmptyAbstractActorLight;
import com.moribitotech.mtx.models.base.SmartModel;
import com.moribitotech.mtx.models.base.TableModel;
import com.moribitotech.mtx.settings.AppSettings;

public class JungleMainMenuScreen extends AbstractScreen implements IScreen {

	// Splash
	public EmptyAbstractActorLight splashLoading;

	// Main Menu Screen elements
	public EmptyAbstractActorLight gameName;
	public EmptyAbstractActorLight mountains;
	public ArrayList<SmartModel> backgroundBalloons;
	public ArrayList<SmartModel> flowers;
	public EmptyAbstractActorLight instructions;
	public JungleGameButton btnPlay;
	public JungleGameButton btnScores;
	public JungleGameButton btnSettings;
	public JungleGameButton btnSocialFacebook;
	public JungleGameButton btnSocialTwitter;
	public JungleGameButton btnSocialGoogle;
	public JungleGameButton btnSwipeForMenu;
	public JungleGameButton btnSwipeForInstructions;
	public TableModel menuTable;

	// Splash, Menu, Instruction activeness management
	private boolean isSplashCompleted;
	private boolean isMenuActive;

	// Main Menu Screen element managers
	// These creates and animates the above screen elements
	JungleMainMenuScreenButtons jungleMainMenuScreenButtons;
	JungleMainMenuScreenEnvironment jungleMainMenuScreenEnvironment;
	JungleMainMenuScreenInstructions jungleMainMenuScreenInstructions;

	// Main Menu Screen helpers
	private final float SPLASH_TIME = 6.0f;
	private final float GAME_NAME_LOOP_ANIMATION_TIME = 3.0f;
	private float gameNameAnimationTimer;

	// Swipe controls
	InputIntent inputIntent;
	float touchDragInterval;

	public JungleMainMenuScreen(Game game, String screenName) {
		super(game, screenName);
		//
		setUpScreenElements();
		setUpInfoPanel();
		setUpMenu();
		setUpSwipeListener();
	}

	@Override
	public void setUpScreenElements() {
		//
		// Reset system
		// #################################################################
		setSecondsTime(0);
		isSplashCompleted = false;
		isMenuActive = false;

		//
		// Set background image
		// #################################################################
		setBackgroundTexture(Assets.img_bg_1_);

		//
		// Set game name animation timer
		// #################################################################
		gameNameAnimationTimer = SPLASH_TIME + GAME_NAME_LOOP_ANIMATION_TIME;

		//
		// InputIntent for swipes/drags
		// #################################################################
		inputIntent = new InputIntent();
		touchDragInterval = AppSettings.SCREEN_H / 3.0f;
		inputIntent.setTouchDragIntervalRange(touchDragInterval);

		//
		// Construct Main Menu Screen element managers
		// #################################################################
		jungleMainMenuScreenButtons = new JungleMainMenuScreenButtons();
		jungleMainMenuScreenEnvironment = new JungleMainMenuScreenEnvironment();
		jungleMainMenuScreenInstructions = new JungleMainMenuScreenInstructions();

		//
		// Prepare splash
		// #################################################################
		splashLoading = new EmptyAbstractActorLight(100f, 100f, true);
		splashLoading.setTextureRegion(Assets.img_obj_loading, true);
		splashLoading.setOrigin(splashLoading.getWidth() / 2.0f,
				splashLoading.getHeight() / 2.0f);
		splashLoading.setPosition(
				AppSettings.SCREEN_W - splashLoading.getWidth(),
				AppSettings.SCREEN_H - splashLoading.getHeight());
		//
		getStage().addActor(splashLoading);
	}

	@Override
	public void setUpInfoPanel() {
		//
	}

	@Override
	public void setUpMenu() {
		//
		// Main menu (Order is important)
		// #################################################################
		//
		// They are invisible or scaled to 0f until splash is completed
		// We will send the elements after splash is completed
		// Check the render() method
		//
		jungleMainMenuScreenEnvironment.setUpBackgroundBalloons(this);
		jungleMainMenuScreenEnvironment.setUpMounatins(this);
		jungleMainMenuScreenEnvironment.setUpFlowers(this);
		jungleMainMenuScreenEnvironment.setUpGameName(this);
		//
		jungleMainMenuScreenButtons.setUpMainMenuButtons(this);
		jungleMainMenuScreenButtons.setUpSocialButtons(this);
		jungleMainMenuScreenButtons.setUpSwipeButtons(this);
		//
		jungleMainMenuScreenInstructions.setUpInstructions(this);
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		//
		// Splash
		// #################################################################
		//
		// When splash is completed, send main menu elements (Name, Buttons,
		// etc...), I faked the splash/loading here. Normally use assetmanager
		// and when its really completed the asset loading, send game elements
		//
		if (!isSplashCompleted) {
			if (getSecondsTime() > SPLASH_TIME) {
				jungleMainMenuScreenButtons.sendInMainMenuButtons(this);
				jungleMainMenuScreenButtons.sendInSocialButtons(this);
				jungleMainMenuScreenButtons.sendInSwipeForInstruction(this);
				//
				jungleMainMenuScreenEnvironment.sendInGameName(this);
				//
				isSplashCompleted = true;
				isMenuActive = true;
			}

			if (getSecondsTime() > SPLASH_TIME - 1.0f) {
				splashLoading.addAction(Actions.rotateBy(-5f));
				EffectCreator.create_FO(splashLoading, 0.7f, null, false);
			} else {
				splashLoading.addAction(Actions.rotateBy(-5f));
			}
		}

		//
		// Game name animation
		// #################################################################
		//
		// Every some specified seconds animate the game name
		// Shake effect
		//
		if (getSecondsTime() > gameNameAnimationTimer) {
			if (gameName != null) {

				// Create Scale>Shake>BackToNormal effect
				EffectCreator.create_SC_SHK_BTN(gameName, 1.1f, 1.1f, 8f, 0,
						0.11f, null, false);

				// Renew timer for next effect
				gameNameAnimationTimer = getSecondsTime()
						+ GAME_NAME_LOOP_ANIMATION_TIME;
			}
		}
	}

	private void setUpSwipeListener() {
		getStage().addListener(new ActorGestureListener() {
			// Touch Down an actor
			@Override
			public void touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				super.touchDown(event, x, y, pointer, button);

				//
				// Set touch down initials for input intent
				// ########################################
				inputIntent.setTouchInitials(x, y);

			}

			// Pan/Drag an actor
			@Override
			public void pan(InputEvent event, float x, float y, float deltaX,
					float deltaY) {
				super.pan(event, x, y, deltaX, deltaY);

				//
				// Set touch currents
				// ########################################
				inputIntent.setTouchCurrents(x, y);

				//
				// Swipe/Drag detection
				// ########################################
				if (inputIntent.getDirectionIntent() == DirectionIntent.TOUCH_D_UP) {

					// if swipe up confirmed, send menu away and get
					// instructions
					if (isMenuActive & isSplashCompleted) {
						if (inputIntent.isTouchDragInterval()) {
							//
							// Reset all actor actions
							resetMenuElementsActions();
							//
							jungleMainMenuScreenEnvironment
									.sendAwayGameName(JungleMainMenuScreen.this);
							jungleMainMenuScreenButtons
									.sendAwaySocialButtons(JungleMainMenuScreen.this);
							jungleMainMenuScreenButtons
									.sendAwayMainMenuButtons(JungleMainMenuScreen.this);
							jungleMainMenuScreenButtons
									.sendInSwipeForMenu(JungleMainMenuScreen.this);
							jungleMainMenuScreenButtons
									.sendAwaySwipeForInstructions(JungleMainMenuScreen.this);
							jungleMainMenuScreenInstructions
									.sendInInstructions(JungleMainMenuScreen.this);
							//
							isMenuActive = false;
						}
					}
				}

				else if (inputIntent.getDirectionIntent() == DirectionIntent.TOUCH_D_DOWN) {

					// if swipe down confirmed, send instructions away, get menu
					if (!isMenuActive && isSplashCompleted) {
						if (inputIntent.isTouchDragInterval()) {
							//
							// Reset all actor actions
							resetMenuElementsActions();
							//
							jungleMainMenuScreenEnvironment
									.sendInGameName(JungleMainMenuScreen.this);
							jungleMainMenuScreenButtons
									.sendInSocialButtons(JungleMainMenuScreen.this);
							jungleMainMenuScreenButtons
									.sendInMainMenuButtons(JungleMainMenuScreen.this);
							jungleMainMenuScreenButtons
									.sendAwaySwipeForMenu(JungleMainMenuScreen.this);
							jungleMainMenuScreenButtons
									.sendInSwipeForInstruction(JungleMainMenuScreen.this);
							jungleMainMenuScreenInstructions
									.sendAwayInstructions(JungleMainMenuScreen.this);

							//
							isMenuActive = true;
						}
					}
				}
			}

			// Touch Up an actor
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);

				//
				// Reset after touch up
				// ########################################
				inputIntent.reset();
			}
		});
	}

	private void resetMenuElementsActions() {
		gameName.clearActions();
		btnPlay.clearActions();
		btnScores.clearActions();
		btnSettings.clearActions();
		btnSocialFacebook.clearActions();
		btnSocialTwitter.clearActions();
		btnSocialGoogle.clearActions();
		btnSwipeForMenu.clearActions();
		btnSwipeForInstructions.clearActions();
	}
}
