package havis.transport.ui.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class TransportErrorEvent extends GwtEvent<TransportErrorEvent.Handler> {

	public interface Handler extends EventHandler {
		void onTransportError(TransportErrorEvent event);
	}

	public interface HasHandlers {
		HandlerRegistration addTransportErrorHandler(TransportErrorEvent.Handler handler);
	}

	private static final Type<TransportErrorEvent.Handler> TYPE = new Type<>();

	private String errorMessage;
	private Throwable exception;

	public TransportErrorEvent(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public TransportErrorEvent(Throwable exception) {
		this.exception = exception;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public Throwable getException() {
		return exception;
	}

	public boolean isException() {
		return exception != null;
	}

	@Override
	public Type<TransportErrorEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TransportErrorEvent.Handler handler) {
		handler.onTransportError(this);
	}

	public static Type<TransportErrorEvent.Handler> getType() {
		return TYPE;
	}
}
