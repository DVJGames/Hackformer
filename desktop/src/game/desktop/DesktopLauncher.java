package game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import game.world.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Hackformer";
		
		config.x = -1;
		config.y = -1;
		config.width = 800;
		config.height = 704;
		config.resizable = false;
		
		config.backgroundFPS = -1;
		config.foregroundFPS = 60;
		config.vSyncEnabled = true;
		
		config.useGL30 = false;
		config.allowSoftwareMode = true;
		config.samples = 4;
		
		new LwjglApplication(new Game(), config);
	}
}

