package com.bandi.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "uri", "actionType" }) 
/*
 * or exclude all values
 * 
 * @EqualsAndHashCode(exclude = { "responseDataId", "responseData" })
 */
public class URICache {

	private String uri;

	private String actionType;

	private int responseDataId;

	private ResponseData responseData;

	@Override
	public String toString() {
		return "URI_CACHE [uri=" + uri + ", actionType=" + actionType + ", responseDataId=" + responseDataId
				+ ", responseData=" + responseData + "]";
	}

}
