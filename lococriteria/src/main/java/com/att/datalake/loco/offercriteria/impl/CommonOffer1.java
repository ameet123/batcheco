package com.att.datalake.loco.offercriteria.impl;

import org.springframework.stereotype.Component;

/**
 * alternative way to build Offer1, we are assuming that there is only one file/table to read from
 * not multiple, i.e. charge and adjustment have already been merged.
 * @author ac2211
 */
@Component
public class CommonOffer1 extends CommonOffer {

	private static final String FROM_TABLE = "ameet.loco_offer1";
	
	public CommonOffer1() {
		super.setFromTable(FROM_TABLE);
	}
}