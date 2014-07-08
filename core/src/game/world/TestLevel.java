package game.world;

import game.entity.Camera;
import game.entity.Console;
import game.entity.ConsoleField;
import game.entity.ConsoleObject;
import game.entity.Player;

import java.util.ArrayList;

public class TestLevel extends Level {

	public TestLevel() {
		super("map1.tmx");
	}
	
	public void init() {
		super.init();
		
		Console console;
		
		manager.addEntity(console = new Console(camera));
		
		Player.initConsoleObject();
		console.addObject(Player.consoleObject);
		
		Camera.initConsoleObject();
		console.addObject(Camera.consoleObject);
		
		ArrayList<ConsoleField<?>> wallFields = new ArrayList<ConsoleField<?>>();
		wallFields.add(ConsoleField.createBooleanField("Visible", true));
		wallFields.add(ConsoleField.createBooleanField("Solid", true));
		console.addObject(new ConsoleObject("obj_walls", wallFields));
	}

}
