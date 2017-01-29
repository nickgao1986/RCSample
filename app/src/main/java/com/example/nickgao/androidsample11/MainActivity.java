package com.example.nickgao.androidsample11;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.nickgao.R;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsProvider;
import com.example.nickgao.database.GeneralSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.network.RestSession;
import com.example.nickgao.network.RestSessionState;
import com.example.nickgao.network.RestSessionStateChange;
import com.example.nickgao.service.ServiceFactory;
import com.example.nickgao.service.clientinfo.ClientInfoService;
import com.example.nickgao.service.contact.ExtensionService;

public class MainActivity extends Activity {

	public static final long AUTH_MAIL_BOXID_KEY = Long.MIN_VALUE;
	public static final String LOGIN_NUMBER = "16504253931";
	public static final String EXT = "5801";
	public static final String PASSWORD = "Test!123";
	RestNotificationReceiver mRestNotificationReceiver;
	private static final String TAG = "[RC]MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button btn = (Button)findViewById(R.id.btn);
		btn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this,ContactsActivity.class);
				startActivity(intent);
			}
		});
		restAuthorization();

		mRestNotificationReceiver = new RestNotificationReceiver();

		IntentFilter restIntentFilterPhoneState = new IntentFilter();
		restIntentFilterPhoneState
				.addAction(RestSessionStateChange.REST_SESSION_STATE_CHANGE_NOTIFICATION);
		registerReceiver(mRestNotificationReceiver, restIntentFilterPhoneState);



	}

	private class RestNotificationReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (context == null || intent == null) {
				return;
			}

			if (intent
					.getAction()
					.equals(RestSessionStateChange.REST_SESSION_STATE_CHANGE_NOTIFICATION)) {
				RestSessionStateChange restSessionStateChange = intent
						.getParcelableExtra(RestSessionStateChange.REST_SESSION_STATE_CHANGE_TAG);
				RestSessionState mState = restSessionStateChange.getState();
				if (mState == RestSessionState.AUTHORIZED) {
					long mailboxId = GeneralSettings.getSettings()
							.getCurrentMailboxId();
					MktLog.d(TAG, "==mailboxId=" + mailboxId);
					ContactsProvider.getInstance().start(mailboxId);

					ExtensionService extensionService = (ExtensionService) ServiceFactory.getInstance().getService(ExtensionService.class.getName());
					extensionService.updateExtension(context);

					restClientInfo();
				}
			}
		}
	}

	private void restClientInfo() {
		ClientInfoService service = (ClientInfoService) ServiceFactory
				.getInstance().getService(ClientInfoService.class.getName());
		service.updateClientInfo();
	}

	private void restAuthorization() {
		long currentMailId = AUTH_MAIL_BOXID_KEY;
		RestSession session = RestSession.createSession(currentMailId);
		boolean sent = session.authorize(LOGIN_NUMBER, EXT, PASSWORD);
	}

}
