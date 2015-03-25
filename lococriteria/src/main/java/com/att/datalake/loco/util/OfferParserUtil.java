package com.att.datalake.loco.util;

public final class OfferParserUtil {
	private OfferParserUtil() {
		// TODO Auto-generated constructor stub
	}
	private static final String TRANSIENT_FILE_REGEX = "^FILE.*";
	
	/**
	 * is the passed table transient or intermediary
	 * @param table
	 * @return
	 */
	public static boolean isTransient(String table) {
		return table.matches(TRANSIENT_FILE_REGEX);
	}

}
