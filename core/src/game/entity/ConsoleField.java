package game.entity;

import java.util.ArrayList;

public class ConsoleField<T> {

	private String name;
	
	private T defaultValue;
	private ArrayList<T> possibleValues;
	private int selectedIndex;
	
	public ConsoleField(String name, ArrayList<T> possibleValues, int selectedIndex) {
		this.name = name;
		this.defaultValue = possibleValues.get(selectedIndex);
		this.possibleValues = possibleValues;
		this.selectedIndex = selectedIndex;
	}

	public String getName() {
		return name;
	}

	public T getSelectedValue() {
		return possibleValues.get(selectedIndex);
	}
	
	public boolean isChanged() {
		return defaultValue != getSelectedValue();
	}
	
	public void moveRight() {
		move(true);
	}
	
	public void moveLeft() {
		move(false);
	}
	
	private void move(boolean right) {
		if (right)
			selectedIndex++;
		else selectedIndex--;
		
		if (selectedIndex < 0)
			selectedIndex = possibleValues.size() - 1;
		else if (selectedIndex >= possibleValues.size())
			selectedIndex = 0;
	}
	
	public static ConsoleField<Boolean> createBooleanField(String name, boolean defaultValue) {
		ArrayList<Boolean> values = new ArrayList<Boolean>();
		values.add(false);
		values.add(true);
		
		int index = 0;
		
		if (defaultValue)
			index = 1;
		
		return new ConsoleField<Boolean>(name, values, index);
	}
	
	
}
