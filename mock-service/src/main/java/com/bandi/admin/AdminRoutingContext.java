package com.bandi.admin;

import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.bandi.cache.ServerCache;
import com.bandi.data.ServerData;
import com.bandi.log.Logger;
import com.bandi.util.Constants;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class AdminRoutingContext implements Handler<RoutingContext> {

	@Override
	public void handle(RoutingContext routingContext) {
		// Logger.log("Got a HTTP request to /");
		// routingContext.response().sendFile("webroot/update.html");

		if (routingContext.request().uri().contains(Constants.UPDATE)) {
			// routingContext.request().setExpectMultipart(true);
			// System.out.println(routingContext.request().formAttributes().size());
			String baseURI = routingContext.request().getFormAttribute(Constants.BASE_URI);
			String hostName = routingContext.request().getFormAttribute(Constants.HOSTNAME);
			String port = routingContext.request().getFormAttribute(Constants.PORT_STRING);

			StringBuilder data = new StringBuilder();
			data.append("<html><head><title>Welcome to Admin Portal</title></head>");
			data.append("<body> <br/>");
			data.append("<p style=\"BACKGROUND-COLOR: grey\" align=center>");
			if (StringUtils.isNotEmpty(hostName) && StringUtils.isNotEmpty(baseURI)) {
				ServerData serverData = new ServerData();
				serverData.setPort(
						StringUtils.isNotEmpty(port) && StringUtils.isNumeric(port) ? Integer.parseInt(port) : null);
				serverData.setUrl(hostName);
				ServerCache.insertInToCache(baseURI, serverData);
				data.append("Updated Succesfully ");
			} else {
				data.append("Update Failed!! Hostname or baseURI passed is empty");
			}

			data.append("</b><br/><br/></p></body></html>");
			routingContext.response().putHeader("Content-Type", MediaType.TEXT_HTML);
			routingContext.response().end(data.toString());
		} else {
			StringBuilder data = new StringBuilder();
			data.append("<html><head><title>Welcome to Admin Portal</title></head>");
			data.append("<body> <h2>Enter baseURI, along with the corresponding Server Hostname and Port </h2> <br/>");
			data.append(
					"<p style=\"BACKGROUND-COLOR: grey\" align=center> Example : To forward all calls to box <b> http://www.mainserver.com:8080/applicationName/getUsers </b><br/><br/>");
			data.append(" Base URI -> applicationName <br/>");
			data.append(" Hostname -> mainserver.com <br/>");
			data.append(" Port -> 8080 <br/><br/></p>");
			data.append(" Currently available list of servers : <br/><br/>");
			data.append("<table bgcolor=\"#f1f1c1\" border=\"1\">");
			data.append("<tr>    <th>Base URI</th>    <th>Hostname</th>    <th>Port</th>  </tr>");
			
			if(CollectionUtils.isEmpty(ServerCache.getAllServerBaseURI())) {
				data.append("<tr><td colspan=\"3\"><b><i><font size=\"5\">No Servers Added.</font></i></b></td></tr>");
			}
			else
			for (String baseUri : ServerCache.getAllServerBaseURI()) {
				data.append("<tr> <td>").append(baseUri).append("</td><td>")
						.append(ServerCache.getServerData(baseUri).getUrl()).append("</td><td>")
						.append(ServerCache.getServerData(baseUri).getPort()).append("</td></tr>");
			}
			data.append("<form action=\"update\" method=\"post\">");
			data.append("<tr><td colspan=\"3\">Base URI : <input type=\"text\" name=\"baseURI\"> <br/>");
			data.append("Hostname : <input type=\"text\" name=\"hostname\"> <br/>");
			data.append("Port : <input type=\"text\" name=\"port\"> <br/>");
			data.append("<input type=\"submit\" value=\"Add New Server\">");
			data.append(" </td></tr> </form> ");

			data.append("</table><br/><br/></body></html>");

			routingContext.response().putHeader("Content-Type", MediaType.TEXT_HTML);
			routingContext.response().end(data.toString());
		}
		// new
		// AdminRequestResponseHandler(routingContext).handle(routingContext.request());
	}
}
