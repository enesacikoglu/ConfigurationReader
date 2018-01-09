package com.cengenes.configuration.client.model;

/**
 * Contains Base Wrapper Parameter class names {String, Boolean, Integer,
 * Double}
 * 
 * @author enes.acikoglu
 */
public enum ConfigurationTypes {

	String, Boolean, Integer, Double;

	/**
	 * 
	 * @param type
	 * @return Object with casted to type {@link #ConfigurationTypes()}
	 */
	public static Object getConfigurationType(String type) {

		if (String.toString().equalsIgnoreCase(type)) {
			return String.class;
		} else if (Integer.toString().equalsIgnoreCase(type)) {
			return Integer.class;
		} else if (Double.toString().equalsIgnoreCase(type)) {
			return Double.class;
		} else if (Boolean.toString().equalsIgnoreCase(type)) {
			return Boolean.class;
		}
		return null;
	}

}
