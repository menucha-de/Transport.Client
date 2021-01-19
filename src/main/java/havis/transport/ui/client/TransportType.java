package havis.transport.ui.client;

public enum TransportType {
	AZURE("azure", "//", ""),
	CUSTOM(""),
	FILE("file", "//", "/"),
	HTTP("http", "//", "127.0.0.1"),
	HTTPS("https", "//", "127.0.0.1"),
	JDBC("jdbc"),
	MQTT("mqtt", "//", "127.0.0.1"),
	MQTTS("mqtts", "//", "127.0.0.1"),
	SQL("sql", "//", null, "?"),
 	TCP("tcp", "//", "127.0.0.1"),
	UDP("udp", "//", "127.0.0.1");

	private String scheme;
	private String schemeSpecificPart;
	private String host;
	private String query;

	private TransportType(String scheme) {
		this.scheme = scheme;
	}

	private TransportType(String scheme, String schemeSpecificPart) {
		this.scheme = scheme;
		this.schemeSpecificPart = schemeSpecificPart;
	}

	private TransportType(String scheme, String schemeSpecificPart, String authority) {
		this.scheme = scheme;
		this.schemeSpecificPart = schemeSpecificPart;
		this.host = authority;
	}

	private TransportType(String scheme, String schemeSpecificPart, String authority, String query) {
		this.scheme = scheme;
		this.schemeSpecificPart = schemeSpecificPart;
		this.host = authority;
		this.query = query;
	}

	public String getScheme() {
		return scheme;
	}

	public String getTemplate() {
		String template = scheme + ":";
		if (schemeSpecificPart != null)
			template += schemeSpecificPart;
		if (host != null)
			template += host;
		if (query != null)
			template += query;
		return template;
	}

	public static TransportType valueByScheme(String scheme) {
		for (TransportType t : TransportType.values()) {
			if (t.scheme.equals(scheme))
				return t;
		}
		return CUSTOM;
	}
}
