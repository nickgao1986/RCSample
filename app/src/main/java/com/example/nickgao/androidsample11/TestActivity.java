package com.example.nickgao.androidsample11;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import com.example.nickgao.R;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsProvider;
import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.network.RestSession;
import com.example.nickgao.network.RestSessionState;
import com.example.nickgao.network.RestSessionStateChange;
import com.example.nickgao.service.ServiceFactory;
import com.example.nickgao.service.clientinfo.ClientInfoService;
import com.example.nickgao.service.extensioninfo.ExtensionInfoService;
import com.example.nickgao.service.i18n.I18nResources;
import com.example.nickgao.service.i18n.Language;
import com.example.nickgao.service.i18n.LanguageListService;
import com.example.nickgao.utils.RCMConstants;
import com.example.nickgao.utils.RCMMimeTypeMap;

public class TestActivity extends Activity {

	public static final long AUTH_MAIL_BOXID_KEY = Long.MIN_VALUE;
	public static final String LOGIN_NUMBER = "16504253931";
	public static final String EXT = "5801";
	public static final String PASSWORD = "Test!123";
	RestNotificationReceiver mRestNotificationReceiver;
	private static final String TAG = "[RC]TestActivity";
	private Handler mHander = new Handler();

	private static Uri mImportingUri = null;
	private static Uri mSchemeUri = null;
	private static String FILE_EXT = "";
	private static boolean isFromImporting = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button btn_main = (Button)findViewById(R.id.btn_main);
		btn_main.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {

				changeLanguage();
				updateNotification();
				mHander.postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent();
						intent.setClass(TestActivity.this,MainActivity.class);
						startActivity(intent);
					}
				},2000);
			}
		});


		Button btn_contacts = (Button)findViewById(R.id.btn_contacts);
		btn_contacts.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TestActivity.this,ContactsActivity.class);
				startActivity(intent);

				//changeLanguage();
				//new DeviceLanguageDetectFlow(TestActivity.this).startFlow();
				//updateNotification();

			}
		});

		Button btn_favorite = (Button)findViewById(R.id.btn_favorites);
		btn_favorite.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TestActivity.this,FavoriteActivity.class);
				startActivity(intent);

			}
		});

		Button btn_calendar = (Button)findViewById(R.id.btn_calendar);
		btn_calendar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TestActivity.this,CreateCalendarActivity.class);
				startActivity(intent);
			}
		});

		restAuthorization();

		mRestNotificationReceiver = new RestNotificationReceiver();

		IntentFilter restIntentFilterPhoneState = new IntentFilter();
		restIntentFilterPhoneState
				.addAction(RestSessionStateChange.REST_SESSION_STATE_CHANGE_NOTIFICATION);
		registerReceiver(mRestNotificationReceiver, restIntentFilterPhoneState);

		Intent intent = this.getIntent();
		int flags = intent.getFlags();
		if ((flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
			if (intent.getAction() != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
				if (RCMConstants.SCHEME_FILE.equals(intent.getScheme())
						|| RCMConstants.SCHEME_CONTENT.equals(intent.getScheme())) {

					String i_type = getIntent().getType();
					if (LogSettings.ENGINEERING) {
						MktLog.d(TAG, "Intent Type: " + i_type);
					}

					FILE_EXT = MimeTypeMap.getSingleton().getExtensionFromMimeType(i_type);
					if (TextUtils.isEmpty(FILE_EXT)) {
						FILE_EXT = RCMMimeTypeMap.getSingleton().getExtensionFromMimeType(i_type);
					}

					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
					isFromImporting = true;
					mImportingUri = intent.getData();
				}
			}
		}

		MktLog.i(TAG,"====isFromImporting="+isFromImporting+"FILE_EXT="+FILE_EXT);

	}


	private void updateNotification() {
		MessagesNotificationService.updateTextNotification(TestActivity.this);

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(mRestNotificationReceiver != null) {
			unregisterReceiver(mRestNotificationReceiver);
		}
	}

	private void changeLanguage() {
	//	Language mLanguage = new Language("fr", "CA", false);

		Language mLanguage = new Language("en", "US", false);
		I18nResources.getResources().changeActivityLanguage(this, mLanguage, true);
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
					long mailboxId = CurrentUserSettings.getSettings()
							.getCurrentMailboxId();
					MktLog.d(TAG, "==mailboxId=" + mailboxId);
					ContactsProvider.getInstance().start(mailboxId);

//					ExtensionService extensionService = (ExtensionService) ServiceFactory.getInstance().getService(ExtensionService.class.getName());
//					extensionService.updateExtension(context);
					languageRestRequest();


					restExtensionInfo();
					restClientInfo();
				}
			}
		}
	}

	private void languageRestRequest() {
		LanguageListService service = (LanguageListService) ServiceFactory.getInstance().getService(LanguageListService.class.getName());
		service.updateData();
	}

	private void restExtensionInfo() {
		ExtensionInfoService service = (ExtensionInfoService) ServiceFactory.getInstance().getService(ExtensionInfoService.class.getName());
		service.updateData();
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
