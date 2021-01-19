package havis.transport.ui.client.script;

import havis.transport.ui.res.AppResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ScriptTextArea extends Composite implements LeafValueEditor<String> {
	@UiField
	TextArea script;
	@UiField
	Label clickButton;

	private AppResources appRes = AppResources.INSTANCE;
	private static ScriptTextAreaUiBinder uiBinder = GWT.create(ScriptTextAreaUiBinder.class);

	interface ScriptTextAreaUiBinder extends UiBinder<Widget, ScriptTextArea> {
	}

	private void ensureInjection() {
		appRes.css().ensureInjected();
	}

	public ScriptTextArea() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		ensureInjection();
	}

	@Override
	public void setValue(String value) {
		setValue(value, false);
	}

	public void setValue(String value, boolean fireEvent) {
		boolean changed = !value.equals(script.getValue());

		script.setValue(value, fireEvent);

		if (fireEvent && changed) {
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), script);
		}
	}
	
	@Override
	public String getValue() {
		return this.script.getValue();
	}
}
