package games.main;

import java.util.ArrayList;

public class Array {
	
	public static <T> ArrayList<T> copy(ArrayList<T> array) {
		ArrayList<T> newArray = new ArrayList<T>();
		for(T thing : array) {
			newArray.add(thing);
		}
		return newArray;
	}
	
}
