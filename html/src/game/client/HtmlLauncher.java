package game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import game.world.Game;

public class HtmlLauncher extends GwtApplication {

        public GwtApplicationConfiguration getConfig () {
        		GwtApplicationConfiguration config = new GwtApplicationConfiguration(800, 704);
        	
        		config.antialiasing = true;
        		config.fps = 60;
        		config.stencil = false;
        		
                return config;
        }

        public ApplicationListener getApplicationListener () {
                return new Game();
        }
}