package havis.transport.ui.client.editor;

import havis.net.ui.shared.client.event.DialogCloseEvent;
import havis.net.ui.shared.client.table.CreateRowEvent;
import havis.net.ui.shared.client.table.CustomTable;
import havis.net.ui.shared.client.table.DeleteRowEvent;
import havis.net.ui.shared.client.widgets.Util;
import havis.transport.ui.client.TransportType;
import havis.transport.ui.client.event.EditScriptEvent;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.editor.client.adapters.EditorSource;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class PropertiesEditor extends Composite implements ValueAwareEditor<List<TransportProperty>> {

	private EditorDelegate<List<TransportProperty>> delegate;
	private TransportType lastTransportType = TransportType.CUSTOM;

	@UiField
	CustomTable properties;

	@Path("")
	ListEditor<TransportProperty, PropertyEditor> editor;

	private class PropertyEditorSource extends EditorSource<PropertyEditor> {

		@Override
		public PropertyEditor create(int index) {
			final PropertyEditor prpEditor = new PropertyEditor();
			prpEditor.addEditScriptHandler(new EditScriptEvent.Handler() {

				@Override
				public void onEditScript(EditScriptEvent event) {
					final ScriptEditor scriptEditor = new ScriptEditor(event.getScript(), event.getPlaceholder());

					scriptEditor.addEditorCloseHandler(new DialogCloseEvent.Handler() {

						@Override
						public void onDialogClose(DialogCloseEvent event) {

							if (event.isAccept()) {
								prpEditor.setValue(scriptEditor.getScript());
							}
							RootLayoutPanel.get().remove(scriptEditor);
							scriptEditor.setVisible(false);
						}
					});

					RootLayoutPanel.get().add(scriptEditor);
					scriptEditor.setVisible(true);
				}
			});

			prpEditor.setValueKeyDownHandler(new KeyDownHandler() {

				@Override
				public void onKeyDown(KeyDownEvent event) {
					// Tab handling for entering values into the properties
					// table
					if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
						event.preventDefault();
						event.stopPropagation();
						PropertyEditor pe = null;
						int index = editor.getList().size() - 1;
						if (index < 0)
							return;

						pe = (PropertyEditor) properties.getRow(index);
						// block inserting if already an empty row exist
						if (pe != null && !Util.isNullOrEmpty(pe.getName())) {
							onCreateRow(null);
							index = editor.getList().size() - 1;
							if (index < 0)
								return;
							pe = (PropertyEditor) properties.getRow(index);
							pe.setStartFocus();
						}
					}

				}
			});

			properties.addRow(prpEditor);
			return prpEditor;
		}

		@Override
		public void dispose(PropertyEditor subEditor) {
			properties.deleteRow(subEditor);
		}
	}

	private static PropertySectionUiBinder uiBinder = GWT.create(PropertySectionUiBinder.class);

	interface PropertySectionUiBinder extends UiBinder<Widget, PropertiesEditor> {
	}

	@UiHandler("properties")
	void onCreateRow(CreateRowEvent event) {
		editor.getList().add(new TransportProperty());
		subscriberTypeChanged(lastTransportType);
	}

	@UiHandler("properties")
	void onDeleteRow(final DeleteRowEvent event) {
		editor.getList().remove(event.getIndex());
	}

	public PropertiesEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		editor = ListEditor.of(new PropertyEditorSource());
		properties.setHeader(Arrays.asList("Name", "Value"));
	}

	@Override
	public void setDelegate(EditorDelegate<List<TransportProperty>> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void flush() {
		int i = 0;
		while (i < editor.getList().size()) {
			TransportProperty p = editor.getList().get(i);
			if (Util.isNullOrEmpty(p.getName())) {
				editor.getList().remove(i);
			} else {
				if (Util.isNullOrEmpty(p.getValue())) {
					delegate.recordError("Value cannot be empty or null for property '" + p.getName() + "'!", p.getName(), p);
				}
				++i;
			}
		}

		editor.flush();
	}

	@Override
	public void onPropertyChange(String... paths) {
		editor.flush();
	}

	@Override
	public void setValue(List<TransportProperty> value) {
	}

	public void subscriberTypeChanged(TransportType currentType) {
		lastTransportType = currentType;
		for (PropertyEditor propertyEditor : editor.getEditors()) {
			propertyEditor.subscriberTypeChanged(currentType);
		}
	}
}
