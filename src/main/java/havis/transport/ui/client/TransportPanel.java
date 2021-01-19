package havis.transport.ui.client;

import havis.net.rest.shared.async.NetServiceAsync;
import havis.net.rest.shared.data.Uri;
import havis.transport.ui.client.editor.TransportEditor;
import havis.transport.ui.client.editor.TransportProperty;
import havis.transport.ui.client.event.SaveTransportEvent;
import havis.transport.ui.client.event.TransportErrorEvent;
import havis.transport.ui.client.event.TransportErrorEvent.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.TextCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;

public class TransportPanel extends Composite implements SaveTransportEvent.HasHandlers, TransportErrorEvent.HasHandlers {

	private TransportEditor editor = new TransportEditor();
	private TransportData transportData;
	private NetServiceAsync service = GWT.create(NetServiceAsync.class);
	private Driver driver = GWT.create(Driver.class);
	private Throwable lastException;
	private boolean parsing;
	private int retries = 0;

	interface Driver extends SimpleBeanEditorDriver<TransportData, TransportEditor> {
	}

	public void setTypes(List<TransportType> types) {
		ArrayList<TransportType> sortedTypes = new ArrayList<>(types);
		Collections.sort(sortedTypes);
		editor.setTypes(sortedTypes);
	}

	public void setData(final String uri, final Map<String, String> properties) {
		setData(uri, properties, null);
	}

	public void setData(final String uri, final Map<String, String> properties, final String suffix) {
		service.getHostname(new TextCallback() {
			@Override
			public void onSuccess(Method method, String hostname) {
				if (hostname == null || hostname.isEmpty()) {
					hostname = "localhost";
				}
				String defaultUri = uri;
				if (uri == null || uri.isEmpty()) {
					String topic = hostname;
					if (suffix != null && !suffix.isEmpty()) {
						topic += "/" + suffix;
					}
					defaultUri = "mqtt://MQTT:1883/" + topic + "?qos=0&clientid=" + hostname;
				} 
				final String copyUri = defaultUri;
				driver.initialize(editor);
				service.parseUri(copyUri, new MethodCallback<Uri>() {
					@Override
					public void onFailure(Method method, Throwable exception) {
						lastException = exception;
					}

					@Override
					public void onSuccess(Method method, Uri response) {
						lastException = null;
						List<TransportProperty> propertyList = new ArrayList<>();
						if (properties != null) {
							for (Map.Entry<String, String> property : properties.entrySet()) {
								propertyList.add(new TransportProperty(property.getKey(), property.getValue()));
							}
						}
						transportData = new TransportData(TransportType.valueByScheme(response.getScheme()), response, propertyList, copyUri);
						driver.edit(transportData);
					}
				});

				editor.addValueChangeHandler(new ValueChangeHandler<TransportData>() {
					@Override
					public void onValueChange(ValueChangeEvent<TransportData> event) {
						TransportType oldTransport = transportData.getTransportType();

						TransportData transportDataCurrent = driver.flush();
						TransportType newTransport = transportDataCurrent.getTransportType();
						if (oldTransport == newTransport) {
							if (newTransport == TransportType.SQL) {
								String uri = transportDataCurrent.getUri().getScheme() + ":///?" + transportDataCurrent.getUri().getQuery();
								editor.setUri(uri);
							} else if (newTransport == TransportType.AZURE) {
								String uri = TransportType.AZURE.getTemplate();
								String query = transportDataCurrent.getUri().getQuery();

								if (query != null && query.length() > 0) {
									uri += query;
								}

								editor.setUri(uri);
							} else {
								if (newTransport != TransportType.JDBC)
									printUri(newTransport == TransportType.MQTT || newTransport == TransportType.MQTTS);
							}
						} else {
							if (newTransport == TransportType.MQTT && uri == null) {
								parseUri(copyUri);
							} else if (newTransport != TransportType.CUSTOM && newTransport != TransportType.JDBC) {
								// Handling first opening of azure type
								if (newTransport == TransportType.AZURE) {
									// Formatting azure uri to be a valid uri
									String uri = newTransport.getTemplate();
									parseUri(uri.substring(0, uri.length() - 1));
								} else {
									parseUri(newTransport.getTemplate());
								}
							}
						}
					}

				});

			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				lastException = exception;

			}
		});
	}

	private void flushData() {
		driver.flush();
		if (lastException != null) {
			fireEvent(new TransportErrorEvent(lastException));
		}
		if (driver.hasErrors()) {
			String error = "";
			for (EditorError e : driver.getErrors()) {
				error += e.getMessage() + '\n';
			}
			fireEvent(new TransportErrorEvent(error));
		} else {
			Map<String, String> properties = new HashMap<>();
			for (TransportProperty property : transportData.getProperties()) {
				properties.put(property.getName(), property.getValue());
			}
			fireEvent(new SaveTransportEvent(transportData.getResultUri(), properties));
		}
	}

	public void saveTransportData() {
		new Timer() {
			@Override
			public void run() {
				if (!parsing) {
					cancel();
					flushData();
				}
				if (retries++ >= 100) {
					cancel();
					parsing = false;
				}
			}
		}.scheduleRepeating(10);
	}

	private void parseUri(String uri) {
		service.parseUri(uri, new MethodCallback<Uri>() {

			@Override
			public void onSuccess(Method method, Uri response) {
				lastException = null;
				transportData.setUri(response);
				printUri(false);
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				lastException = exception;
			}
		});
	}

	private void printUri(final boolean parse) {
		parsing = true;
		service.printUri(transportData.getUri(), new TextCallback() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				lastException = exception;
			}

			@Override
			public void onSuccess(Method method, String response) {
				if (parse) {
					parseUri(response);
				} else {
					lastException = null;
					transportData.setResultUri(response);
					driver.edit(transportData);
					parsing = false;
				}
			}
		});
	}

	public TransportPanel() {
		initWidget(editor);
		driver.initialize(editor);
	}

	@Override
	public HandlerRegistration addSaveTransportHandler(SaveTransportEvent.Handler handler) {
		return addHandler(handler, SaveTransportEvent.getType());
	}

	@Override
	public HandlerRegistration addTransportErrorHandler(Handler handler) {
		return addHandler(handler, TransportErrorEvent.getType());
	}
}
