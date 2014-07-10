package game.entity.component;

import game.entity.Camera;
import game.entity.Entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class Component implements Disposable {

	protected Entity parent;
	private boolean initialized = false;

	public Entity getParent() {
		return parent;
	}
	
	public void setParent(Entity parent) {
		this.parent = parent;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void init(Camera camera) {
		initialized = true;
	}

	public void update(Camera camera, float dt) {
	}

	public void renderEarly(Camera camera, SpriteBatch batch) {
	}
	
	public void render(Camera camera, SpriteBatch batch) {
	}

	public void dispose() {
	}

}
