package game.entity;

import game.entity.component.Physics;
import game.entity.component.PlatformWalk;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Monster extends Entity implements Collidable {

	private static Texture texture;
	private static Animation walkAnim;

	public Monster(float x, float y) {
		super(new Rectangle(x, y, 64, 64));
		addComponent(new PlatformWalk(walkAnim, 3f));
	}

	public Rectangle getCollisionBounds() {
		return new Rectangle(bounds.x + 20, bounds.y + 10, bounds.width - 20, bounds.height - 25);
	}

	public void onCollide(Entity e) {
		if (e instanceof Player) {
			Physics physics = e.getComponent(Physics.class);
			
			if (physics.velocity.y < 0) {
				remove();
				physics.velocity.y = Player.MONSTER_HEAD_BOUNCE;
			} else
				e.remove();
		}
	}
	
	static {
		texture = new Texture("textures/monster.png");

		TextureRegion[][] splitTexture = TextureRegion.split(texture, 64, 64);

		TextureRegion[] walkFrames = new TextureRegion[5];

		for (int i = 0; i < walkFrames.length; i++)
			walkFrames[i] = splitTexture[1][i];

		walkAnim = new Animation(8f, walkFrames);
		walkAnim.setPlayMode(PlayMode.LOOP);
	}

}
