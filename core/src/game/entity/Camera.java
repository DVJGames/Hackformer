package game.entity;

import game.world.Map;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Camera extends Entity {

	public static ConsoleObject consoleObject;

	private OrthographicCamera orthoCamera;

	public Camera(float screenWidth, float screenHeight) {
		super(new Rectangle(-screenWidth / 2, -screenHeight / 2, screenWidth, screenHeight));
		orthoCamera = new OrthographicCamera(bounds.width, bounds.height);
	}

	public void update(Camera camera, float dt) {
		super.update(camera, dt);

		positionCameraInsideMap();
		updateCameraPosition();
	}

	private void positionCameraInsideMap() {
		if (bounds.x < bounds.width / 2)
			bounds.x = bounds.width / 2;

		else if (bounds.x + bounds.width / 2 > map.getWidth() * Map.TILE_SIZE)
			bounds.x = map.getWidth() * Map.TILE_SIZE - bounds.width / 2;

		if (bounds.y < bounds.height / 2)
			bounds.y = bounds.height / 2;

		else if (bounds.y + bounds.height / 2 > map.getHeight() * Map.TILE_SIZE)
			bounds.y = map.getHeight() * Map.TILE_SIZE - bounds.height / 2;
	}

	private void updateCameraPosition() {
		orthoCamera.position.x = bounds.x + (Float) consoleObject.fields.get(0).getSelectedValue();
		orthoCamera.position.y = bounds.y + (Float) consoleObject.fields.get(1).getSelectedValue();
		orthoCamera.update();
	}

	public void projectBatch(SpriteBatch batch) {
		batch.setProjectionMatrix(orthoCamera.combined);
	}

	public void projectMap(MapRenderer renderer) {
		renderer.setView(orthoCamera);
	}

	public Vector2 toWorldPos(Vector2 screenPos) {
		Vector3 screenPos3 = new Vector3(screenPos.x, screenPos.y, 0);
		Vector3 worldPos3 = orthoCamera.unproject(screenPos3);
		return new Vector2(worldPos3.x, worldPos3.y);
	}

	public Vector2 getMousePosInWorld() {
		Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		return toWorldPos(mousePos);
	}

	public Vector2 toScreenPos(Vector2 worldPos) {
		Vector3 worldPos3 = new Vector3(worldPos.x, worldPos.y, 0);
		Vector3 screenPos3 = orthoCamera.project(worldPos3);
		return new Vector2(screenPos3.x, screenPos3.y);
	}

	public Matrix4 getCombinedMatrix() {
		return orthoCamera.combined;
	}

	public void centerAt(float x, float y) {
		bounds.x = x;
		bounds.y = y;
	}

	public static void initConsoleObject() {
		ArrayList<Float> offsetOptions = new ArrayList<Float>();
		offsetOptions.add(-500f);
		offsetOptions.add(-250f);
		offsetOptions.add(0f);
		offsetOptions.add(250f);
		offsetOptions.add(500f);

		ArrayList<ConsoleField<?>> fields = new ArrayList<ConsoleField<?>>();

		fields.add(new ConsoleField<Float>("xOffset", offsetOptions, 2));
		fields.add(new ConsoleField<Float>("yOffset", offsetOptions, 2));

		consoleObject = new ConsoleObject("obj_camera", fields);
	}

}
