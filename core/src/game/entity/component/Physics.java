package game.entity.component;

import game.entity.Camera;
import game.world.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Physics extends Component {

	private static final float DEFAULT_GRAVITY = 0.5f;
	private static final float TERMINAL_VELOCITY = 12f;
	private static final float MIN_STEP = 1;
	private static final float LADDER_NUDGE = 1;

	public Vector2 velocity = new Vector2();

	private boolean bridgeCollide = false;
	private boolean yCollide = false;
	private boolean onGround = false;
	private boolean canClimb = false;
	private boolean intersectsDarkPlatforms = true;

	private float bridgeYVelChange = 5;

	private float gravity = DEFAULT_GRAVITY;

	public void update(Camera camera, float dt) {
		super.update(camera, dt);

		boolean onLadder = !yCollide && canClimb && parent.map.onLadder(parent.getCollisionBounds());
		bridgeCollide = false;

		if (gravity != 0)
			onGround = yCollide = false;

		if (!onLadder)
			velocity.y -= gravity * Math.max(1, (1 - (-velocity.y) / TERMINAL_VELOCITY)) * dt;

		Rectangle cBounds = parent.getCollisionBounds();

		int tileX = (int) ((cBounds.x + cBounds.width / 2) / Map.TILE_SIZE);
		int tileY = (int) ((cBounds.y) / Map.TILE_SIZE);

		if (intersectsDarkPlatforms)
			if (parent.map.bridgeAt(tileX, tileY) || parent.map.upLeftBridgeAt(tileX, tileY) || parent.map.upRightBridgeAt(tileX, tileY))
				parent.bounds.y += bridgeYVelChange * dt;

		if (velocity.y < -TERMINAL_VELOCITY)
			velocity.y = -TERMINAL_VELOCITY;
		if (velocity.y > TERMINAL_VELOCITY)
			velocity.y = TERMINAL_VELOCITY;

		move(velocity.cpy().scl(dt));

		if (onLadder)
			velocity.y = 0;

		if (!bridgeCollide)
			intersectsDarkPlatforms = true;
	}

	public boolean isOnGround() {
		return onGround;
	}

	private void move(Vector2 velocity) {
		if (velocity.x != 0 && velocity.y != 0) {
			move(new Vector2(velocity.x, 0));
			move(new Vector2(0, velocity.y));
			return;
		}

		if (velocity.x > MIN_STEP) {
			move(new Vector2(MIN_STEP, 0));
			move(new Vector2(velocity.x - MIN_STEP, 0));
			return;
		}

		if (velocity.x < -MIN_STEP) {
			move(new Vector2(-MIN_STEP, 0));
			move(new Vector2(velocity.x + MIN_STEP, 0));
			return;
		}

		if (velocity.y > MIN_STEP) {
			move(new Vector2(0, MIN_STEP));
			move(new Vector2(0, velocity.y - MIN_STEP));
			return;
		}

		if (velocity.y < -MIN_STEP) {
			move(new Vector2(0, -MIN_STEP));
			move(new Vector2(0, velocity.y + MIN_STEP));
			return;
		}

		boolean solid = false;
		Rectangle collisionBounds = parent.getCollisionBounds();

		float x = collisionBounds.x + velocity.x;
		float y = collisionBounds.y + velocity.y;
		float width = collisionBounds.width;
		float height = collisionBounds.height;

		if (x + width < 0 || x >= parent.map.getWidth() * Map.TILE_SIZE)
			return;

		for (float xPos = x; xPos <= x + width; xPos += MIN_STEP) {

			int tileX = (int) (xPos / Map.TILE_SIZE);
			int tileY = (int) (y / Map.TILE_SIZE);

			if (intersectsDarkPlatforms && velocity.y < 0 && gravity > 0)
				solid |= parent.map.darkPlatformAt(tileX, tileY) && !parent.map.darkPlatformAt(tileX, (int) ((y + MIN_STEP) / Map.TILE_SIZE));

			for (float yPos = y; yPos <= y + height; yPos += MIN_STEP) {
				tileY = (int) (yPos / Map.TILE_SIZE);

				solid |= parent.map.platformAt(tileX, tileY);

				if (velocity.y < 0 && parent.map.bridgeAt(tileX, tileY)) {
					bridgeCollide = true;

					if (intersectsDarkPlatforms)
						solid = true;
				}

				if (velocity.y > 0 && gravity <= 0)
					solid |= parent.map.darkPlatformAt(tileX, tileY, true) && !parent.map.darkPlatformAt(tileX, (int) ((y + MIN_STEP) / Map.TILE_SIZE));
			}
		}

		if (!solid) {
			parent.bounds.x += velocity.x;
			parent.bounds.y += velocity.y;
		} else if (velocity.y != 0) {
			if (velocity.y < 0) {
				onGround = true;
			} else {
				if (parent.map.onLadder(collisionBounds)) {
					boolean nudgeRight = true;

					for (float i = 0; i < Map.TILE_SIZE; i += MIN_STEP) {
						collisionBounds.x = x + i;

						if (!parent.map.onLadder(collisionBounds)) {
							nudgeRight = false;
							break;
						}
					}

					if (nudgeRight)
						parent.bounds.x += LADDER_NUDGE;
					else
						parent.bounds.x -= LADDER_NUDGE;
				}
			}

			this.velocity.y = 0;
			yCollide = true;
		}
	}

	public void setCanClimb(boolean canClimb) {
		this.canClimb = canClimb;
	}

	public void setGravity(float gravity) {
		this.gravity = gravity;
	}

	public void flickerDarkPlatformIntersection() {
		intersectsDarkPlatforms = false;
	}

	public float getGravity() {
		return gravity;
	}
}
