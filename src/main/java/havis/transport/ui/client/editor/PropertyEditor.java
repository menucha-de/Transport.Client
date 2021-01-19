package havis.transport.ui.client.editor;

import havis.net.ui.shared.client.table.CustomWidgetRow;
import havis.net.ui.shared.client.widgets.CustomListBox;
import havis.net.ui.shared.client.widgets.CustomRenderer;
import havis.net.ui.shared.client.widgets.CustomSuggestBox;
import havis.net.ui.shared.resourcebundle.ResourceBundle;
import havis.transport.ui.client.TransportType;
import havis.transport.ui.client.data.Property;
import havis.transport.ui.client.data.PropertyGroup;
import havis.transport.ui.client.event.EditScriptEvent;
import havis.transport.ui.client.event.EditScriptEvent.Handler;
import havis.transport.ui.client.event.SubscriberTypeChangeEvent;
import havis.transport.ui.client.script.ScriptTextArea;
import havis.transport.ui.res.cons.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditor extends CustomWidgetRow implements ValueAwareEditor<TransportProperty>, EditScriptEvent.HasHandlers,
		SubscriberTypeChangeEvent.HasHandlers {
	CustomSuggestBox<String> name = new CustomSuggestBox<String>(CustomSuggestBox.getStringParser());
	@UiField(provided = true)
	CustomSuggestBox<String> value = new CustomSuggestBox<String>(CustomSuggestBox.getStringParser());

	@UiField
	ScriptTextArea scriptArea = new ScriptTextArea();

	@UiField
	FlowPanel valueGroup;

	private TransportProperty tpValue;
	private static PropertyEditorUiBinder uiBinder = GWT.create(PropertyEditorUiBinder.class);
	private final static AppConstants CONSTANTS = AppConstants.INSTANCE;

	interface PropertyEditorUiBinder extends UiBinder<Widget, PropertyEditor> {
	}

	public PropertyEditor() {
		uiBinder.createAndBindUi(this);

		// Set renderer to show label instead of name in the listbox
		name.getListBox().setRenderer(new CustomRenderer<String>() {

			@Override
			public String render(String value) {
				if (Property.PROPERTIES.containsKey(value)) {
					return Property.PROPERTIES.get(value).getLabel();
				}
				return value;
			}
		});

		// Adding predefined properties to "name" listBox
		for (PropertyGroup propertyGroup : Property.PROPERTY_GROUPS) {
			String currentGroupName = propertyGroup.getName();
			CustomListBox<String> nameListBox = name.getListBox();

			if (!currentGroupName.equals("<JDBC>")) {
				for (Property property : propertyGroup.getProperties()) {
					// if property is a common one
					if (currentGroupName.isEmpty()) {
						nameListBox.addItem(property.getName());
					} else {
						nameListBox.addItem(property.getName(), propertyGroup.getName());
					}
				}
			}
		}

		// prepare nameSuggestBox
		name.addChangeHandler(changeHandler);
		name.getListBox().setStyleName(ResourceBundle.INSTANCE.css().webuiCustomTableListBox());
		name.setAddTextBoxStyleNames(ResourceBundle.INSTANCE.css().webuiCustomTableTextBox());
		addColumn(name);

		// prepare valueSuggestBox
		value.getListBox().setStyleName(ResourceBundle.INSTANCE.css().webuiCustomTableListBox());
		value.setAddTextBoxStyleNames(ResourceBundle.INSTANCE.css().webuiCustomTableTextBox());
		value.getListBox().setVisibility(Visibility.HIDDEN);
		valueGroup.add(value);

		scriptArea.addDomHandler(valueClickHandler, ClickEvent.getType());
		valueGroup.add(scriptArea);

		addColumn(valueGroup);
	}

	private ClickHandler valueClickHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (name.getValue().equals(Property.TRANSFORMER_JAVASCRIPT_SCRIPT)) {
				fireEvent(new EditScriptEvent(scriptArea.getValue()));
			} else if (name.getValue().equals(Property.DATA_CONVERTER_EXPRESSION_PROPERTY)) {
				fireEvent(new EditScriptEvent(scriptArea.getValue(), CONSTANTS.expressionPlaceholder()));
			}
		}
	};

	private ChangeHandler changeHandler = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			String nameValue = name.getValue();

			if (Property.PROPERTIES.containsKey(nameValue)) {
				if (nameValue.equals(Property.TRANSFORMER_JAVASCRIPT_SCRIPT) || nameValue.equals(Property.DATA_CONVERTER_EXPRESSION_PROPERTY)) {
					value.setVisible(false);
					scriptArea.setVisible(true);
				} else if (nameValue.equals(Property.JDBC_INIT_STATEMENT)) {
					value.setPlaceholder(CONSTANTS.initStatementPlaceholder());
				} else if (Property.PROPERTIES.get(nameValue).getValues() != null) {
					value.setVisible(true);
					scriptArea.setVisible(false);

					value.getListBox().setItems(Property.PROPERTIES.get(nameValue).getValues());
					value.getListBox().getElement().getStyle().setProperty("visibility", "");
					value.setEnabled(true);
				} else {
					value.getListBox().setVisibility(Visibility.HIDDEN);
				}
			} else {
				value.getListBox().setVisibility(Visibility.HIDDEN);
			}
		}
	};

	public String getName() {
		return name.getValue();
	}

	public void setValue(String value) {
		this.value.setValue(value);
		this.scriptArea.setValue(value);
	}

	public void setStartFocus() {
		name.setFocus(true);
	}

	public void setValueKeyUpHandler(KeyUpHandler handler) {
		value.getTextBox().addKeyUpHandler(handler);
	}

	public void setValueKeyDownHandler(KeyDownHandler handler) {
		value.getTextBox().addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addEditScriptHandler(Handler handler) {
		return addHandler(handler, EditScriptEvent.getType());
	}

	@Override
	public HandlerRegistration addSubscriberTypeChangeHandler(havis.transport.ui.client.event.SubscriberTypeChangeEvent.Handler handler) {
		return addHandler(handler, SubscriberTypeChangeEvent.getType());
	}

	@Override
	public void setDelegate(EditorDelegate<TransportProperty> delegate) {
		// ignore
	}

	@Override
	public void flush() {
		tpValue.setValue(value.getValue());
	}

	@Override
	public void onPropertyChange(String... paths) {
		// ignore
	}

	@Override
	public void setValue(TransportProperty value) {
		this.tpValue = value;
		// triggers changeEvent for this.name to ensure elements are correctly
		// initialized and displayed
		name.setValue(value.getName(), true);
		if (name.getValue().equals(Property.TRANSFORMER_JAVASCRIPT_SCRIPT) || name.getValue().equals(Property.DATA_CONVERTER_EXPRESSION_PROPERTY)) {
			scriptArea.setValue(value.getValue());
		}
	}

	public void subscriberTypeChanged(TransportType currentType) {
		name.getListBox().clear();
		for (PropertyGroup propertyGroup : Property.PROPERTY_GROUPS) {
			String currentPropertyGroup = propertyGroup.getName();

			for (Property property : propertyGroup.getProperties()) {
				// if type is JDBC add all properties
				if (currentType.equals(TransportType.JDBC)) {
					if (currentPropertyGroup.isEmpty()) {
						name.getListBox().addItem(property.getName());
					} else {
						name.getListBox().addItem(property.getName(), currentPropertyGroup);
					}
					// else dont add JDBC Properties
				} else {
					if (!currentPropertyGroup.equals(Property.JDBC_GROUP)) {
						if (currentPropertyGroup.isEmpty()) {
							name.getListBox().addItem(property.getName());
						} else {
							name.getListBox().addItem(property.getName(), currentPropertyGroup);
						}
					}
				}
			}
		}
	}
}
