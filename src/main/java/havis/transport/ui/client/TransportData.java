package havis.transport.ui.client;

import havis.net.rest.shared.data.Uri;
import havis.transport.ui.client.editor.TransportProperty;

import java.util.List;

public class TransportData {
	private Uri uri;
	private List<TransportProperty> properties;
	private TransportType transportType;
	private String resultUri;

	public TransportData(TransportType transportType, Uri uri, List<TransportProperty> properties, String resultUri) {
		this.uri = uri;
		this.properties = properties;
		this.transportType = transportType;
		this.resultUri = resultUri;
	}

	public Uri getUri() {
		return uri;	
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public List<TransportProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<TransportProperty> properties) {
		this.properties = properties;
	}

	public TransportType getTransportType() {
		return transportType;
	}

	public void setTransportType(TransportType transportType) {
		this.transportType = transportType;
	}

	public String getResultUri() {
		return resultUri;
	}

	public void setResultUri(String resultUri) {
		this.resultUri = resultUri;
	}
}
