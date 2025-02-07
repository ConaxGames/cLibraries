package com.conaxgames.libraries.util;

import java.util.TreeMap;

public class RomanNumerals {

	private final static TreeMap<Integer, String> ROMAN_NUMERALS = new TreeMap<>();

	static {
		ROMAN_NUMERALS.put(1000, "M");
		ROMAN_NUMERALS.put(900, "CM");
		ROMAN_NUMERALS.put(500, "D");
		ROMAN_NUMERALS.put(400, "CD");
		ROMAN_NUMERALS.put(100, "C");
		ROMAN_NUMERALS.put(90, "XC");
		ROMAN_NUMERALS.put(50, "L");
		ROMAN_NUMERALS.put(40, "XL");
		ROMAN_NUMERALS.put(10, "X");
		ROMAN_NUMERALS.put(9, "IX");
		ROMAN_NUMERALS.put(5, "V");
		ROMAN_NUMERALS.put(4, "IV");
		ROMAN_NUMERALS.put(1, "I");
		ROMAN_NUMERALS.put(0, ""); // Safety
	}

	private RomanNumerals() {
		throw new RuntimeException("Cannot instantiate a utility class.");
	}

	public static String toRoman(int number) {
		int l = ROMAN_NUMERALS.floorKey(number);
		if (number == l) {
			return ROMAN_NUMERALS.get(number);
		}
		return ROMAN_NUMERALS.get(l) + toRoman(number - l);
	}

}
