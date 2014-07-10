package game.entity;

import game.entity.component.Render;
import game.world.Game;
import game.world.LevelFactory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Door extends Entity implements Collidable {

	private static Texture texture = new Texture("textures/door.png");
	
	public Door(float x, float y) {
		super(new Rectangle(x, y, 48, 64));
		addComponent(new Render(new Sprite(texture)));
	}

	public void onCollide(Entity e) {
		if (e instanceof Player && bounds.contains(e.getCenter()))
			Game.setLevel(LevelFactory.getNextLevel());
	}

}
