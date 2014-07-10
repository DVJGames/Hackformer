package game.entity;

import game.entity.component.Collider;
import game.entity.component.MouseConsole;
import game.entity.component.Physics;
import game.entity.component.Render;
import game.world.KeyHandler;

import java.util.ArrayList;

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
	private static ArrayList<ConsoleField<?>> tempFields;

	private static final Texture texture = new Texture("textures/ninja.png");
	private static Animation walkAnim, climbAnim;
	private static Sprite standSprite, jumpSprite;

	private static final float JUMP_SPEED = 12;
	public static final float MONSTER_HEAD_BOUNCE = 12;

	private Render render;
	private Physics physics;
	private ArrayList<ConsoleField<?>> fields;

	private boolean[] wasOnGround = new boolean[10];
	private boolean left = false, right = false, canJump = true;
	private float moveSpeed;
	private float climbSpeed;
	private float moveVelocity = 0;
	private float moveAccelerationFraction = 40f;
	private float initialMoveSpeed = 3f;
	private float friction = 0.8f;

	private int jumpCount = 0, maxJumps = 1;

	public Player(float x, float y) {
		super(new Rectangle(x, y, 30, 46.2f));

		tempFields = fields = initFields();

		addComponent(render = new Render(climbAnim));
		addComponent(physics = new Physics());
		addComponent(new Collider());
		addComponent(new MouseConsole(fields));

		physics.setCanClimb(true);

		initConsoleObject();
	}

	public void update(Camera camera, float dt) {
		super.update(camera, dt);

		camera.centerAt(bounds.x, bounds.y);

		if (!(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)))
			render.setSprite(standSprite);

		checkOnGround();
		moveHorizontally();
		moveVertically();
		changeBasedOnConsoleVariables();
	}

	private void checkOnGround() {
		wasOnGround[wasOnGround.length - 1] = physics.isOnGround();

		for (int i = 0; i < wasOnGround.length - 1; i++)
			wasOnGround[i] = wasOnGround[i + 1];
	}

	private void moveHorizontally() {
		float velX = 0;

		if (!left && !right) {
			physics.velocity.x -= moveVelocity;
			moveVelocity = 0;
		}

		left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
		right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);

		if (left && moveVelocity > -moveSpeed) {
			move(-moveSpeed / moveAccelerationFraction);
			render.setFlip(true);
		}

		if (right && moveVelocity < moveSpeed) {
			move(moveSpeed / moveAccelerationFraction);
			render.setFlip(false);
		}

		if (left || right)
			if (!Gdx.input.isKeyPressed(Input.Keys.W) && !Gdx.input.isKeyPressed(Input.Keys.UP))
				render.setAnimation(walkAnim);

		physics.velocity.x += velX;
	}

	private void move(float amt) {
		if (moveVelocity == 0) {
			if (amt < 0)
				amt = -initialMoveSpeed;
			else
				amt = initialMoveSpeed;
		}
		
		if (amt > 0 && moveVelocity < 0)
			amt += Math.min(friction, -moveVelocity);
		else if (amt < 0 && moveVelocity > 0)
			amt -= Math.min(friction, moveVelocity);

		moveVelocity += amt;
		physics.velocity.x += amt;
	}

	private void moveVertically() {
		canJump = jumpCount < maxJumps && (jumpCount >= 1 || wasOnGroundRecently());

		if (!physics.isOnGround()) {
			render.setSprite(jumpSprite);
		} else {
			jumpCount = 0;
		}

		boolean climbing = false, wasClimbing = false;

		if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
			if (map.onLadder(getCollisionBounds())) {
				bounds.y += climbSpeed;

				if (map.onLadder(getCollisionBounds())) {
					physics.velocity.y += climbSpeed;
					bounds.y -= climbSpeed;
					climbing = !climbing;
				} else {
					wasClimbing = true;
					canJump = true;
				}
			}

			if (canJump && (wasClimbing || physics.isOnGround() || (KeyHandler.keyClicked(Input.Keys.UP) || KeyHandler.keyClicked(Input.Keys.W)))) {
				physics.velocity.y += JUMP_SPEED;
				jumpCount++;
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
			if (map.onLadder(getCollisionBounds())) {
				physics.velocity.y -= climbSpeed;
				climbing = !climbing;
			} else if (KeyHandler.keyClicked(Input.Keys.DOWN) || KeyHandler.keyClicked(Input.Keys.S)) {
				physics.flickerDarkPlatformIntersection();
			}
		}

		if (map.onLadder(getCollisionBounds())) {
			render.setAnimation(climbAnim);
			jumpCount = 0;

			if (!climbing)
				render.resetAnimation();
		}
	}

	private boolean wasOnGroundRecently() {
		for (int i = 0; i < wasOnGround.length; i++)
			if (wasOnGround[i])
				return true;

		return false;
	}

	private void changeBasedOnConsoleVariables() {
		physics.setGravity((Float) fields.get(0).getSelectedValue());
		moveSpeed = (Float) fields.get(1).getSelectedValue();
		climbSpeed = (Float) fields.get(2).getSelectedValue();
		maxJumps = (Boolean) fields.get(3).getSelectedValue() ? 2 : 1;
	}

	public Rectangle getCollisionBounds() {
		return new Rectangle(bounds.x + 4, bounds.y, bounds.width - 8, bounds.height - 5);
	}

	public static void initConsoleObject() {
		consoleObject = new ConsoleObject("obj_player", tempFields);
	}

	private static ArrayList<ConsoleField<?>> initFields() {
		ArrayList<ConsoleField<?>> fields = new ArrayList<ConsoleField<?>>();

		ArrayList<Float> gravityOptions = new ArrayList<Float>();
		gravityOptions.add(-1f);
		gravityOptions.add(-0.5f);
		gravityOptions.add(0f);
		gravityOptions.add(0.5f);
		gravityOptions.add(1f);

		fields.add(new ConsoleField<Float>("gravity", gravityOptions, 3));

		ArrayList<Float> speedOptions = new ArrayList<Float>();
		speedOptions.add(2f);
		speedOptions.add(4f);
		speedOptions.add(6f);
		speedOptions.add(8f);
		speedOptions.add(10f);

		fields.add(new ConsoleField<Float>("move_speed", speedOptions, 2));
		fields.add(new ConsoleField<Float>("climb_speed", speedOptions, 2));

		fields.add(ConsoleField.createBooleanField("double_jump", false));

		return fields;
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

		climbAnim = new Animation(7f, climbFrames);
		climbAnim.setPlayMode(PlayMode.LOOP);

		jumpSprite = new Sprite(texture, 100, 77, 50, 77);
	}

}
