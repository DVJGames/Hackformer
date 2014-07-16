package game.world.level;

public class LevelFactory {

	private static int levelNum = 1;

	public static Level getNextLevel() {
		return new Level("map" + levelNum++ + ".tmx");
	}

	public static int getLevelNum() {
		return levelNum - 1;
	}

}
