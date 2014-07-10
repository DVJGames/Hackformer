package game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Text extends Entity {

	private static BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/console font.fnt"), Gdx.files.internal("fonts/console font.png"), false);
	
	private Color color = Color.WHITE;
	private String text;
	
	public Text(String text, float x, float y) {
		super(new Rectangle(x, y, 0, 0));
		
		this.text = text;
		
		TextBounds stringBounds = font.getBounds(text);
		bounds.width = stringBounds.width;
		bounds.height = stringBounds.height;
	}

	public void renderEarly(Camera camera, SpriteBatch batch) {
		super.renderEarly(camera, batch);
		
		batch.begin();
		font.setColor(color);
		font.drawMultiLine(batch, text, bounds.x, bounds.y, 0, HAlignment.CENTER);
		batch.end();
	}
	
	public Text setColor(Color color) {
		this.color = color;
		return this;
	}
	
	static {
		font.setScale(2);
	}
	

}
