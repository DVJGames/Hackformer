package game.entity;

import game.entity.component.Collider;
import game.entity.component.MouseConsole;
import game.entity.component.Render;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Spike extends Entity implements Collidable {

	private static Texture texture = new Texture("textures/spikes.png");
	private static Random random = new Random();

	private ArrayList<ConsoleField<?>> fields;
	
	public Spike(float x, float y) {
		super(new Rectangle(x, y, 32, 14));
		addComponent(new Render(getSprite()));
		
		fields = new ArrayList<ConsoleField<?>>();
		fields.add(ConsoleField.createBooleanField("hurts_player", true));
		fields.add(ConsoleField.createBooleanField("hurts_monsters", false));
		addComponent(new MouseConsole(fields));
		addComponent(new Collider());
	}

	public Rectangle getCollisionBounds() {
		return new Rectangle(bounds.x + 4, bounds.y, bounds.width - 8, bounds.height - 4);
	}
	
	public void onCollide(Entity e) {
		if (e instanceof Player && (Boolean) fields.get(0).getSelectedValue())
			e.remove();
		else if (e instanceof Monster && (Boolean) fields.get(1).getSelectedValue())
			e.remove();
	}

	private static Sprite getSprite() {
		return new Sprite(texture, 32 * random.nextInt(4), 0, 32, 14);
	}

}
