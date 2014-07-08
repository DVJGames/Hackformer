package game.entity;

import game.entity.component.Physics;
import game.entity.component.PlatformWalk;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Monster extends Entity implements Collidable {

	public static ConsoleObject consoleObject;
	
	private static Texture texture;
	private static Animation walkAnim;

	private PlatformWalk platformWalk;
	private boolean hurtsPlayer = true;
	
	public Monster(float x, float y) {
		super(new Rectangle(x, y, 64, 64));
		addComponent(platformWalk = new PlatformWalk(walkAnim, 3f));
	}
	
	public void update(Camera camera, float dt) {
		super.update(camera, dt);
		
		platformWalk.setCanMove((Boolean) consoleObject.fields.get(0).getSelectedValue());
		hurtsPlayer = (Boolean) consoleObject.fields.get(1).getSelectedValue();
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
			} else if (hurtsPlayer)
				e.remove();
		}
	}
	
	public static void initConsoleObject() {
		ArrayList<ConsoleField<?>> fields = new ArrayList<ConsoleField<?>>();
		
		fields.add(ConsoleField.createBooleanField("can_move", true));
		fields.add(ConsoleField.createBooleanField("hurts_player", true));
		
		consoleObject = new ConsoleObject("obj_monster", fields);
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
