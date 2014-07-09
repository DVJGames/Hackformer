package game.entity.component;

import game.entity.Camera;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Render extends Component {

	private Sprite sprite;
	private Animation animation;
	private float animTime = 0;
	private boolean flipped = false;

	public Render(Sprite sprite) {
		this.sprite = sprite;
	}

	public Render(Animation animation) {
		this.animation = animation;
	}

	public void update(Camera camera, float dt) {
		super.update(camera, dt);

		if (animation != null) {
			animTime += dt;
		} else
			animTime = 0;
	}

	public void render(Camera camera, SpriteBatch batch) {
		super.render(camera, batch);

		batch.begin();

		if (sprite != null) {
			sprite.setBounds(parent.bounds.x, parent.bounds.y, parent.bounds.width, parent.bounds.height);
			sprite.setFlip(flipped, false);
			sprite.draw(batch);
		}

		if (animation != null) {
			TextureRegion region = animation.getKeyFrame(animTime);

			if ((flipped && !region.isFlipX()) || (!flipped && region.isFlipX()))
				region.flip(true, false);

			batch.draw(region, parent.bounds.x, parent.bounds.y, parent.bounds.width, parent.bounds.height);
		}

		batch.end();
	}

	public Render setSprite(Sprite sprite) {
		this.sprite = sprite;
		this.animation = null;
		return this;
	}

	public Render setAnimation(Animation animation) {
		this.animation = animation;
		this.sprite = null;
		return this;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public Animation getAnimation() {
		return animation;
	}

	public Render resetAnimation() {
		animTime = 0;
		return this;
	}

	public Render setFlip(boolean flipped) {
		this.flipped = flipped;
		return this;
	}
	
	public boolean isFinishedAnimation() {
		if (animation == null)
			throw new IllegalStateException("Error: There is no animation currently playing!");
		
		return animation.isAnimationFinished(animTime);
	}

	public void dispose() {
		super.dispose();

		if (sprite != null)
			sprite.getTexture().dispose();
	}

}
