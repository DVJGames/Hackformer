package game.entity;

import game.entity.component.Render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;

public class Death extends Entity {

	private Render render;
	
	public Death(Rectangle bounds, Animation deathAnimation) {
		super(bounds);
		addComponent(render = new Render(deathAnimation));
	}

	public void update(Camera camera, float dt) {
		super.update(camera, dt);
		
		if (render.isFinishedAnimation()) 
			remove();
	}

}
