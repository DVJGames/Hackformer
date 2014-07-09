package game.entity;

import game.entity.component.MouseConsole;
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
	private static Animation walkAnim, deathAnim;
	private static ArrayList<ConsoleField<?>> tempFields;

	private PlatformWalk platformWalk;
	private ArrayList<ConsoleField<?>> fields;
	private boolean hurtsPlayer = true;

	public Monster(float x, float y) {
		super(new Rectangle(x, y, 64, 64));

		tempFields = fields = initFields();

		addComponent(platformWalk = new PlatformWalk(walkAnim, 3f));
		addComponent(new MouseConsole(fields));

		initConsoleObject();
	}

	public void update(Camera camera, float dt) {
		super.update(camera, dt);

		platformWalk.setCanMove((Boolean) fields.get(0).getSelectedValue());
		hurtsPlayer = (Boolean) fields.get(1).getSelectedValue();
	}

	public Rectangle getCollisionBounds() {
		return new Rectangle(bounds.x + 20, bounds.y + 10, bounds.width - 35, bounds.height - 35);
	}

	public void onCollide(Entity e) {
		if (e instanceof Player) {
			Physics physics = e.getComponent(Physics.class);

			Rectangle cBounds = getCollisionBounds();
			
			if (physics.velocity.y < 0 && e.bounds.y > cBounds.y + cBounds.height / 2) {
				remove();
				physics.velocity.y = Player.MONSTER_HEAD_BOUNCE;
			} else if (hurtsPlayer)
				e.remove();
		}
	}

	public static void initConsoleObject() {
		consoleObject = new ConsoleObject("obj_monster", tempFields);
	}

	public void remove() {
		super.remove();
		manager.addEntity(new Death(bounds, deathAnim));
	}

	private static ArrayList<ConsoleField<?>> initFields() {
		ArrayList<ConsoleField<?>> fields = new ArrayList<ConsoleField<?>>();

		fields.add(ConsoleField.createBooleanField("can_move", true));
		fields.add(ConsoleField.createBooleanField("hurts_player", true));

		return fields;
	}

	static {
		texture = new Texture("textures/monster.png");

		TextureRegion[][] splitTexture = TextureRegion.split(texture, 64, 64);

		TextureRegion[] walkFrames = new TextureRegion[5];

		for (int i = 0; i < walkFrames.length; i++)
			walkFrames[i] = splitTexture[1][i];

		walkAnim = new Animation(8f, walkFrames);
		walkAnim.setPlayMode(PlayMode.LOOP);

		TextureRegion[] deathFrames = new TextureRegion[7];

		for (int i = 0; i < deathFrames.length; i++)
			deathFrames[i] = splitTexture[3][i];

		deathAnim = new Animation(3f, deathFrames);
	}

}
