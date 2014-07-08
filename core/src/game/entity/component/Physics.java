package game.entity.component;

import game.entity.Camera;
import game.world.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Physics extends Component {

	private static final float DEFAULT_GRAVITY = 0.5f;
	private static final float TERMINAL_VELOCITY = 12f;
	private static final float MIN_STEP = 1;

	public Vector2 velocity = new Vector2();

	private boolean onGround = false;
	private boolean canClimb = false;
	private boolean intersectsDarkPlatforms = true;

	private float gravity = DEFAULT_GRAVITY;

	public void update(Camera camera, float dt) {
		super.update(camera, dt);

		if (gravity != 0)
			onGround = false;

		boolean onLadder = canClimb && parent.map.onLadder(parent.getCollisionBounds());

		if (!onLadder)
			velocity.y -= gravity * Math.max(1, (1 - (-velocity.y) / TERMINAL_VELOCITY)) * dt;

		if (velocity.y < -TERMINAL_VELOCITY)
			velocity.y = -TERMINAL_VELOCITY;
		if (velocity.y > TERMINAL_VELOCITY)
			velocity.y = TERMINAL_VELOCITY;

		move(velocity.cpy(), dt);

		if (onLadder)
			velocity.y = 0;
		
		intersectsDarkPlatforms = true;
	}

	public boolean isOnGround() {
		return onGround;
	}

	private void move(Vector2 velocity, float dt) {
		if (velocity.x != 0 && velocity.y != 0) {
			move(new Vector2(velocity.x, 0), dt);
			move(new Vector2(0, velocity.y), dt);
			return;
		}

		if (velocity.x > MIN_STEP) {
			move(new Vector2(MIN_STEP, 0), dt);
			move(new Vector2(velocity.x - MIN_STEP, 0), dt);
			return;
		}

		if (velocity.x < -MIN_STEP) {
			move(new Vector2(-MIN_STEP, 0), dt);
			move(new Vector2(velocity.x + MIN_STEP, 0), dt);
			return;
		}

		if (velocity.y > MIN_STEP) {
			move(new Vector2(0, MIN_STEP), dt);
			move(new Vector2(0, velocity.y - MIN_STEP), dt);
			return;
		}

		if (velocity.y < -MIN_STEP) {
			move(new Vector2(0, -MIN_STEP), dt);
			move(new Vector2(0, velocity.y + MIN_STEP), dt);
			return;
		}

		velocity.scl(dt);

		boolean solid = false;
		Rectangle collisionBounds = parent.getCollisionBounds();

		float x = collisionBounds.x + velocity.x;
		float y = collisionBounds.y + velocity.y;
		float width = collisionBounds.width;
		float height = collisionBounds.height;

		Outer: for (float xPos = x; xPos <= x + width; xPos += MIN_STEP) {

			if (velocity.y < 0 && intersectsDarkPlatforms) 
				solid |= parent.map.darkPlatformAt((int) (xPos / Map.TILE_SIZE), (int) (y / Map.TILE_SIZE)) && !parent.map.darkPlatformAt((int) (xPos / Map.TILE_SIZE), (int) ((y + MIN_STEP) / Map.TILE_SIZE));

			for (float yPos = y; yPos <= y + height; yPos += MIN_STEP) {
				if (solid)
					break Outer;

				solid |= parent.map.platformAt((int) (xPos / Map.TILE_SIZE), (int) (yPos / Map.TILE_SIZE));
			}
		}

		if (!solid) {
			parent.bounds.x += velocity.x;
			parent.bounds.y += velocity.y;
		} else if (velocity.y != 0) {
			this.velocity.y = 0;
			onGround = true;
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
}
