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
	private int justChanged = 0;

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
			move();
		}
		
		Rectangle collisonBounds = parent.getCollisionBounds();
		int tileX, tileY;

		tileY = (int) ((collisonBounds.y - speed) / Map.TILE_SIZE);

		if (right)
			tileX = (int) ((collisonBounds.x + speed * Game.MAX_DT) / Map.TILE_SIZE);
		else
			tileX = (int) ((collisonBounds.x  - speed * Game.MAX_DT) / Map.TILE_SIZE);

		if (tileX < 0) {
			right = false;
			tileX = 0;
		}
		
		if (justChanged >= 2)
			right = false;
		
		if (((collisonBounds.x < speed && !right) || (collisonBounds.x + collisonBounds.width / 2 + speed >= parent.map.getWidth() * Map.TILE_SIZE )
				|| (!parent.map.topPlatformAt(tileX, tileY) && !parent.map.darkPlatformAt(tileX, tileY) && !parent.map.bridgeTilesAt(tileX, tileY) && !parent.map.bridgeTilesAt(tileX + 1, tileY + 1)))) {
			right = !right;
			justChanged++;
			move();
		} else {
			justChanged = 0;
		}
	}
	
//	public void render(Camera camera, SpriteBatch batch) {
//		super.render(camera, batch);
//		
//		Rectangle collisonBounds = parent.getCollisionBounds();
//		int tileX, tileY;
//
//		tileY = (int) ((collisonBounds.y - speed) / Map.TILE_SIZE);
//
//		if (right)
//			tileX = (int) ((collisonBounds.x + speed) / Map.TILE_SIZE);
//		else
//			tileX = (int) ((collisonBounds.x  - speed) / Map.TILE_SIZE);
//	
//		ShapeRenderer sr = new ShapeRenderer();
//		sr.begin(ShapeType.Filled);
//		sr.setColor(1, 0, 0, 1);
//		sr.rect(tileX * Map.TILE_SIZE - camera.bounds.x + camera.bounds.width / 2, tileY * Map.TILE_SIZE, Map.TILE_SIZE, Map.TILE_SIZE);
//		sr.end();
//	}

	private void move() {
		if (physics.isOnGround()) {
			if (right) {
				render.setFlip(false);
				physics.velocity.x = speed;
			} else {
				render.setFlip(true);
				physics.velocity.x = -speed;
			}
		}
	}

	public void setCanMove(boolean canMove) {
		if (!canMove) {
			if (moved) 
				physics.velocity.x = 0;
			moved = false;
		}
		
		this.canMove = canMove;
	}

}
