package game.entity.component;

import game.entity.Camera;
import game.entity.Collidable;
import game.entity.Entity;

import java.util.ArrayList;

public class Collider extends Component {

	public void update(Camera camera, float dt) {
		super.update(camera, dt);
		
		ArrayList<Entity> collisions = parent.manager.getEntitiesWithinArea(parent.getCollisionBounds());
		
		for (int i = 0; i < collisions.size(); i++) {
			if (!(collisions.get(i) instanceof Collidable))
				continue;
			
			((Collidable) collisions.get(i)).onCollide(parent);
			
			if (parent instanceof Collidable) 
				((Collidable) parent).onCollide(collisions.get(i));
		}
	}
	
}
