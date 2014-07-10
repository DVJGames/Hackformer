package game.world.level;

import game.entity.Camera;
import game.entity.Player;
import game.entity.PlayerDeath;
import game.world.EntityManager;
import game.world.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public abstract class Level {
	
	protected Map map;
	protected EntityManager manager;
	protected Camera camera;
	
	private String mapPath;
	private PlayerDeath playerDeath;
	
	public Level(String mapPath) {
		this.mapPath = mapPath;
		camera = new Camera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public void init() {
		if (playerDeath != null)
			return;
		
		Vector2 startPos = null;
		
		if (manager != null) {
			Player player = manager.getPlayer();
			
			if (player != null)
				startPos = new Vector2(player.bounds.x, player.bounds.y);
			
			manager.finish();
		}
		
		map = new Map(mapPath);
		manager = new EntityManager(map);
		manager.addEntity(camera);
		
		Player player = manager.getPlayer();
		
		if (player != null && startPos != null) {
			manager.remove(player);
			manager.addEntity(playerDeath = new PlayerDeath(startPos, new Vector2(player.bounds.x, player.bounds.y)));
		}
	}
	
	public void update(float dt) {
		if (playerDeath != null && playerDeath.isRemoved())
			playerDeath = null;
		
		manager.update(camera, dt);
	}
	
	public void render() {
		map.render(camera);
		manager.render(camera);
	}
	
}
