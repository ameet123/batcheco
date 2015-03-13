package com.att.datalake.loco.offercriteria.impl;

import java.util.List;

import com.att.datalake.loco.offercriteria.model.OfferSpecification.Detail;

/**
 * an interface to build offer sql
 * @author ac2211
 *
 */
public interface OfferBuilder {
	String build(List<Detail> d);
}
