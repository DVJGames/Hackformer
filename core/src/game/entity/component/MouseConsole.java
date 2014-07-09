package game.entity.component;

import game.entity.Camera;
import game.entity.ConsoleField;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MouseConsole extends Component {

	private static ShapeRenderer sr = new ShapeRenderer();
	private static final Color SELECTED_COLOR = new Color(1, 0, 0, 1);
	private static final Color FIELD_COLOR = new Color(0.3f, 1f, 0.65f, 1f);
	private static final Color VALUE_COLOR = new Color(.95f, 0.68f, 1f, 1f);
	private static final Color CHANGED_VALUE_COLOR = new Color(1f, 1f, 0f, 1f);
	private static final Color DEFAULT_TRIANGLE_COLOR = new Color(1f, 1f, 1f, 1f);
	private static final Color COLLIDING_TRIANGLE_COLOR = new Color(0.8f, 0.8f, 0.8f, 1f);

	private static BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/console font.fnt"), Gdx.files.internal("fonts/console font.png"), false);

	private static final float FIELD_HEIGHT = 30;
	private static final float FIELD_SPACING = 5;
	private static final float FIELD_WIDTH = 100;
	private static final float VALUE_X_OFFS = 10;
	private static final float VALUE_WIDTH = 35;
	static final float TRIANGLE_LENGTH = 25;

	private ArrayList<ConsoleField<?>> fields;
	private ArrayList<TriangleSelector> triSelectors = new ArrayList<TriangleSelector>();
	private SpriteBatch batch = new SpriteBatch();

	private boolean selected = false;
	private boolean inConsoleMode = false;
	private int numHacks = 0;
	private int maxHacks = 1;

	public MouseConsole(ArrayList<ConsoleField<?>> fields) {
		this.fields = fields;
	}

	public void update(Camera camera, float dt) {
		super.update(camera, dt);

		calcNumHacks();
		
		if (inConsoleMode)
			if (changeValuesBasedOnMouseInput())
				return;

		selected = parent.getCollisionBounds().contains(camera.getMousePosInWorld());

		if (Gdx.input.isButtonPressed(0) && Gdx.input.justTouched())
			inConsoleMode = selected;
	}

	private void calcNumHacks() {
		numHacks = 0;
		
		for (int i = 0; i < fields.size(); i++)
			if (fields.get(i).isChanged())
				numHacks++;
	}

	private boolean changeValuesBasedOnMouseInput() {
		if (Gdx.input.isButtonPressed(0) && Gdx.input.justTouched()) {
			Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

			for (int i = 0; i < triSelectors.size(); i++) {
				if (triSelectors.get(i).isColliding(mousePos)) {
					if (triSelectors.get(i).field.isChanged() || numHacks < maxHacks ) {

						if (triSelectors.get(i).right)
							triSelectors.get(i).field.moveRight();
						else
							triSelectors.get(i).field.moveLeft();
					}
					
					return true;
				}
			}
		}

		triSelectors.clear();
		return false;
	}

	public void render(Camera camera, SpriteBatch batch) {
		super.render(camera, batch);

		if (!selected && !inConsoleMode)
			return;

		renderBounds(camera);

		if (!inConsoleMode)
			return;

		renderFields(batch, camera);
	}

	private void renderBounds(Camera camera) {
		Rectangle cBounds = parent.getCollisionBounds();

		Gdx.gl20.glLineWidth(3);

		sr.begin(ShapeType.Line);
		sr.setColor(SELECTED_COLOR);
		
		if (numHacks > 0)
			sr.setColor(CHANGED_VALUE_COLOR);
		
		sr.rect(cBounds.x - camera.bounds.x + camera.bounds.width / 2, cBounds.y - camera.bounds.y + camera.bounds.height / 2, cBounds.width, cBounds.height);
		sr.end();
	}
	
	private void renderFields(SpriteBatch batch, Camera camera) {
		Rectangle cBounds = parent.getCollisionBounds();

		float x = cBounds.x + cBounds.width - camera.bounds.x + camera.bounds.width / 2;
		float y = cBounds.y + cBounds.height + (fields.size() - 1) * FIELD_HEIGHT - camera.bounds.y + camera.bounds.height / 2;
		
		float width = FIELD_WIDTH + VALUE_WIDTH + 2 * (VALUE_X_OFFS + TRIANGLE_LENGTH);
		float height = FIELD_HEIGHT * 2;
		
		if (x + width > camera.bounds.width)
			x -= width + cBounds.width;
		
		if (y + height > camera.bounds.height)
			y -= height + cBounds.height;
		
		Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

		for (int i = 0; i < fields.size(); i++) {
			renderField(x, y, FIELD_WIDTH, FIELD_HEIGHT, fields.get(i).getName(), FIELD_COLOR);

			float valueX = x + VALUE_X_OFFS + FIELD_WIDTH + TRIANGLE_LENGTH;

			Color fieldColor = VALUE_COLOR;

			if (fields.get(i).isChanged())
				fieldColor = CHANGED_VALUE_COLOR;

			renderField(valueX, y, VALUE_WIDTH, FIELD_HEIGHT, fields.get(i).getSelectedValue().toString(), fieldColor);

			drawTriangles(valueX, y, true, fields.get(i), mousePos);
			drawTriangles(valueX, y, false, fields.get(i), mousePos);

			y -= (FIELD_HEIGHT + FIELD_SPACING);
		}
	}

	private void renderField(float x, float y, float width, float height, String s, Color color) {
		sr.begin(ShapeType.Filled);
		sr.setColor(color);
		sr.rect(x, y, width, height);
		sr.end();

		Gdx.gl20.glLineWidth(2);
		sr.begin(ShapeType.Line);
		sr.setColor(0f, 0f, 0f, 1f);
		sr.rect(x, y, width, height);
		sr.end();

		batch.begin();
		font.setColor(0f, 0f, 0f, 1f);
		font.draw(batch, s, x + (width - font.getBounds(s).width) / 2, y + font.getLineHeight());
		batch.end();
	}

	private void drawTriangles(float x, float y, boolean filled, ConsoleField<?> field, Vector2 mousePos) {
		if (filled)
			sr.begin(ShapeType.Filled);
		else {
			sr.begin(ShapeType.Line);
			sr.setColor(0, 0, 0, 1);
			Gdx.gl20.glLineWidth(2);
		}

		x -= TRIANGLE_LENGTH + VALUE_X_OFFS / 2;
		y += (FIELD_HEIGHT - TRIANGLE_LENGTH) / 2;

		TriangleSelector s = new TriangleSelector(x + TRIANGLE_LENGTH, y + TRIANGLE_LENGTH, x, y + TRIANGLE_LENGTH / 2, x + TRIANGLE_LENGTH, y, field, false);

		if (filled) {
			sr.setColor(DEFAULT_TRIANGLE_COLOR);

			triSelectors.add(s);
			if (s.isColliding(mousePos))
				sr.setColor(COLLIDING_TRIANGLE_COLOR);
		}

		sr.triangle(s.x1, s.y1, s.x2, s.y2, s.x3, s.y3);

		x += VALUE_WIDTH + TRIANGLE_LENGTH + VALUE_X_OFFS;
		s = new TriangleSelector(x, y + TRIANGLE_LENGTH, x + TRIANGLE_LENGTH, y + TRIANGLE_LENGTH / 2, x, y, field, true);

		if (filled) {
			sr.setColor(DEFAULT_TRIANGLE_COLOR);

			triSelectors.add(s);
			if (s.isColliding(mousePos))
				sr.setColor(COLLIDING_TRIANGLE_COLOR);
		}

		sr.triangle(s.x1, s.y1, s.x2, s.y2, s.x3, s.y3);

		sr.end();
	}

	public boolean isInConsoleMode() {
		return inConsoleMode;
	}

}

class TriangleSelector {
	float x1, x2, x3;
	float y1, y2, y3;

	ConsoleField<?> field;
	boolean right;

	public TriangleSelector(float x1, float y1, float x2, float y2, float x3, float y3, ConsoleField<?> field, boolean right) {
		super();
		this.x1 = x1;
		this.x2 = x2;
		this.x3 = x3;
		this.y1 = y1;
		this.y2 = y2;
		this.y3 = y3;
		this.field = field;
		this.right = right;
	}

	public boolean isColliding(Vector2 mousePos) {
		return mousePos.dst((x1 + x2 + x3) / 3, (y1 + y2 + y3) / 3) < MouseConsole.TRIANGLE_LENGTH / 2;
	}

}
