package com.bandi.data;

import org.raml.model.ActionType;
import org.raml.model.MimeType;

import lombok.Data;

@Data
public class ResponseData {

	String responseContentType;

	ActionType actionType;

	MimeType mimeType;

	Integer statusCode;

	@Override
	public String toString() {
		return "ResponseData [responseContentType=" + responseContentType + ", actionType=" + actionType + ", mimeType="
				+ mimeType + ", statusCode=" + statusCode + ", sampleResponse=" + mimeType.getExample() + "]";
	}

}
