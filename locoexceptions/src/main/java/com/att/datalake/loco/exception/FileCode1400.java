package com.att.datalake.loco.exception;

/**
 * All error codes related to invenio src template
 * 
 * @author am568g
 * 
 */
public enum FileCode1400 implements ErrorCode {

	FILE_APPEND_ERROR(1400, "file concatenate error, couldn't read source or write to target"), 
	OUTPUT_DIR_CREATION_ERROR(1401, "Could not create output directory, check permissions"),
	COMPRESS_FILE_CREATION_ERROR(1402,"Could not open output stream to create compress output file"),
	CONCATENATED_FILE_CREATION_ERROR(1403,"could not create concatenated output file"),
	CONCATENATE_FILE_ERROR(1404,"Either src/tgt does not exist or append error");

	private final int number;
	private final String description;

	private FileCode1400(int number, String description) {
		this.number = number;
		this.description = description;
	}

	public int getNumber() {
		return number;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return number + ": " + description;
	}
}
