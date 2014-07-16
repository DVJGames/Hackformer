package game.world;

import game.world.level.Level;
import game.world.level.LevelFactory;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;

public class Game extends ApplicationAdapter {

	public static final float MAX_DT = 2;
	
	private static Level level;
	private static boolean restartedLevel = false;
	
	private FPSLogger fpsLogger;
	
	public void create() {
		fpsLogger = new FPSLogger();
		setLevel(LevelFactory.getNextLevel());
	}

	public void render() {
		updateWorld(Math.min(MAX_DT, Gdx.graphics.getDeltaTime() * 60));
		
		if (restartedLevel) {
			restartedLevel = false;
			return;
		}
		
		Gdx.gl.glClearColor(77f / 255f, 77f / 255f, 77f / 255f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderWorld();
		
		fpsLogger.log();
	}

	private void updateWorld(float dt) {
		KeyHandler.update();
		
		if (KeyHandler.keyClicked(Input.Keys.R))
			level.init();
		
		if (level != null)
			level.update(dt);
	}

	private void renderWorld() {
		if (level != null)
			level.render();
	}
	
	public static void setLevel(Level level) {
		Game.level = level;
		level.init();
	}
	
	public static void restartLevel() {
		level.init();
		restartedLevel = true;
	}

}
