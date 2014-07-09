package game.world;

import game.entity.Camera;
import game.entity.Console;
import game.entity.Entity;
import game.entity.component.MouseConsole;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class EntityManager implements Disposable {

	private ArrayList<Entity> entities;
	private Map map;
	private SpriteBatch batch;

	private MouseConsole mouseConsole;
	private Console console;

	public EntityManager(Map map) {
		this.map = map;

		entities = new ArrayList<Entity>();
		batch = new SpriteBatch();

		map.addEntities(this);
	}

	public void update(Camera camera, float dt) {
		if (console != null) {
			if (!console.isActive())
				console = null;
			else {
				console.update(camera, dt);
				return;
			}
		}

		if (mouseConsole != null && mouseConsole.isInConsoleMode()) {
			mouseConsole.update(camera, dt);
			return;
		}

		for (int i = 0; i < entities.size(); i++) {

			if (entities.get(i).isRemoved()) {
				entities.remove(i--);
				continue;
			}

			if (entities.get(i) instanceof Console) {
				Console c = (Console) entities.get(i);

				if (c.isActive())
					console = c;
			}

			MouseConsole mConsole = entities.get(i).getComponent(MouseConsole.class);

			if (mConsole != null && mConsole.isInConsoleMode())
				mouseConsole = mConsole;

			entities.get(i).update(camera, dt);
		}
	}

	public void render(Camera camera) {
		camera.projectBatch(batch);

		for (int i = 0; i < entities.size(); i++)
			entities.get(i).render(camera, batch);
	}

	public void addEntity(Entity e) {
		entities.add(e);
		e.setManager(this);
		e.setMap(map);
	}

	public ArrayList<Entity> getEntitiesWithinArea(Rectangle area) {
		ArrayList<Entity> result = new ArrayList<Entity>();

		for (int i = 0; i < entities.size(); i++)
			if (entities.get(i).getCollisionBounds().overlaps(area))
				result.add(entities.get(i));

		return result;
	}

	public ArrayList<Entity> getEntitiesAtPoint(Vector2 point) {
		ArrayList<Entity> result = new ArrayList<Entity>();

		for (int i = 0; i < entities.size(); i++)
			if (entities.get(i).getCollisionBounds().contains(point))
				result.add(entities.get(i));

		return result;
	}

	public ArrayList<Entity> getEntitiesWithinArc(Vector2 pos, float radius, float angle, float angleSpread) {
		ArrayList<Entity> result = new ArrayList<Entity>();

		for (int i = 0; i < entities.size(); i++) {
			Vector2 ePos = entities.get(i).getCenter();

			if (ePos.dst(pos) > radius)
				continue;

			Vector2 direction = ePos.sub(pos);

			double angleToEntity = Math.toDegrees(Math.atan2(direction.y, direction.x));

			if (angleToEntity >= angle - angleSpread && angleToEntity <= angle + angleSpread)
				result.add(entities.get(i));
		}

		return result;
	}

	public void dispose() {
		for (int i = 0; i < entities.size(); i++)
			entities.get(i).dispose();
	}

}
