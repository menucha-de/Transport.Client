package havis.transport.ui.client.editor;

import havis.net.rest.shared.data.Uri;
import havis.net.ui.shared.client.ConfigurationSection;
import havis.net.ui.shared.client.widgets.IntegerTextBox;
import havis.net.ui.shared.client.widgets.LabelRow;
import havis.net.ui.shared.client.widgets.Util;
import havis.net.ui.shared.resourcebundle.ResourceBundle;
import havis.transport.ui.client.TransportData;
import havis.transport.ui.client.TransportType;
import havis.transport.ui.client.event.EditScriptEvent;
import havis.transport.ui.client.event.EditScriptEvent.Handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class TransportEditor extends Composite implements ValueAwareEditor<TransportData>, HasValueChangeHandlers<TransportData>, EditScriptEvent.HasHandlers {

	public static final String MQTT_CLIENT_ID = "clientid";
	public static final String MQTT_QOS = "qos";

	private static final String SQL_CONNECTION_STRING = "connection";
	private static final String SQL_TABLENAME = "table";
	private static final String SQL_PLAIN = "plain";

	ConfigurationSection s;

	@Path("resultUri")
	@UiField
	TextBox uri;

	@Path("transportType")
	@UiField
	ValueListBox<TransportType> type = new ValueListBox<>(new AbstractRenderer<TransportType>() {
		@Override
		public String render(TransportType object) {
			return object.name();
		}
	});

	@Path("uri.host")
	@UiField
	TextBox hostname;

	@Ignore
	@UiField
	LabelRow hostnameRow;

	@Path("uri.port")
	@UiField(provided = true)
	IntegerTextBox port = new IntegerTextBox();

	@Ignore
	@UiField
	LabelRow portRow;

	@Path("uri.path")
	@UiField
	TextBox path;

	@Ignore
	@UiField
	LabelRow pathRow;

	@Path("uri.query")
	@UiField
	TextBox query;

	@Ignore
	@UiField
	LabelRow queryRow;

	@Path("uri.fragment")
	@UiField
	TextBox fragment;

	@Ignore
	@UiField
	LabelRow fragmentRow;

	@Path("uri.userInfo")
	@UiField
	TextBox userInfo;

	@Path("properties")
	@UiField
	PropertiesEditor properties;

	@Ignore
	@UiField
	LabelRow userInfoRow;

	@Ignore
	@UiField
	TextBox clientid;

	@Ignore
	@UiField
	LabelRow clientidRow;

	@Ignore
	@UiField
	ListBox qos;

	@Ignore
	@UiField
	LabelRow qosRow;

	@Ignore
	@UiField
	TextBox topic;

	@Ignore
	@UiField
	LabelRow topicRow;

	@Ignore
	@UiField
	TextBox sqlConnectionString;

	@Ignore
	@UiField
	LabelRow sqlConnectionStringRow;

	@Ignore
	@UiField
	TextBox sqlTablename;

	@Ignore
	@UiField
	LabelRow sqlTablenameRow;

	@Ignore
	@UiField
	TextBox sqlPlain;

	@Ignore
	@UiField
	LabelRow sqlPlainRow;

	@Ignore
	@UiField
	TextArea sqlColumnMappingList;

	@Ignore
	@UiField
	LabelRow sqlColumnMappingListRow;

	@Ignore
	@UiField
	LabelRow azureConnectionStringRow;

	@Ignore
	@UiField
	TextArea azureConnectionString;

	ResourceBundle res = ResourceBundle.INSTANCE;

	private TransportData transportData;
	private EditorDelegate<TransportData> delegate;

	private static SubscriberEditorUiBinder uiBinder = GWT.create(SubscriberEditorUiBinder.class);

	interface SubscriberEditorUiBinder extends UiBinder<Widget, TransportEditor> {
	}

	public TransportEditor() {
		port.setRenderer(Util.getIntegerRenderer());
		port.setParser(Util.getIntegerParser());
		initWidget(uiBinder.createAndBindUi(this));
		SelectElement selectElement = qos.getElement().cast();
		selectElement.getOptions().getItem(0).setDisabled(true);
		hideRows();
		type.setValue(TransportType.CUSTOM);
		showRows(type.getValue());
	}

	private void hideRows() {
		hostnameRow.setVisible(false);
		portRow.setVisible(false);
		pathRow.setVisible(false);
		queryRow.setVisible(false);
		fragmentRow.setVisible(false);
		userInfoRow.setVisible(false);

		clientidRow.setVisible(false);
		qosRow.setVisible(false);
		topicRow.setVisible(false);

		sqlConnectionStringRow.setVisible(false);
		sqlTablenameRow.setVisible(false);
		sqlPlainRow.setVisible(false);
		sqlColumnMappingListRow.setVisible(false);

		azureConnectionStringRow.setVisible(false);
		azureConnectionString.setText("");
	}

	@UiHandler("type")
	void onChange(ValueChangeEvent<TransportType> event) {
		TransportType currentType = type.getValue();

		showRows(currentType);
		properties.subscriberTypeChanged(currentType);
		fireValueChangeEvent();
	}

	@UiHandler({ "hostname", "path", "query", "fragment", "userInfo" })
	public void onValueChange(ValueChangeEvent<String> event) {
		fireValueChangeEvent();
	}

	@UiHandler("port")
	public void onChangePort(ValueChangeEvent<Integer> event) {
		fireValueChangeEvent();
	}

	@UiHandler("port")
	public void onChangePort(ChangeEvent event) {
		fireValueChangeEvent();
	}

	@UiHandler("port")
	public void onInput(KeyPressEvent event) {
		char input = event.getCharCode();
		if (input == '.') {
			port.cancelKey();
			event.getNativeEvent().preventDefault();
		} else {
			switch (event.getNativeEvent().getKeyCode()) {
			case KeyCodes.KEY_TAB:
			case KeyCodes.KEY_BACKSPACE:
			case KeyCodes.KEY_DELETE:
			case KeyCodes.KEY_LEFT:
			case KeyCodes.KEY_RIGHT:
			case KeyCodes.KEY_UP:
			case KeyCodes.KEY_DOWN:
			case KeyCodes.KEY_END:
			case KeyCodes.KEY_ENTER:
			case KeyCodes.KEY_ESCAPE:
			case KeyCodes.KEY_PAGEDOWN:
			case KeyCodes.KEY_PAGEUP:
			case KeyCodes.KEY_HOME:
			case KeyCodes.KEY_SHIFT:
			case KeyCodes.KEY_ALT:
			case KeyCodes.KEY_CTRL:
				break;
			default:
				if (event.isAltKeyDown() || (event.isControlKeyDown() && (event.getCharCode() != 'v' && event.getCharCode() != 'V')))
					break;
				if (!Character.isDigit(input) || port.getValue() > 99999) {
					port.cancelKey();
					event.getNativeEvent().preventDefault();
				}
			}
		}
	}

	private void fireValueChangeEvent() {
		ValueChangeEvent.fire(this, transportData);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<TransportData> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@UiHandler("topic")
	public void onTopicKeyUp(ValueChangeEvent<String> event) {
		String text = topic.getText();

		if (text != null) {
			String ttext = text.startsWith("/") ? text : ("/" + text);
			path.setText(ttext);
			onValueChange(event);
		}
	}

	@UiHandler({ "clientid" })
	public void onClientIdKeyUp(ValueChangeEvent<String> event) {
		String qos = this.qos.getSelectedValue();
		String clientId = this.clientid.getText();

		clientId = (clientId != null) && (clientId.trim().length() > 0) ? ("clientid=" + clientId) : "";

		int qosN = Integer.parseInt(qos);

		qos = (qosN >= 0) ? ("qos=" + qos) : "";

		String pAnd = (clientId.trim().length() > 0 ? "&" : "");
		String query = clientId + pAnd + qos;

		int end = query.length() - 1 >= 0 ? query.length() - 1 : 0;

		query = !query.endsWith("&") ? query : query.substring(0, end);

		this.query.setText(query);
		onValueChange(event);
	}

	@UiHandler("qos")
	public void onQoSSelectionChanged(ChangeEvent event) {
		onClientIdKeyUp(null);
	}

	@UiHandler({ "sqlConnectionString", "sqlTablename", "sqlPlain", "sqlColumnMappingList" })
	public void onTablePlainColumnMappingListKeyUp(ValueChangeEvent<String> event) {
		String connection = this.sqlConnectionString.getText();
		String table = this.sqlTablename.getText();
		String plain = this.sqlPlain.getText();
		String columnMappingList = genColumnMappingList();

		if (connection != null && connection.trim().length() > 0) {
			connection = "connection=" + URL.encodeQueryString(connection);
		} else {
			connection = "";
		}

		table = (table != null) && (table.trim().length() > 0) ? ("table=" + URL.encodeQueryString(table)) : "";
		plain = (plain != null) && (plain.trim().length() > 0) ? ("plain=" + URL.encodeQueryString(plain)) : "";

		String cAnd = (connection.trim().length() > 0 ? "&" : "");
		String tAnd = (table.trim().length() > 0 ? "&" : "");
		String pAnd = (plain.trim().length() > 0 ? "&" : "");
		String query = connection + cAnd + table + tAnd + plain + pAnd + columnMappingList;

		int end = query.length() - 1 >= 0 ? query.length() - 1 : 0;

		query = !query.endsWith("&") ? query : query.substring(0, end);

		this.query.setText(query);
		onValueChange(event);
	}

	@UiHandler("azureConnectionString")
	public void onAzureConnectionStringChange(ValueChangeEvent<String> event) {
		String query = "";
		String azureConnectionString = this.azureConnectionString.getText();

		if (azureConnectionString != null && azureConnectionString.length() > 0) {
			query = azureConnectionString;
		}

		this.query.setText(query);
		onValueChange(event);
	}

	private void setMqttParameter(Uri uri) {
		Map<String, String> params = splitQuery(uri);
		String topic = uri.getPath();
		String clientid = params.get(MQTT_CLIENT_ID);
		String qos = params.get(MQTT_QOS);
		int qosN;

		try {
			qosN = Integer.parseInt(qos) + 1;
		} catch (NumberFormatException nfe) {
			qosN = 0;
		}

		this.topic.setText(topic);
		this.qos.setSelectedIndex(qosN);
		this.clientid.setText(clientid);
	}

	private void setSqlParameter(Uri uri) {
		Map<String, String> params = splitQuery(uri);
		String connectionString = params.get(SQL_CONNECTION_STRING);
		String plain = params.get(SQL_PLAIN);
		String tablename = params.get(SQL_TABLENAME);

		if (connectionString != null && connectionString.length() > 0) {
			connectionString = URL.decodeQueryString(connectionString);
		}
		this.sqlConnectionString.setText(connectionString);
		this.sqlPlain.setText(plain);
		this.sqlTablename.setText(tablename);

		String sqlColumnMappingList = "";

		for (Map.Entry<String, String> keyValue : params.entrySet()) {

			boolean equalsSqlConnectionString = SQL_CONNECTION_STRING.equals(keyValue.getKey());
			boolean equalsSqlPlain = SQL_PLAIN.equals(keyValue.getKey());
			boolean equalsTablename = SQL_TABLENAME.equals(keyValue.getKey());

			if (!equalsSqlConnectionString && !equalsSqlPlain && !equalsTablename) {
				String attrName = keyValue.getKey();
				String attrValue = keyValue.getValue();

				sqlColumnMappingList += (attrValue != null) && (attrValue.trim().length() > 0) ? (attrName + "=" + attrValue + "\n") : "";
			}
		}

		this.sqlColumnMappingList.setText(sqlColumnMappingList);
	}

	private Map<String, String> splitQuery(Uri uri) {
		final Map<String, String> query_pairs = new HashMap<String, String>();
		String query = uri.getRawQuery();
		if (query != null) {
			final String[] pairs = query.split("&");
			for (String pair : pairs) {
				final int idx = pair.indexOf("=");
				final String key = idx > 0 ? pair.substring(0, idx) : pair;
				final String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
				query_pairs.put(key, value);
			}
		}
		return query_pairs;
	}

	private String genColumnMappingList() {
		StringBuilder result = new StringBuilder();
		String text = sqlColumnMappingList.getText();
		if (text != null) {
			String[] lines = text.split("\n");
			for (String line : lines) {
				if (result.length() > 0)
					result.append('&');
				int index = line.indexOf('=');
				if (index > -1)
					result.append(line.substring(0, index)).append('=').append(URL.encodeQueryString(line.substring(index + 1)));
				else
					result.append(line);
			}
		}
		return result.toString();
	}

	private void setAzureParameter(Uri uri) {
		this.azureConnectionString.setText(uri.getHost());
	}

	private void showRows(TransportType transportType) {
		this.uri.setReadOnly(true);
		hideRows();
		switch (transportType) {
		case FILE:
			pathRow.setVisible(true);
			break;
		case HTTP:
		case HTTPS:
			hostnameRow.setVisible(true);
			portRow.setVisible(true);
			pathRow.setVisible(true);
			queryRow.setVisible(true);
			fragmentRow.setVisible(true);
			userInfoRow.setVisible(true);
			break;
		case MQTT:
		case MQTTS:
			hostnameRow.setVisible(true);
			portRow.setVisible(true);
			userInfoRow.setVisible(true);
			clientidRow.setVisible(true);
			qosRow.setVisible(true);
			topicRow.setVisible(true);
			break;
		case SQL:
			sqlConnectionStringRow.setVisible(true);
			sqlTablenameRow.setVisible(true);
			sqlPlainRow.setVisible(true);
			sqlColumnMappingListRow.setVisible(true);
			break;
		case TCP:
		case UDP:
			hostnameRow.setVisible(true);
			portRow.setVisible(true);
			break;
		case AZURE:
			azureConnectionStringRow.setVisible(true);
			break;
		case JDBC:
		case CUSTOM:
			this.uri.setReadOnly(false);
			break;
		default:
			break;
		}
	}

	public void setTypes(List<TransportType> types) {
		type.setAcceptableValues(types);
	}

	@Override
	public void setDelegate(EditorDelegate<TransportData> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void flush() {
		if (Util.isNullOrEmpty(path.getValue()))
			transportData.getUri().setPath("/");
		else if (!path.getValue().startsWith("/")) {
			transportData.getUri().setPath("/" + path.getValue());
		}
		if (delegate != null) {
			if (Util.isNullOrEmpty(uri.getValue())) {
				delegate.recordError("URI cannot be empty or null!", uri.getValue(), uri);
				return;
			}
		}
	}

	@Override
	public void onPropertyChange(String... paths) {
	}

	@Override
	public void setValue(TransportData value) {
		this.transportData = value;
		Uri uri = value.getUri();
		setSqlParameter(uri);
		setMqttParameter(uri);
		showRows(transportData.getTransportType());
		setAzureParameter(uri);
	}

	public void setUri(String uri) {
		this.uri.setValue(uri, true);
	}

	@Override
	public HandlerRegistration addEditScriptHandler(Handler handler) {
		return addHandler(handler, EditScriptEvent.getType());
	}
}