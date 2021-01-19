package havis.transport.ui.client.data;

import havis.transport.ui.client.editor.TransportProperty;
import havis.transport.ui.res.cons.AppConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Property extends TransportProperty {
	private final static AppConstants CONSTANTS = AppConstants.INSTANCE;
	public static final List<PropertyGroup> PROPERTY_GROUPS;
	public final static Map<String, Property> PROPERTIES;

	// Transformer
	public final static String TRANSFORMER = "Transformer";
	public final static String TRANSFORMER_JAVASCRIPT_SCRIPT = "Transformer.javascript.Script";
	public final static String MIME_TYPE = "MimeType";
	// JDBC
	public final static String JDBC_GROUP = "<JDBC>";
	public final static String PREFIX = "Transporter.";
	public final static String JDBC_TABLE_NAME_PROPERTY = PREFIX + "JDBC.Table";
	public final static String JDBC_KEEP_CONNECTION_PROPERTY = PREFIX + "JDBC.KeepConnection";
	public final static String JDBC_INIT_STATEMENT = PREFIX + "JDBC.InitStatement";
	public final static String JDBC_DROP = PREFIX + "JDBC.Drop";
	public final static String JDBC_STORAGE = PREFIX + "JDBC.Storage";
	public final static String JDBC_CLEAR = PREFIX + "JDBC.Clear";
	public final static String DATA_CONVERTER_EXPRESSION_PROPERTY = PREFIX + "DataConverter.Expression";
	public final static String DATA_CONVERTER_AVOID_DUPLICATES_PROPERTY = PREFIX + "DataConverter.AvoidDuplicates";
	// Transporter
	public final static String RESEND_REPEAT_PERIOD = PREFIX + "ResendRepeatPeriod";
	public final static String RESEND_QUEUE_SIZE = PREFIX + "ResendQueueSize";

	static {
		Map<String, Property> properties = new HashMap<String, Property>();

		List<Property> transporterProperties = new ArrayList<Property>();
		transporterProperties.add(new Property(RESEND_REPEAT_PERIOD, "ResendRepeatPeriod"));
		transporterProperties.add(new Property(RESEND_QUEUE_SIZE, "ResendQueueSize"));
		for (Property p : transporterProperties) {
			properties.put(p.getName(), p);
		}
		
		List<Property> transformerProperties = new ArrayList<Property>();
		transformerProperties.add(new Property(TRANSFORMER, "Transformer", Arrays.asList(new String[] { "javascript" })));
		transformerProperties.add(new Property(TRANSFORMER_JAVASCRIPT_SCRIPT, "JavaScript"));
		transformerProperties.add(new Property(MIME_TYPE, "MimeType", Arrays.asList(new String[] { "text/plain", "text/xml", "application/json",
				"application/octet-stream" })));
		for (Property p : transformerProperties) {
			properties.put(p.getName(), p);
		}

		List<Property> jdbcProperties = new ArrayList<Property>();
		jdbcProperties = new ArrayList<Property>();
		jdbcProperties.add(new Property(JDBC_TABLE_NAME_PROPERTY, CONSTANTS.tablename() + "*"));
		jdbcProperties.add(new Property(DATA_CONVERTER_EXPRESSION_PROPERTY, CONSTANTS.expression() + "*"));
		jdbcProperties.add(new Property(JDBC_INIT_STATEMENT, CONSTANTS.initstatement()));
		jdbcProperties.add(new Property(JDBC_STORAGE, CONSTANTS.storage()));
		jdbcProperties.add(new Property(JDBC_CLEAR, CONSTANTS.clear(), Arrays.asList(new String[] { "True", "False" })));
		jdbcProperties.add(new Property(JDBC_DROP, CONSTANTS.drop(), Arrays.asList(new String[] { "True", "False" })));
		jdbcProperties.add(new Property(JDBC_KEEP_CONNECTION_PROPERTY, CONSTANTS.keepconnection(), Arrays.asList(new String[] { "True", "False" })));
		jdbcProperties
				.add(new Property(DATA_CONVERTER_AVOID_DUPLICATES_PROPERTY, CONSTANTS.avoidduplicates(), Arrays.asList(new String[] { "True", "False" })));
		for (Property p : jdbcProperties) {
			properties.put(p.getName(), p);
		}

		PROPERTY_GROUPS = Collections.unmodifiableList(Arrays.asList(new PropertyGroup[] {
				new PropertyGroup("Transporter", transporterProperties),
				new PropertyGroup("", transformerProperties),
				new PropertyGroup(JDBC_GROUP, jdbcProperties) 
		}));
		PROPERTIES = Collections.unmodifiableMap(properties);
	}

	public static Map<String, Property> getProperties() {
		return Collections.unmodifiableMap(new HashMap<String, Property>(PROPERTIES));
	}

	public static List<PropertyGroup> getPropertyGroups() {
		return Collections.unmodifiableList(PROPERTY_GROUPS);
	}

	private String label;
	private List<String> values;
	private String name;

	public Property(String name) {
		this.name = name;
	}

	public Property(String name, String label) {
		this.name = name;
		this.label = label;
	}

	public Property(String name, List<String> values) {
		this.name = name;
		this.values = values;
	}

	public Property(String name, String label, List<String> values) {
		this.name = name;
		this.label = label;
		this.values = values;
	}

	public String getLabel() {
		if (label == null) {
			return name;
		}
		return label;
	}

	public List<String> getValues() {
		return values;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return "Property [label=" + label + ", values=" + values + ", name=" + name + "]";
	}
}
