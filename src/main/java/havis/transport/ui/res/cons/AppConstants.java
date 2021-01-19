package havis.transport.ui.res.cons;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.Constants;

public interface AppConstants extends Constants {

	public static final AppConstants INSTANCE = GWT.create(AppConstants.class);

	String header();
	String type();
	String host();
	String port();
	String topic();
	String path();
	String query();
	String clientid();
	String qualityOfService();
	String qos();
	String qosAtMostOnce();
	String qosAtLeastOnce();
	String qosExactlyOnce();
	String fragment();
	String subscriberUserInfo();
	String connectionString();
	String tablename();
	String plain();
	String columnMappingList();
	String uri();
	String expression();
	String expressionPlaceholder();
	String optional();
	String initstatement();
	String initStatementPlaceholder();
	String truetext();
	String falsetext();
	String storage();
	String clear();
	String drop();
	String keepconnection();
	String avoidduplicates();
}