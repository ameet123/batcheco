package com.att.datalake.loco.offerconfiguration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.att.datalake.loco.offerconfiguration.model.OfferComponent;

/**
 * to set up JPA crud methods against offer component table
 * @author ac2211
 *
 */
public interface OfferComponentRepository extends JpaRepository<OfferComponent, Long> {

	OfferComponent findByLocalDirectory(String dir);
}
