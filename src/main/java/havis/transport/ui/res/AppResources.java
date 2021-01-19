package havis.transport.ui.res;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface AppResources extends ClientBundle {

	public static final AppResources INSTANCE = GWT.create(AppResources.class);

	@Source("CssResources.css")
	CssResources css();

	@Source("images/close.png")
	ImageResource close();
	
	@Source("images/footer.png")
	ImageResource clickButton();
}