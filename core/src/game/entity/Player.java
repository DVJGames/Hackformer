package game.entity;

import java.util.ArrayList;

import game.entity.component.Collider;
import game.entity.component.Physics;
import game.entity.component.Render;
import game.world.KeyHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Player extends Entity {

	public static ConsoleObject consoleObject;

	private static final Texture texture = new Texture("textures/ninja.png");
	private static Animation walkAnim, climbAnim;
	private static Sprite standSprite, jumpSprite;

	private static final float JUMP_SPEED = 12;
	public static final float MONSTER_HEAD_BOUNCE = 12;

	private Render render;
	private Physics physics;
	
	private boolean left = false, right = false, canJump = true, jumpEnabled = true;
	private float moveSpeed;
	private float climbSpeed;

	public Player(float x, float y) {
		super(new Rectangle(x, y, 30, 46.2f));

		addComponent(render = new Render(climbAnim));
		addComponent(physics = new Physics());
		addComponent(new Collider());

		physics.setCanClimb(true);
	}

	public void update(Camera camera, float dt) {
		super.update(camera, dt);

		camera.centerAt(bounds.x, bounds.y);
		render.setSprite(standSprite);

		moveHorizontally();
		moveVertically();
		changeBasedOnConsoleVariables();
	}

	private void moveHorizontally() {
		float velX = 0;

		if (left)
			velX += moveSpeed;

		if (right)
			velX -= moveSpeed;

		left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
		right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);

		if (left) {
			velX -= moveSpeed;
			render.setFlip(true);
		}

		if (right) {
			velX += moveSpeed;
			render.setFlip(false);
		}

		if (left || right)
			render.setAnimation(walkAnim);

		physics.velocity.x += velX;
	}

	private void moveVertically() {
		canJump = jumpEnabled && physics.isOnGround();

		if (!physics.isOnGround())
			render.setSprite(jumpSprite);

		boolean climbing = false;

		if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
			if (map.onLadder(getCollisionBounds())) {
				bounds.y += climbSpeed;

				if (map.onLadder(getCollisionBounds())) {
					physics.velocity.y += climbSpeed;
					bounds.y -= climbSpeed;
					climbing = !climbing;
				} else {
					canJump = true;
				}
			}

			if (canJump)
				physics.velocity.y += JUMP_SPEED;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
			if (map.onLadder(getCollisionBounds())) {
				physics.velocity.y -= climbSpeed;
				climbing = !climbing;
			} else if (KeyHandler.keyClicked(Input.Keys.DOWN) || KeyHandler.keyClicked(Input.Keys.S)){
				physics.flickerDarkPlatformIntersection();
			}
		}

		if (map.onLadder(getCollisionBounds())) {
			render.setAnimation(climbAnim);
			if (!climbing)
				render.resetAnimation();
		}
	}
	
	private void changeBasedOnConsoleVariables() {
		physics.setGravity((Float)consoleObject.fields.get(0).getSelectedValue());
		moveSpeed = (Float) consoleObject.fields.get(1).getSelectedValue();
		climbSpeed = (Float) consoleObject.fields.get(2).getSelectedValue();
		jumpEnabled = (Boolean) consoleObject.fields.get(3).getSelectedValue();
	}

	public Rectangle getCollisionBounds() {
		return new Rectangle(bounds.x + 8, bounds.y, bounds.width - 8, bounds.height - 7);
	}

	public static void initConsoleObject() {
		ArrayList<ConsoleField<?>> fields = new ArrayList<ConsoleField<?>>();

		ArrayList<Float> gravityOptions = new ArrayList<Float>();
		gravityOptions.add(-1f);
		gravityOptions.add(-0.5f);
		gravityOptions.add(0f);
		gravityOptions.add(0.5f);
		gravityOptions.add(1f);

		fields.add(new ConsoleField<Float>("gravity", gravityOptions, 3));
		
		ArrayList<Float> speedOptions = new ArrayList<Float>();
		speedOptions.add(1f);
		speedOptions.add(3f);
		speedOptions.add(5f);
		speedOptions.add(7f);
		speedOptions.add(9f);
		
		fields.add(new ConsoleField<Float>("move_speed", speedOptions, 2));
		fields.add(new ConsoleField<Float>("climb_speed", speedOptions, 2));
		
		fields.add(ConsoleField.createBooleanField("can_jump", true));
		
		for (int i = 0; i < 5; i++)
			fields.add(ConsoleField.createBooleanField("unused field", true));
		
		consoleObject = new ConsoleObject("obj_player", fields);
	}

	static {
		standSprite = new Sprite(texture, 50 * 8, 77 * 2, 50, 77);

		TextureRegion[][] splitTexture = TextureRegion.split(texture, 50, 77);

		TextureRegion[] walkFrames = new TextureRegion[8];

		for (int i = 0; i < walkFrames.length; i++)
			walkFrames[i] = splitTexture[0][i];

		walkAnim = new Animation(6f, walkFrames);
		walkAnim.setPlayMode(PlayMode.LOOP);

		TextureRegion[] climbFrames = new TextureRegion[4];

		for (int i = 0; i < climbFrames.length; i++)
			climbFrames[i] = splitTexture[2][i];

		climbAnim = new Animation(10f, climbFrames);
		climbAnim.setPlayMode(PlayMode.LOOP);

		jumpSprite = new Sprite(texture, 100, 77, 50, 77);
	}

}
