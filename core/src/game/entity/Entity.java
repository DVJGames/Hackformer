package game.entity;

import game.entity.component.Component;
import game.world.EntityManager;
import game.world.Map;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public abstract class Entity implements Disposable {

	private ArrayList<Component> components;
	private boolean removed = false;
	
	public Rectangle bounds;
	public EntityManager manager;
	public Map map;
	
	public Entity(Rectangle bounds) {
		components = new ArrayList<Component>();
		this.bounds = bounds;
	}

	public void update(Camera camera, float dt) {
		for (int i = 0; i < components.size(); i++) {
			
			if (!components.get(i).isInitialized()) 
				components.get(i).init(camera);
			
			components.get(i).update(camera, dt);
		}
	}

	public void renderEarly(Camera camera, SpriteBatch batch) {
		for (int i = 0; i < components.size(); i++) 
			if (components.get(i).isInitialized()) 
				components.get(i).renderEarly(camera, batch);
	}
	
	public void render(Camera camera, SpriteBatch batch) {
		for (int i = 0; i < components.size(); i++) 
			if (components.get(i).isInitialized()) 
				components.get(i).render(camera, batch);
	}
	
	public void renderLate(Camera camera, SpriteBatch batch) {
		for (int i = 0; i < components.size(); i++) 
			if (components.get(i).isInitialized()) 
				components.get(i).renderLate(camera, batch);
	}

	public void addComponent(Component component) {
		components.add(component);
		component.setParent(this);
	}

	public boolean isRemoved() {
		return removed;
	}

	public void remove() {
		removed = true;
	}
	
	public Rectangle getCollisionBounds() {
		return bounds;
	}

	@SuppressWarnings("unchecked")
	public <T> T getComponent(Class<T> component) {
		for (int i = 0; i < components.size(); i++) 
			if (components.get(i).getClass().equals(component)) 
				return (T) components.get(i);
		
		return null;
	}
	
	public void removeComponent(Component component) {
		for (int i = 0; i < components.size(); i++) {
			if (components.get(i) == component) {
				components.remove(i);
				return;
			}
		}
	}

	public void setManager(EntityManager manager) {
		this.manager = manager;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public void dispose() {
		for (int i = 0; i < components.size(); i++) 
			components.get(i).dispose();
	}

	public Vector2 getCenter() {
		Vector2 center = new Vector2();
		return bounds.getCenter(center);
	}

}
