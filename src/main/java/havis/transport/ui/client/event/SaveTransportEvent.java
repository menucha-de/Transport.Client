package havis.transport.ui.client.event;

import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class SaveTransportEvent extends GwtEvent<SaveTransportEvent.Handler> {

	public interface Handler extends EventHandler {
		void onSaveTransport(SaveTransportEvent event);
	}

	public interface HasHandlers {
		HandlerRegistration addSaveTransportHandler(SaveTransportEvent.Handler handler);
	}

	private static final Type<SaveTransportEvent.Handler> TYPE = new Type<>();

	private String uri;
	private Map<String, String> properties;

	public SaveTransportEvent(String uri, Map<String, String> properties) {
		super();
		this.uri = uri;
		this.properties = properties;
	}

	public String getUri() {
		return uri;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	public Type<SaveTransportEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SaveTransportEvent.Handler handler) {
		handler.onSaveTransport(this);
	}

	public static Type<SaveTransportEvent.Handler> getType() {
		return TYPE;
	}
}
