package com.bandi.data;

import javax.annotation.PostConstruct;

import org.raml.model.MimeType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "uri", "actionType" })
/*
 * or exclude all values
 * 
 * @EqualsAndHashCode(exclude = { "responseDataId", "responseData" })
 */
public class ResponseData {

	private int id;

	private MimeType mimeType;

	private Integer statusCode;

	private String uri;

	private String actionType;

	private String responseContentType;

	@Override
	public String toString() {
		return "ResponseData [id=" + id + ", mimeType=" + mimeType + ", statusCode=" + statusCode + ", uri=" + uri
				+ ", actionType=" + actionType + ", responseContentType=" + responseContentType + "]";
	}

	@PostConstruct
	public void setResponseContentType(String value/* not used */) {
		if (mimeType != null)
			responseContentType = mimeType.getType();
	}

}
