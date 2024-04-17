package com.amway.support;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * HashMap Utils to copy / merge / add / delete data in HashMaps
 */
public class MapUtils {

	static public LinkedHashMap<String, String> mergeHashMaps(LinkedHashMap<String , String> sourceMap, LinkedHashMap<String, String> destinationMap){
		List<String> keySet = new ArrayList<String>(sourceMap.keySet());
		for(String key : keySet) {
			destinationMap.put(key, sourceMap.get(key));
		}
		return destinationMap;
	}
	
	static public  boolean mergeHashMapss(LinkedHashMap<String , String> sourceMap, LinkedHashMap<String, String> destinationMap){
		boolean destination =false;
		List<String> keySet = new ArrayList<String>(sourceMap.keySet());
		for(String key : keySet) {
			destinationMap.put(key, sourceMap.get(key));
			destination =true;
		}
		return destination;
	}
	
	
	static public LinkedHashMap<String, Object> mergeHashMapofObjects(LinkedHashMap<String , Object> sourceMap, LinkedHashMap<String, Object> destinationMap){
		List<String> keySet = new ArrayList<String>(sourceMap.keySet());
		for(String key : keySet) {
			destinationMap.put(key, sourceMap.get(key));
		}
		return destinationMap;
	}
	
	public static boolean addTwoLinkedListHashMap(
			LinkedList<LinkedHashMap<String, String>> expectedList,
			LinkedList<LinkedHashMap<String, String>> actualList
			) {
		int size = expectedList.size();
		
		boolean flag = true;
		try {
			for (int i = 0; i < size; i++) {
				if (!MapUtils.mergeHashMapss(expectedList.get(i),
						actualList.get(i)))
					flag = false;
			}
		} catch (NullPointerException np) {
			return false;
		}
		return flag;
	}
	
}
