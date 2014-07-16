package game.entity;

import game.entity.component.MouseConsole;
import game.world.level.LevelFactory;

import java.util.ArrayList;

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
	
	private ArrayList<ConsoleField<?>> fields;
	private float defaultX, defaultY;
	
	public Text(String text, float x, float y) {
		this(createList(text), x, y);
	}
	
	private static ArrayList<String> createList(String text) {
		ArrayList<String> result = new ArrayList<String>();
		result.add(text);
		return result;
	}
	
	public Text(ArrayList<String> textOptions, float x, float y) {
		super(new Rectangle(x, y, 0, 0));
		
		this.defaultX = x;
		this.defaultY = y;
		
		setText(textOptions.get(0));
		
		ConsoleField<String> messageField = new ConsoleField<String> ("Text", textOptions, 0);
		
		fields = new ArrayList<ConsoleField<?>>();
		
		if (LevelFactory.getLevelNum() > 2)
			fields.add(messageField);
		
		addComponent(new MouseConsole(fields));
	}

	private void setText(String text) {
		for (int j = 0; j < text.length() - 1; j++) {
			if (text.charAt(j) == '\\' && text.charAt(j + 1) == 'n') {
				text = text.substring(0, j) + "\n" + text.substring(j + 2);
				j = 0;
			}
		}
		
		this.text = text;
		
		TextBounds stringBounds = font.getMultiLineBounds(text);
		bounds.width = stringBounds.width;
		bounds.height = stringBounds.height;
		bounds.x = defaultX - bounds.width / 2;
		bounds.y = defaultY - bounds.height;
	}

	public void renderEarly(Camera camera, SpriteBatch batch) {
		super.renderEarly(camera, batch);
		
		if (!fields.isEmpty()) 
			setText((String)fields.get(0).getSelectedValue());
		
		batch.begin();
		font.setColor(color);
		font.drawMultiLine(batch, text, bounds.x + bounds.width / 2, bounds.y + bounds.height, 0, HAlignment.CENTER);
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
