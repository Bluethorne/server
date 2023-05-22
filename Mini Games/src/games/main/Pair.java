package games.main;

import java.util.ArrayList;

public class Pair<K, V> {
	
	K key;
	V value;
	
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public void setKey(K key) {
		this.key = key;
	}
	
	public void setValue(V value) {
		this.value = value;
	}
	
	public K getKey() {
		return this.key;
	}
	
	public V getValue() {
		return this.value;
	}
	
	public static <K, V> ArrayList<K> getKeys(ArrayList<Pair<K, V>> pairList) {
		ArrayList<K> keys = new ArrayList<K>();
		for(Pair<K, V> pair: pairList) {
			keys.add(pair.getKey());
		}
		return keys;
	}
	
	public static <K, V> ArrayList<V> getValues(ArrayList<Pair<K, V>> pairList) {
		ArrayList<V> keys = new ArrayList<V>();
		for(Pair<K, V> pair: pairList) {
			keys.add(pair.getValue());
		}
		return keys;
	}
	
	@Override
	public String toString() {
		return this.key + "=" + this.value;
	}
	
	public static <K, V> ArrayList<K> getKey(V value, ArrayList<Pair<K, V>> pairList) {
		ArrayList<K> keyList = new ArrayList<K>();
		for(Pair<K, V> pair: pairList) {
			if(pair.getValue().equals(value)) {
				keyList.add(pair.getKey());
			}
		}
		return keyList;
	}
	
	public static <K, V> ArrayList<V> getValue(K key, ArrayList<Pair<K, V>> pairList) {
		ArrayList<V> valueList = new ArrayList<V>();
		for(Pair<K, V> pair: pairList) {
			if(pair.getKey().equals(key)) {
				valueList.add(pair.getValue());
			}
		}
		return valueList;
	}
	
	public static <K, V> void setKey(K key, V value, ArrayList<Pair<K, V>> pairList) {
		for(Pair<K, V> pair: pairList) {
			if(pair.getValue().equals(value)) {
				pair.setKey(key);
			}
		}
	}
	
	public static <K, V> void setValue(V value, K key, ArrayList<Pair<K, V>> pairList) {
		for(Pair<K, V> pair: pairList) {
			if(pair.getKey().equals(key)) {
				pair.setValue(value);
			}
		}
	}
	
	public static <K, V> ArrayList<Pair<K,V>> getPairFromValue(V value, ArrayList<Pair<K, V>> pairList) {
		ArrayList<Pair<K, V>> keyList = new ArrayList<Pair<K, V>>();
		for(Pair<K, V> pair: pairList) {
			if(pair.getValue().equals(value)) {
				keyList.add(pair);
			}
		}
		return keyList;
	}
	
	public static <K, V> ArrayList<Pair<K,V>> getPairFromKey(K key, ArrayList<Pair<K, V>> pairList) {
		ArrayList<Pair<K,V>> valueList = new ArrayList<Pair<K, V>>();
		for(Pair<K, V> pair: pairList) {
			if(pair.getKey().equals(key)) {
				valueList.add(pair);
			}
		}
		return valueList;
	}
	
	public boolean equalTo(Pair<K, V> pair) {
		if(pair == this) {
			return true;
		}
		if(pair.equals(this)) {
			return true;
		}
		if(pair.getKey() == this.getKey() && pair.getValue() == this.getValue()) {
			return true;
		}
		if(pair.getKey().equals(this.getKey()) && pair.getValue().equals(this.getValue())) {
			return true;
		}
		return false;
	}
	
	public static <K, V> void removeAllEqualTo(ArrayList<Pair<K, V>> pairList, ArrayList<Pair<K, V>> remove) {
		ArrayList<Pair<K, V>> temp = new ArrayList<Pair<K, V>>();
		for(Pair<K, V> pair: pairList) {
			for(Pair<K, V> pairRemove: remove) {
				if(pair.equalTo(pairRemove)) {
					temp.add(pair);
				}
			}
		}
		pairList.removeAll(temp);
	}
}
