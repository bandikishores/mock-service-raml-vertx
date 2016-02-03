package com.bandi.data;

import org.raml.model.MimeType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ResponseData {
	
	String responseContentType;
	
	MimeType mimeType;

}
