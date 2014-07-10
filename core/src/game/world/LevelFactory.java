package game.world;

public class LevelFactory {

	private static int levelNum = 1;

	public static Level getNextLevel() {
		switch (levelNum++) {
		case 1:
			return new TestLevel();
		case 2:
			return new TestLevel();
		}

		throw new IllegalStateException("Error: Level " + (levelNum - 1) + " does not exist!");
	}
	
}
