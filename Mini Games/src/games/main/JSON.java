package games.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class JSON {
	
	public static <K, V> String toJSON(HashMap<K, V> jsonO) {
		String s = "{";
		for(Map.Entry<K,V> entry: jsonO.entrySet()) {
			K key = entry.getKey();
			V value = entry.getValue();
			s += "\"" + key + "\":";
			if(value instanceof HashMap) {
				HashMap<?, ?> map = (HashMap<?, ?>) value;
				s += toJSON(map);
			} else if(value instanceof ArrayList) {
				ArrayList<?> a = (ArrayList<?>) value;
				s += array(a);
			} else if(value instanceof Integer) {
				s += String.valueOf(value);
			} else {
				s += "\"" + value.toString() + "\"";
			}
			s += ",";
		}
		s = s.substring(0, s.length() - 1);
		s += "}";
		return s;
	}
	
	private static String array(ArrayList<?> array) {
		String s = "";
		s += "[";
		for(Object o: array) {
			if(o instanceof HashMap) {
				HashMap<?, ?> map = (HashMap<?, ?>) o;
				s += toJSON(map);
			} else if(o instanceof ArrayList) {
				ArrayList<?> a = (ArrayList<?>) o;
				s += array(a);
			} else if(o instanceof Integer) {
				s += String.valueOf(o);
			} else {
				s += "\"" + o.toString() + "\"";
			}
			s += ",";
		}
		s = s.substring(0, s.length() - 1);
		s += "]";
		return s;
	}
	
	public static HashMap<?, ?> getJSON(File file) {
		String json = "";
		try {
			Scanner scanner = new Scanner(file);
			json = scanner.useDelimiter("\\Z").next().replaceAll("\\R+", " ");
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			return null;
		}
		return getJSON(json);
	}
	
	public static HashMap<?, ?> getJSON(String json) {
		if(json != null && json != "") {
			return jsonO(json.substring(1, json.length() - 1));
		} else {
			return null;
		}
	}
	
	static private HashMap<?, ?> jsonO(String json) {
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		for(String s: split(json)) {
			Pair<?, ?> pair = jsonE(s);
			map.put(pair.getKey(), pair.getValue());
		}
		return map;
	}
	
	static private Pair<?, ?> jsonE(String json) {
		boolean tkey = true;
		String key = "";
		String tempval = "";
		for(char c: json.toCharArray()) {
			if(tkey == true) {
				if(c == ':') {
					tkey = false;
				} else if(c != '\"') {
					key += c;
				}
			} else {
				tempval += c;
			}
		}
		Pair<?, ?> entry = new Pair<Object, Object>(key, getO(tempval));
		return entry;
	}
	
	static private ArrayList<?> jsonA(String json) {
		ArrayList<Object> array = new ArrayList<Object>();
		for(String s: split(json)) {
			array.add(getO(s));
		}
		return array;
	}
	
	static private ArrayList<String> split(String json) {
		ArrayList<String> split = new ArrayList<String>();
		String temp = "";
		int num = 0;
		boolean string = false;
		for(char c: json.toCharArray()) {
			if((c != ' ' && c != '\t') || string == true) {
				temp += c;
			}
			if(string == false) {
				if(c == '{' || c == '[') {
					num++;
				} else if(c == '}' || c == ']') {
					num--;
				}
				if(num == 0) {
					if(c == ',') {
						split.add(temp.substring(0, temp.length() - 1));
						temp = "";
					}
				}
			}
			if(c == '\"') {
				if(string == false) {
					string = true;
				} else {
					string = false;
				}
			}
		}
		split.add(temp);
		return split;
	}
	
	static Object getO(String s) {
		if(s.length() != 0) {
			if(s.toCharArray()[0] == '{') {
				return jsonO(s.substring(1, s.length() - 1));
			} else if(s.toCharArray()[0] == '[') {
				return jsonA(s.substring(1, s.length() - 1));
			} else if(s.toCharArray()[0] == '\"') {
				return s.substring(1, s.length() - 1);
			} else if(s.equals("true") || s.equals("false")) {
				return Boolean.valueOf(s);
			} else if(!s.contains(".")) {
				return Integer.valueOf(s);
			} else {
				return Double.valueOf(s);
			}
		} else {
			return null;
		}
	}
	
	static <K, V> HashMap<K, V> number(ArrayList<HashMap<K, V>> array, int num) {
		for(int count = 0; count < array.size(); count++) {
			Integer number = (Integer) array.get(count).get("number");
			if(number == num) {
				return array.get(count);
			}
		}
		return null;
	}
	
}
