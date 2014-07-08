package game.entity;

import game.world.KeyHandler;

import java.util.ArrayList;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

public class Console extends Entity {

	private static final float HEIGHT = 300;
	private static final float TEXT_X_OFFS = 30;
	private static final float TEXT_Y_OFFS = 20;

	private static final Color DEFAULT_TEXT_COLOR = new Color(1, 1, 1, 1);
	private static final Color HACKED_OBJECT_COLOR = new Color(1, 1, 0, 1);

	private static final ShapeRenderer sr = new ShapeRenderer();

	private ArrayList<ConsoleObject> objects = new ArrayList<ConsoleObject>();
	private ConsoleObject selectedObject = null;

	private SpriteBatch batch = new SpriteBatch();
	private BitmapFont font = new BitmapFont();

	private boolean active = false;
	private int selectedLine = 0;

	private int numHacks = 0, maxHacks = 1;

	public Console(Camera camera) {
		super(new Rectangle(0, camera.bounds.height - HEIGHT, camera.bounds.width, HEIGHT));
	}

	public void update(Camera camera, float dt) {
		super.update(camera, dt);

		toggleActive();

		if (!active)
			return;

		moveSelectedLine();
		selectItem();
		updateHacksCount();
	}

	private void toggleActive() {
		if (KeyHandler.keyClicked(Input.Keys.Q) || KeyHandler.keyClicked(Input.Keys.NUMPAD_0)) {
			active = !active;
			selectedObject = null;
			selectedLine = 0;
		}
	}

	private void moveSelectedLine() {
		if (KeyHandler.keyClicked(Input.Keys.W) || KeyHandler.keyClicked(Input.Keys.UP))
			moveSelectedLine(true);
		if (KeyHandler.keyClicked(Input.Keys.S) || KeyHandler.keyClicked(Input.Keys.DOWN))
			moveSelectedLine(false);
	}

	private void moveSelectedLine(boolean up) {
		if (up)
			selectedLine--;
		else
			selectedLine++;

		if (selectedObject != null) {
			if (selectedLine < 0)
				selectedLine = selectedObject.fields.size();
			else if (selectedLine > selectedObject.fields.size())
				selectedLine = 0;
		} else {
			if (selectedLine < 0)
				selectedLine = objects.size() - 1;
			else if (selectedLine >= objects.size())
				selectedLine = 0;
		}
	}

	private void selectItem() {
		if (selectedObject == null && (KeyHandler.keyClicked(Input.Keys.D) || KeyHandler.keyClicked(Input.Keys.RIGHT) || KeyHandler.keyClicked(Input.Keys.A) || KeyHandler.keyClicked(Input.Keys.LEFT))) {
			selectedObject = objects.get(selectedLine);
			selectedLine = 0;
			return;
		}

		if (selectedObject == null)
			return;

		if (KeyHandler.keyClicked(Input.Keys.A) || KeyHandler.keyClicked(Input.Keys.LEFT)) {
			if (selectedLine == 0) {
				selectedLine = objects.indexOf(selectedObject);
				selectedObject = null;
				return;
			}

			if (numHacks < maxHacks || selectedObject.fields.get(selectedLine - 1).isChanged())
				selectedObject.fields.get(selectedLine - 1).moveLeft();
		}
		if (selectedLine != 0 && (KeyHandler.keyClicked(Input.Keys.D) || KeyHandler.keyClicked(Input.Keys.RIGHT)))
			if (numHacks < maxHacks || selectedObject.fields.get(selectedLine - 1).isChanged())
				selectedObject.fields.get(selectedLine - 1).moveRight();
	}

	private void updateHacksCount() {
		numHacks = 0;

		for (int i = 0; i < objects.size(); i++)
			for (int j = 0; j < objects.get(i).fields.size(); j++)
				if (objects.get(i).fields.get(j).isChanged())
					numHacks++;
	}

	public void render(Camera camera, SpriteBatch batch) {
		super.render(camera, batch);

		if (!active)
			return;

		drawBackground();
		drawSelectedLine(camera);

		if (selectedObject == null)
			drawObjectsList(camera);
		else
			drawObjectFields(selectedObject, camera);

		drawHacksCount(camera);
	}

	private void drawBackground() {
		sr.begin(ShapeType.Filled);
		sr.setColor(0, 0, 0, 1);
		sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		sr.end();
	}

	private void drawSelectedLine(Camera camera) {
		sr.begin(ShapeType.Filled);
		sr.setColor(.1f, .8f, .1f, 1);
		sr.rect(bounds.x, camera.bounds.height - (selectedLine + 1) * font.getLineHeight() - TEXT_Y_OFFS - font.getLineHeight(), bounds.width, font.getLineHeight());
		sr.end();
	}

	private void drawObjectsList(Camera camera) {
		batch.begin();
		font.setColor(DEFAULT_TEXT_COLOR);

		font.draw(batch, "<objects list>", bounds.x + TEXT_X_OFFS / 2, camera.bounds.height - TEXT_Y_OFFS);

		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i).isChanged())
				font.setColor(HACKED_OBJECT_COLOR);
			else
				font.setColor(DEFAULT_TEXT_COLOR);

			font.draw(batch, objects.get(i).name, bounds.x + TEXT_X_OFFS, camera.bounds.height - (i + 1) * font.getLineHeight() - TEXT_Y_OFFS);
		}

		batch.end();
	}

	private void drawObjectFields(ConsoleObject selectedObject, Camera camera) {
		batch.begin();

		font.setColor(DEFAULT_TEXT_COLOR);
		font.draw(batch, "<variables of " + selectedObject.name + ">", bounds.x + TEXT_X_OFFS / 2, camera.bounds.height - TEXT_Y_OFFS);

		font.draw(batch, "<..", bounds.x + TEXT_X_OFFS, camera.bounds.height - TEXT_Y_OFFS - font.getLineHeight());

		for (int i = 0; i < selectedObject.fields.size(); i++) {
			String line = selectedObject.fields.get(i).getName() + "    " + selectedObject.fields.get(i).getSelectedValue();

			if (selectedObject.fields.get(i).isChanged())
				font.setColor(HACKED_OBJECT_COLOR);
			else
				font.setColor(DEFAULT_TEXT_COLOR);

			font.draw(batch, line, bounds.x + TEXT_X_OFFS, camera.bounds.height - (i + 2) * font.getLineHeight() - TEXT_Y_OFFS);
		}

		batch.end();
	}

	private void drawHacksCount(Camera camera) {
		batch.begin();

		if (numHacks >= maxHacks)
			font.setColor(HACKED_OBJECT_COLOR);
		else
			font.setColor(DEFAULT_TEXT_COLOR);

		font.draw(batch, "Hacks " + numHacks + " / " + maxHacks, camera.bounds.width - 200, camera.bounds.height - TEXT_Y_OFFS);
		batch.end();
	}

	public void addObject(ConsoleObject object) {
		objects.add(object);
	}

	public boolean isActive() {
		return active;
	}
}
