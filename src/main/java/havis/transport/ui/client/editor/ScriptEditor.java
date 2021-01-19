package havis.transport.ui.client.editor;

import havis.net.ui.shared.client.event.DialogCloseEvent.Handler;
import havis.net.ui.shared.client.widgets.CommonEditorDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ScriptEditor extends Composite {
	@UiField
	CommonEditorDialog dialog;
	@UiField
	FlowPanel fp_header;
	@UiField
	Label lbl_title;
	@UiField
	SimplePanel sp_editor;
	@UiField
	public TextArea ta_script;

	private static ScriptEditorUiBinder uiBinder = GWT.create(ScriptEditorUiBinder.class);

	interface ScriptEditorUiBinder extends UiBinder<Widget, ScriptEditor> {
	}

	public ScriptEditor(String script, String placeholder) {
		initWidget(uiBinder.createAndBindUi(this));
		styleDialogInnards();

		lbl_title.setText("Script");
		fp_header.clear();
		fp_header.add(lbl_title);
		ta_script.setVisibleLines(3);
		ta_script.setCharacterWidth(1);
		ta_script.getElement().setAttribute("spellcheck", "false");
		ta_script.addKeyDownHandler(keyDownHandler);
		ta_script.setText(script);
		
		if(placeholder != null){
			ta_script.getElement().setAttribute("placeholder", placeholder);
			ta_script.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(ta_script.getValue().isEmpty()){
						ta_script.setValue(ta_script.getElement().getAttribute("placeholder"));
					}
				}
			});
		}
	}

	public String getScript() {
		return ta_script.getText();
	}

	public void addEditorCloseHandler(Handler handler) {
		dialog.addDialogCloseHandler(handler);
	}

	private KeyDownHandler keyDownHandler = new KeyDownHandler() {

		@Override
		public void onKeyDown(KeyDownEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
				event.preventDefault();

				if (event.getSource() instanceof TextArea) {
					TextArea ta = (TextArea) event.getSource();
					int index = ta.getCursorPos();
					String text = ta.getText();
					ta.setText(text.substring(0, index) + "\t" + text.substring(index));
					ta.setCursorPos(index + 1);
				}
			}
		}
	};

	private void styleDialogInnards() {
		Element dialogBox = dialog.getElement().getFirstChildElement();

		// Style visible dialog box
		Style dialogBoxStyle = dialogBox.getStyle();
		dialogBoxStyle.setProperty("maxWidth", "47em");
		dialogBoxStyle.setProperty("height", "auto");
		dialogBoxStyle.setWidth(792.0, Unit.PX);

		Element textAreaWrapper = dialogBox.getFirstChildElement().getNextSiblingElement().getFirstChildElement().getNextSiblingElement();
		textAreaWrapper.getStyle().setPaddingBottom(0, Unit.EM);

		Element applyButton = dialogBox.getFirstChildElement().getNextSiblingElement().getNextSiblingElement().getFirstChildElement();
		applyButton.getStyle().setFontSize(1.5, Unit.EM);
		applyButton.getStyle().setColor("black");
		applyButton.getStyle().setMarginBottom(0.5, Unit.EM);
		applyButton.getStyle().setMarginTop(0.5, Unit.EM);
	}
}
