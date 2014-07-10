package game.world.level;

import game.entity.Text;

public class Level1 extends Level {

	public Level1() {
		super("map1.tmx");
	}

	public void init() {
		super.init();
		
		manager.addEntity(new Text("WASD or Arrow Keys\nTo Move", 200, 200));
		manager.addEntity(new Text("D or Down Arrow\nTo Drop Down\nFrom Bridges", 630, 200));
	}

}
