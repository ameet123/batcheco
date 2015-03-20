package com.att.datalake.locobatch.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class GsonCreator {

	@Bean
	public Gson get() {
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	}
}
