package game.entity;

import game.entity.component.Render;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Spike extends Entity implements Collidable {

	private static Texture texture = new Texture("textures/spikes.png");
	private static Random random = new Random();
	
	public Spike(float x, float y) {
		super(new Rectangle(x, y, 32, 14));
		addComponent(new Render(getSprite()));
	}

	public Rectangle getCollisionBounds() {
		return new Rectangle(bounds.x + 4, bounds.y, bounds.width - 8, bounds.height - 4);
	}
	
	public void onCollide(Entity e) {
		if (e instanceof Player)
			e.remove();
	}

	private static Sprite getSprite() {
		return new Sprite(texture, 32 * random.nextInt(4), 0, 32, 14);
	}

}
