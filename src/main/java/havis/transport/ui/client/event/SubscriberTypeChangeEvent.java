package havis.transport.ui.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class SubscriberTypeChangeEvent extends GwtEvent<SubscriberTypeChangeEvent.Handler> {

	public interface Handler extends EventHandler {
		void onSubscriberTypeChange(SubscriberTypeChangeEvent event);
	}

	public interface HasHandlers {
		HandlerRegistration addSubscriberTypeChangeHandler(SubscriberTypeChangeEvent.Handler handler);
	}

	private static final Type<SubscriberTypeChangeEvent.Handler> TYPE = new Type<>();

	private String subscriberType;

	public String getSubscriberType() {
		return subscriberType;
	}

	public SubscriberTypeChangeEvent(String subscriberType) {
		super();
		this.subscriberType = subscriberType;
	}

	@Override
	public Type<SubscriberTypeChangeEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SubscriberTypeChangeEvent.Handler handler) {
		handler.onSubscriberTypeChange(this);
	}

	public static Type<SubscriberTypeChangeEvent.Handler> getType() {
		return TYPE;
	}
}
