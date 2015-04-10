package com.att.datalake.loco.util;

public final class OfferConstants {
	private OfferConstants() {
		// TODO Auto-generated constructor stub
	}
	public static final String OFFER_DB = "ameet";
	public static final String OFFER_DAILY_TABLE = OFFER_DB + ".offer_daily";
	public static final String OFFER_FINAL_TABLE = OFFER_DB + ".offer_daily";
	
	// Offer1 constants
	public static final String OFFER1_CHARGE_TABLE = OFFER_DB + ".loco_charge";
	public static final String OFFER1_ADJUSTMENT_TABLE = OFFER_DB + ".loco_adjustment";
	public static final String OFFER1_MERGED_TABLE = OFFER_DB + ".loco_offer1";
	
	// Criteria
	public static final String OFFER_CRITERIA_ID = "LOCO";
	public static final String CRIT_LOCAL_EXTRACT_DIR = "/user/ac2211/loco_extract";
	
	public static final String OFFER_SHIP_DIR = "/user/ac2211/loco_extract_ship";
	public static final String OFFER_FINAL_FILE = OFFER_SHIP_DIR + "/loco_out.dat";
	public static final String OFFER_FINAL__COMPRESS_FILE = OFFER_SHIP_DIR + "/loco_out.dat.gz";
	

}