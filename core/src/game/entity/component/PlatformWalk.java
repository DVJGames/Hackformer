package game.entity.component;

import game.entity.Camera;
import game.world.Map;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;

public class PlatformWalk extends Component {

	private static Random random = new Random();

	private Animation walkAnim;
	private float speed;
	private boolean right;
	private boolean moved = false;

	private Render render;
	private Physics physics;

	public PlatformWalk(Animation walkAnim, float speed) {
		this.walkAnim = walkAnim;
		this.speed = speed;
		right = random.nextBoolean();
	}

	public void init(Camera camera) {
		super.init(camera);

		parent.addComponent(render = new Render(walkAnim));

		physics = parent.getComponent(Physics.class);

		if (physics == null) {
			physics = new Physics();
			parent.addComponent(physics);
		}
	}

	public void update(Camera camera, float dt) {
		super.update(camera, dt);

		if (!moved && physics.isOnGround()) {
			moved = true;
			move(1);
		}

		Rectangle collisonBounds = parent.getCollisionBounds();
		int tileX, tileY;

		tileY = (int) (collisonBounds.y / Map.TILE_SIZE) - 1;

		if (right)
			tileX = (int) ((collisonBounds.x + collisonBounds.width) / Map.TILE_SIZE) - 1;
		else
			tileX = (int) (collisonBounds.x / Map.TILE_SIZE);

		if (!parent.map.platformAt(tileX, tileY) || parent.map.darkPlatformAt(tileX, tileY)) {
			right = !right;
			move(2);
		}
	}
	
	private void move(int mult) {
		if (physics.isOnGround()) {
			if (right) {
				render.setFlip(false);
				physics.velocity.x += speed * mult;
			} else {
				render.setFlip(true);
				physics.velocity.x -= speed * mult;
			}
		}
	}

}
