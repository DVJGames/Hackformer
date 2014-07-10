package game.world;

import game.world.level.Level;
import game.world.level.Level1;
import game.world.level.TestLevel;

public class LevelFactory {

	private static int levelNum = 1;

	public static Level getNextLevel() {
		switch (levelNum++) {
		case 1:
			return new Level1();
		case 2:
			return new TestLevel();
		}

		throw new IllegalStateException("Error: Level " + (levelNum - 1) + " does not exist!");
	}
	
}
