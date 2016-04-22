package com.bandi.data;

import lombok.Data;

@Data
public class ServerData {

	private String url;
	
	private Integer port;

	@Override
	public String toString() {
		return "http://"+url+((port != null)?(":"+port):"");
	}
}
