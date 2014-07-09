package game.world;

import game.entity.Camera;
import game.entity.Entity;
import game.entity.Monster;
import game.entity.Player;
import game.entity.Spike;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Map implements Disposable {

	public static final float TILE_SIZE = 16;

	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer renderer;
	private TiledMapTileLayer platformLayer;
	private TiledMapTileLayer darkPlatformLayer;
	private TiledMapTileLayer ladderLayer;

	public Map(String tmxPath) {
		tiledMap = new TmxMapLoader().load("maps/" + tmxPath);
		renderer = new OrthogonalTiledMapRenderer(tiledMap);

		platformLayer = (TiledMapTileLayer) tiledMap.getLayers().get("platforms");
		darkPlatformLayer = (TiledMapTileLayer) tiledMap.getLayers().get("dark platforms");
		ladderLayer = (TiledMapTileLayer) tiledMap.getLayers().get("ladders");
	}

	public void render(Camera camera) {
		camera.projectMap(renderer);
		renderer.render();
	}

	public boolean onLadder(Rectangle bounds) {
		int tileX1 = (int) ((bounds.x + bounds.width / 2) / Map.TILE_SIZE);
		int tileY1 = (int) ((bounds.y + bounds.height / 2) / Map.TILE_SIZE);

		return ladderAt(tileX1, tileY1);
	}

	public boolean platformAt(int x, int y) {
		return platformLayer.getCell(x, y) != null;
	}

	public boolean darkPlatformAt(int x, int y) {
		return darkPlatformLayer.getCell(x, y) != null && darkPlatformLayer.getCell(x, y + 1) == null;
	}

	public boolean ladderAt(int x, int y) {
		return ladderLayer.getCell(x, y) != null;
	}

	public int getWidth() {
		return platformLayer.getWidth();
	}

	public int getHeight() {
		return platformLayer.getHeight();
	}

	public boolean outOfBounds(int x, int y) {
		return x < 0 || y < 0 || x >= getWidth() || y >= getHeight();
	}

	public void addEntities(EntityManager manager) {
		MapLayer objectsLayer = (MapLayer) tiledMap.getLayers().get("objects");

		if (objectsLayer == null)
			return;

		MapObjects objects = objectsLayer.getObjects();

		int numObjects = objects.getCount();

		for (int i = 0; i < numObjects; i++) {
			MapObject object = objects.get(i);
			Entity e = getEntityFromObject((RectangleMapObject) object);

			if (e != null)
				manager.addEntity(e);
		}
	}

	private Entity getEntityFromObject(RectangleMapObject object) {
		if (!object.isVisible())
			return null;

		String name = object.getName();

		if (name == null)
			return null;

		name = name.toLowerCase();

		Rectangle rect = object.getRectangle();
		float x = rect.getX();
		float y = rect.getY();

		Entity e = null;

		if (name.equals("monster"))
			e = new Monster(x, y);
		else if (name.equals("player"))
			e = new Player(x, y);
		else if (name.equals("spike"))
			e = new Spike(x, y);

		return e;
	}

	public void dispose() {
		tiledMap.dispose();
		renderer.dispose();
	}

}
