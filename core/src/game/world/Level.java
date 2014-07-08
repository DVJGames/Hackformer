package game.world;

import com.badlogic.gdx.Gdx;

import game.entity.Camera;

public abstract class Level {
	
	protected Map map;
	protected EntityManager manager;
	protected Camera camera;
	
	private String mapPath;
	
	public Level(String mapPath) {
		this.mapPath = mapPath;
	}
	
	public void init() {
		map = new Map(mapPath);
		manager = new EntityManager(map);
		manager.addEntity(camera = new Camera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}
	
	public void update(float dt) {
		manager.update(camera, dt);
	}
	
	public void render() {
		map.render(camera);
		manager.render(camera);
	}
	
}
