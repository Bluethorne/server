package games.game;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import games.main.Pair;

public class Land implements Listener{
	
	public static ArrayList<Integer> map = new ArrayList<Integer>();
	ArrayList<ArrayList<Pair<Integer, Integer>>> land = new ArrayList<ArrayList<Pair<Integer, Integer>>>();
	static public ArrayList<Land> lands = new ArrayList<Land>();
	public static ArrayList<Pair<Player, Pair<Integer, Integer>>> buildbattlesmallest = new ArrayList<Pair<Player, Pair<Integer, Integer>>>();
	
	public Land(Integer plotSize, Integer plotNum) {
		lands.add(this);
		for(int count = 0; count < plotNum; count++) {
			ArrayList<Pair<Integer, Integer>> plot = new ArrayList<Pair<Integer, Integer>>();
			ArrayList<Integer> m = new ArrayList<Integer>();
			boolean found = false;
			for(int xStart = 0; found == false; xStart++) {
				plot.clear();
				m.clear();
				if(!map.contains(xStart)) {
					found = true;
					for(int x = 0; x < plotSize; x++) {
						if(!map.contains(x + xStart)) {
							m.add(x + xStart);
							for(int z = 0; z < plotSize; z++) {
								plot.add(new Pair<>(x + xStart, z));
							}
						} else {
							found = false;
						}
					}
				}
			}
			map.addAll(m);
			land.add(plot);
		}
	}
	
	public ArrayList<ArrayList<Pair<Integer, Integer>>> getLand() {
		return land;
	}
	
	public void unclaim() {
		for(ArrayList<Pair<Integer, Integer>> plot: land) {
			for(Integer x: Pair.getKeys(plot)) {
				map.remove(x);
			}
		}
	}
}
