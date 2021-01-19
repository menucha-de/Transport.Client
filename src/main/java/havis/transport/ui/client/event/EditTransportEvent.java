package havis.transport.ui.client.event;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import havis.transport.ui.client.TransportType;

public class EditTransportEvent extends GwtEvent<EditTransportEvent.Handler> {

	public interface Handler extends EventHandler {
		void onEditTransport(EditTransportEvent event);
	}

	public interface HasHandlers {
		HandlerRegistration addEditTransportHandler(EditTransportEvent.Handler handler);
	}

	private static final Type<EditTransportEvent.Handler> TYPE = new Type<>();

	private List<TransportType> types;
	private String uri;
	private Map<String, String> properties;

	public EditTransportEvent(List<TransportType> types, String uri, Map<String, String> properties) {
		super();
		this.types = types;
		this.uri = uri;
		this.properties = properties;
	}

	public List<TransportType> getTypes() {
		return types;
	}

	public String getUri() {
		return uri;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	public Type<EditTransportEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EditTransportEvent.Handler handler) {
		handler.onEditTransport(this);
	}

	public static Type<EditTransportEvent.Handler> getType() {
		return TYPE;
	}
}
