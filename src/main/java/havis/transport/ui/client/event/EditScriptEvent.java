package havis.transport.ui.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class EditScriptEvent extends GwtEvent<EditScriptEvent.Handler> {

	public interface Handler extends EventHandler {
		void onEditScript(EditScriptEvent event);
	}

	public interface HasHandlers {
		HandlerRegistration addEditScriptHandler(EditScriptEvent.Handler handler);
	}

	private static final Type<EditScriptEvent.Handler> TYPE = new Type<>();
	private String script;
	private String placeholder;

	public EditScriptEvent(String script) {
		this(script, null);
	}

	public EditScriptEvent(String script, String placeholder) {
		super();
		this.script = script;
		this.placeholder = placeholder;
	}

	public String getScript() {
		return script;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	@Override
	public Type<EditScriptEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EditScriptEvent.Handler handler) {
		handler.onEditScript(this);
	}

	public static Type<EditScriptEvent.Handler> getType() {
		return TYPE;
	}
}
