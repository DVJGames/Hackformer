package game.world;

import game.entity.Camera;
import game.entity.Door;
import game.entity.Entity;
import game.entity.Monster;
import game.entity.Player;
import game.entity.Spike;
import game.entity.Text;

import java.util.ArrayList;

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
	private TiledMapTileLayer bridgeLayer;

	public Map(String tmxPath) {
		tiledMap = new TmxMapLoader().load("maps/" + tmxPath);
		renderer = new OrthogonalTiledMapRenderer(tiledMap);
		
		platformLayer = (TiledMapTileLayer) tiledMap.getLayers().get("platforms");
		darkPlatformLayer = (TiledMapTileLayer) tiledMap.getLayers().get("dark platforms");
		ladderLayer = (TiledMapTileLayer) tiledMap.getLayers().get("ladders");
		bridgeLayer = (TiledMapTileLayer) tiledMap.getLayers().get("bridges");
		
		if (darkPlatformLayer == null)
			darkPlatformLayer = createDefaultLayer();
		if (ladderLayer == null)
			ladderLayer = createDefaultLayer();
		if (bridgeLayer == null)
			bridgeLayer = createDefaultLayer();
	}
	
	private TiledMapTileLayer createDefaultLayer() {
		return new TiledMapTileLayer(getWidth(), getHeight(), (int)TILE_SIZE, (int)TILE_SIZE);
	}

	public void render(Camera camera) {
		camera.projectMap(renderer);
		renderer.render();
//		
//		ShapeRenderer sr = new ShapeRenderer();
//		
//		Gdx.gl20.glLineWidth(1);
//		
//		sr.begin(ShapeType.Line);
//		sr.setColor(0, 0, 0, 0);
//		
//		for (int i = 0; i < getWidth(); i++)
//			for (int j = 0; j < getHeight(); j++)
//				if (bridgeTilesAt(i, j) || darkPlatformAt(i, j) || topPlatformAt(i, j))
//					sr.rect(i * TILE_SIZE - camera.bounds.x + camera.bounds.width / 2, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
//		
//		sr.end();
	}

	public boolean onLadder(Rectangle bounds) {
		int tileX1 = (int) ((bounds.x + bounds.width / 2) / Map.TILE_SIZE);
		int tileY1 = (int) ((bounds.y + bounds.height / 2) / Map.TILE_SIZE);

		return ladderAt(tileX1, tileY1);
	}

	public boolean platformAt(int x, int y) {
		if (outOfBounds(x, y))
			return false;

		return platformLayer.getCell(x, y) != null;
	}
	
	public boolean topPlatformAt(int x, int y) {
		return platformAt(x, y) && !platformAt(x, y + 1);
	}

	public boolean darkPlatformAt(int x, int y) {
		return darkPlatformAt(x, y, false);
	}
	
	public boolean darkPlatformAt(int x, int y, boolean flippedGravity) {
		if (outOfBounds(x, y))
			return false;
		
		return darkPlatformLayer.getCell(x, y) != null && darkPlatformLayer.getCell(x, y + 1) == null;
	}

	private boolean actuallyDarkPlatform(int x, int y) {
		if (outOfBounds(x, y))
			return false;

		return darkPlatformLayer.getCell(x, y) != null;
	}

	public boolean ladderAt(int x, int y) {
		if (outOfBounds(x, y))
			return false;

		return ladderLayer.getCell(x, y) != null;
	}

	public boolean upRightBridgeAt(int x, int y) {
		return isRightUp(x, y) || isRightUp(x - 1, y) || isRightUp(x, y - 1);
	}

	private boolean isRightUp(int x, int y) {
		if (!bridgeAt(x, y) && !bridgeAt(x - 1, y) && !bridgeAt(x + 1, y - 1))
			return false;

		return (bridgeAt(x - 1, y) && bridgeAt(x - 1, y + 1)) || platformAt(x - 1, y) || actuallyDarkPlatform(x - 1, y);
	}

	public boolean upLeftBridgeAt(int x, int y) {
		return isLeftUp(x, y) || isLeftUp(x + 1, y) || isLeftUp(x, y - 1);
	}

	private boolean isLeftUp(int x, int y) {
		if (!bridgeAt(x, y) && !bridgeAt(x + 1, y) && !bridgeAt(x - 1, y - 1))
			return false;

		return (bridgeAt(x + 1, y) && bridgeAt(x + 1, y + 1)) || platformAt(x + 1, y) || actuallyDarkPlatform(x + 1, y);
	}

	public boolean bridgeAt(int x, int y) {
		return isBridge(x, y) || ((isBridge(x + 1, y) || isBridge(x - 1, y)) && isBridge(x, y + 1));
	}

	public boolean bridgeTilesAt(int x, int y) {
		return bridgeLayer.getCell(x, y) != null;
	}

	private boolean isBridge(int x, int y) {
		if (outOfBounds(x, y))
			return false;

		return bridgeLayer.getCell(x, y) != null && (bridgeLayer.getCell(x, y - 1) == null || platformAt(x - 1, y) || actuallyDarkPlatform(x - 1, y) || platformAt(x + 1, y) || actuallyDarkPlatform(x + 1, y)) && !platformAt(x, y - 1) && !actuallyDarkPlatform(x, y - 1);
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

		if (name == null || name == "")
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
		else if (name.equals("door"))
			e = new Door(x, y);
		else if (name.equals("text")) {
			int index = 1;
			
			ArrayList<String> messages = new ArrayList<String>();
			
			while(true) {
				String message = (String) object.getProperties().get("message" + index++);
				
				if (message != null)
					messages.add(message);
				else break;
			}
			
			if (!messages.isEmpty())
				e = new Text(messages, x + rect.getWidth() / 2, y + rect.getHeight());
		}
		
		return e;
	}

	public void dispose() {
		tiledMap.dispose();
		renderer.dispose();
	}

}
