package com.amway.support;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.ArithmeticUtils;

import com.amway.tdata.TData.Global;

public class CommonUtils {
	

	/*
	 *This method accepts generic list for integer type and adds all the element for that list
	 */
	public static int sumAllElementsForList(List<Integer> sumList)
	{
		Integer sum = sumList.stream()
				  .collect(Collectors.summingInt(Integer::intValue));
		return sum;
	}
	
	/*
	 *This method accepts generic list for string type and converts into integer
	 */
	public static List<Integer> convertStringListToInteger(List<String> intList)
	{
		return intList.stream().map(Integer::parseInt).collect(Collectors.toList());
		
	}

	public static  boolean validateStringResults(String actual, String expected) {
		return actual.equals(expected);
	}

	/*
	 * This method is for creating a list from comma separated list of strings
	 */
	public static List<String> createListFromCommaSeapratedStrings(String actualCommaSeaparatedString) {
		List<String> splitString = new ArrayList<>();
		for (String str : actualCommaSeaparatedString.split(Global.COMMAN_SPLIT))
			splitString.add(str);
		return splitString;
	}
}
