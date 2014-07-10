package game.entity;

import game.entity.component.Render;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PlayerDeath extends Entity {

	private static final float MOVE_ACCELERATION = 0.5f;
	private static final float CIRCLE_RADIUS = 5;
	private static ShapeRenderer sr = new ShapeRenderer();

	private Vector2 goalPos;
	private ArrayList<Vector2> circleCenters = new ArrayList<Vector2>();

	private float moveSpeed = 0;

	public PlayerDeath(Vector2 startPos, Vector2 goalPos) {
		super(new Rectangle(startPos.x, startPos.y, Player.WIDTH, Player.HEIGHT));
		addComponent(new Render(Player.standSprite));
		this.goalPos = goalPos;
	}

	public void update(Camera camera, float dt) {
		super.update(camera, dt);

		moveSpeed += MOVE_ACCELERATION;

		Vector2 dir = goalPos.cpy().sub(bounds.x, bounds.y);
		float length = dir.len();
		dir.nor().scl(Math.min(length, moveSpeed));

		bounds.x += dir.x;
		bounds.y += dir.y;

		circleCenters.add(new Vector2(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2));

		camera.centerAt(bounds.x, bounds.y);

		if (length <= moveSpeed) {
			remove();
			manager.addEntity(new Player(goalPos.x, goalPos.y));
		}
	}

	public void render(Camera camera, SpriteBatch batch) {
		super.render(camera, batch);

		sr.begin(ShapeType.Filled);
		sr.setColor(0, 0, 0, 1);

		for (int i = 0; i < circleCenters.size(); i++)
			sr.circle(circleCenters.get(i).x - camera.bounds.x + camera.bounds.width / 2, circleCenters.get(i).y - camera.bounds.y + camera.bounds.height / 2, CIRCLE_RADIUS);

		sr.end();
	}

}
