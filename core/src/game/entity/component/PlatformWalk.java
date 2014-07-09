package game.entity.component;

import game.entity.Camera;
import game.world.Game;
import game.world.Map;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;

public class PlatformWalk extends Component {

	private static Random random = new Random();

	private Animation walkAnim;
	private float speed;
	private boolean right;
	private boolean moved = false, canMove = true;

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

		if (!canMove) {
			render.resetAnimation();
			return;
		}
		
		if (!moved && physics.isOnGround()) {
			moved = true;
			move(1);
		}
		
		Rectangle collisonBounds = parent.getCollisionBounds();
		int tileX, tileY;

		tileY = (int) (collisonBounds.y / Map.TILE_SIZE) - 1;

		if (right)
			tileX = (int) ((collisonBounds.x + collisonBounds.width / 2 + speed * Game.MAX_DT) / Map.TILE_SIZE);
		else
			tileX = (int) ((collisonBounds.x - speed * Game.MAX_DT) / Map.TILE_SIZE);

		if (collisonBounds.x < speed * Game.MAX_DT || collisonBounds.x + collisonBounds.width / 2 + speed * Game.MAX_DT >= parent.map.getWidth() * Map.TILE_SIZE
				|| (!parent.map.topPlatformAt(tileX, tileY) && !parent.map.darkPlatformAt(tileX, tileY) && !parent.map.bridgeTilesAt(tileX, tileY) && !parent.map.bridgeTilesAt(tileX - 1, tileY + 1))) {
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

	public void setCanMove(boolean canMove) {
		if (!canMove) {
			if (moved) 
				move(-1);
			moved = false;
		}
		
		this.canMove = canMove;
	}

}
