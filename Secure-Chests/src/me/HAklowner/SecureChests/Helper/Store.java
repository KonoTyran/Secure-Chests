package me.HAklowner.SecureChests.Helper;

import java.util.LinkedHashMap;
import java.util.Map;

import me.HAklowner.SecureChests.Lock;

import org.bukkit.Location;

public class Store extends LinkedHashMap<Location, Lock> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2797197856485202288L;
	private final int capacity;

	public Store(int capacity) {
		super(capacity +1, 1.1f, true);
		this.capacity = capacity;
	}
	
	public void put(Lock lock) {
		this.put(lock.getLocation(), lock);
	}
	
	public void remove(Lock lock) {
		this.remove(lock.getLocation());
	}

	protected boolean removeEldestEntry(Map.Entry<Location, Lock> eldest)
	{
		return size() > capacity;
	}

}
