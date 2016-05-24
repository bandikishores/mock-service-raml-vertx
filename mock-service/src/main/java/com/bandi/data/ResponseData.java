package com.bandi.data;

import org.raml.model.MimeType;

import lombok.Data;

@Data
public class ResponseData {

	private int id;

	private MimeType mimeType;

	private Integer statusCode;

	@Override
	public String toString() {
		return "ResponseData [id=" + id + ", mimeType=" + mimeType + ", statusCode=" + statusCode + ", sampleResponse="
				+ mimeType.getExample() + "]";
	}

}
