package game.entity;

import java.util.ArrayList;

public class ConsoleObject {

	public String name;
	public ArrayList<ConsoleField<?>> fields;
	
	public ConsoleObject(String name, ArrayList<ConsoleField<?>> fields) {
		this.name = name;
		this.fields = fields;
	}
	
	public boolean isChanged() {
		for (int i = 0; i < fields.size(); i++) 
			if (fields.get(i).isChanged())
				return true;
		
		return false;
	}
}

