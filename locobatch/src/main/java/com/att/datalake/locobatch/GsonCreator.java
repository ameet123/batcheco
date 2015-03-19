package com.att.datalake.locobatch;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class GsonCreator {

	public Gson get() {
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	}
}
