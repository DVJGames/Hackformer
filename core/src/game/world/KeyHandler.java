package game.world;

import com.badlogic.gdx.Gdx;

public class KeyHandler {

	private static boolean[] pressedLastFrame = new boolean[1000];
	private static boolean[] pressedThisFrame = new boolean[pressedLastFrame.length];
	
	public static boolean keyClicked(int keyCode) {
		return pressedThisFrame[keyCode] && !pressedLastFrame[keyCode];
	}
	
	public static void update() {
		for (int i = 0; i < pressedLastFrame.length; i++) 
			pressedLastFrame[i] = Gdx.input.isKeyPressed(i);
		
		boolean[] temp = pressedLastFrame;
		pressedLastFrame = pressedThisFrame;
		pressedThisFrame = temp;
	}
	
	
	
}
