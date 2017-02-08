package com.example.nickgao.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.androidsample11.RingCentralMain;
import com.example.nickgao.fragment.RCListFragment;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.utils.RCMConstants;
import com.example.nickgao.utils.SearchUtil;
import com.example.nickgao.utils.SuperSearching;
import com.example.nickgao.utils.widget.SearchBarView;

import java.util.ArrayList;
import java.util.List;



public class SearchFragment extends RCListFragment
{
  private static final String TAG = "[RC]SearchFragment";
  protected BaseAdapter mAdapter;
  private AsyncSearchItemLoader mCurrentLoader;
  protected TextView mEmtytext;
  protected ProgressBar mLoadingBar;
  protected SearchBarView mSearchBar;
  private List<SuperSearching.ItemSearchResult> mSearchItemList;
  private SearchBarClearReceiver mSearchBarClearReceiver;

  private class SearchBarClearReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.hasExtra(RingCentralMain.TAB_TAG)
              && !RingCentralMain.MainActivities.Contacts.toString().equals(intent.getStringExtra(RingCentralMain.TAB_TAG))) {
        if (!mSearchBar.isEmpty()) {
          mSearchBar.clearEditText();
          mSearchBar.clearFocus();
        }
      }
    }
  }

  @Override
  public void onDestroy() {
    if (mSearchBarClearReceiver != null) {
      mActivity.unregisterReceiver(mSearchBarClearReceiver);
      mSearchBarClearReceiver = null;
    }
    super.onDestroy();
  }

  private void onSearching(String paramString)
  {
    if (this.mCurrentLoader != null)
      this.mCurrentLoader.cancel(false);
    this.mCurrentLoader = new AsyncSearchItemLoader(paramString);
    mCurrentLoader.execute();
  }

  protected void initializeListHeader()
  {
    LayoutInflater inflater = LayoutInflater.from(mActivity);
    LinearLayout view = (LinearLayout) inflater.inflate(R.layout.contacts_search_bar_view, null);
    mSearchBar = (SearchBarView) view.findViewById(R.id.contacts_list_search_bar);
    this.mSearchBar.setSearchHandler(new SearchBarView.SearchHandler() {
      public void search(String paramString) {
        onSearching(paramString);
      }
    });
    getListView().addHeaderView(view, null, false);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mSearchBarClearReceiver = new SearchBarClearReceiver();
    IntentFilter searchIntent = new IntentFilter(RCMConstants.RECEIVER_SEARCHBAR_CLEAR);
    mActivity.registerReceiver(mSearchBarClearReceiver, searchIntent);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(R.layout.search_fragment, paramViewGroup, false);
  }

  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    this.mEmtytext = ((TextView)paramView.findViewById(R.id.emptyListText));
    this.mLoadingBar = ((ProgressBar)paramView.findViewById(R.id.loading));

    this.mSearchItemList = new ArrayList();
    this.mAdapter = new CombinedSearchItemAdapter(this.mActivity, this.mSearchItemList);
    setListAdapter(this.mAdapter);
    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
        SearchItemViewHolder localSearchItemViewHolder = (SearchItemViewHolder) paramView.getTag();
        if (localSearchItemViewHolder != null)
          localSearchItemViewHolder.onItemClick(paramAdapterView, paramView, paramInt - 1, paramLong);
          //if (localSearchItemViewHolder instanceof CombinedSearchItemAdapter.CallLogItemViewHolder || localSearchItemViewHolder instanceof MessageItemViewHolder) {
          //  localSearchItemViewHolder.onItemClick(paramAdapterView, paramView, paramInt - 1, paramLong);
          //}else {
          //  localSearchItemViewHolder.onItemClick(paramAdapterView, paramView, paramInt, paramLong);
          //}
      }
    });
    initializeListHeader();
  }

  private class AsyncSearchItemLoader extends AsyncTask<String, Void,  List<SuperSearching.ItemSearchResult>> {
    private final String mFilter;

    public AsyncSearchItemLoader(String filter) {
      mFilter = filter;
    }

    @Override
    protected void onPreExecute() {
      updateLoading();
    }

    @Override
    protected List<SuperSearching.ItemSearchResult> doInBackground(String... params) {
      if(!TextUtils.isEmpty(mFilter)) {
        SuperSearching ss = new SuperSearching(getContext());
        return ss.search(mFilter);
      }else {
        return new ArrayList<>();
      }
    }

    @Override
    protected void onPostExecute( List<SuperSearching.ItemSearchResult> adapterData) {
      MktLog.d(TAG, "loaded: " + adapterData.toString());

      if(isDetached() || mActivity.isFinishing() || getListView() == null) {
        MktLog.d(TAG, "the fragment is already detached or the main activity is finish or listview is not initialize");
        return; //according to loading process can be invoked when the fragment is already in detached state
      }

      mLoadingBar.setVisibility(View.GONE);

      if (adapterData.size() == 0) {
        mEmtytext.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(mFilter)) {
          mEmtytext.setText(getText(R.string.messages_no_results_found));
        } else {
          mEmtytext.setText(getText(R.string.messages_no_results_found));
        }
        getListView().setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
      } else {
        mEmtytext.setVisibility(View.GONE);
        getListView().setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
      }
      mSearchItemList.clear();
      MktLog.d(TAG, "Loaded contacts; " + adapterData);
      mSearchItemList.addAll(adapterData);
      mAdapter.notifyDataSetChanged();
    }

    private void updateLoading() {
      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          if (isDetached()) {
            MktLog.d(TAG, "the fragment is already detached");
            return; //according to loading process can be invoked when the fragment is already in detached state
          }
          if (getStatus() != Status.FINISHED) {
            mEmtytext.setVisibility(View.GONE);
            mLoadingBar.setVisibility(View.VISIBLE);
          }
        }
      }, 300l);
    }
  }


  class CombinedSearchItemAdapter extends BaseAdapter
  {
    private Context mContext;
    private List<SuperSearching.ItemSearchResult> mItems;

    public CombinedSearchItemAdapter(Context context, List<SuperSearching.ItemSearchResult> items)
    {
      this.mContext = context;
      this.mItems = items ;
    }

    private View prepareConvertView(SuperSearching.ItemSearchResult paramItemSearchResult)
    {
      View localView;
      SearchItemViewHolder localObject;
      if (paramItemSearchResult.type == SuperSearching.SearchType.Message) {
        localView = LayoutInflater.from(this.mContext).inflate(R.layout.message_list_item_main, null);
//        localObject = new MessageItemViewHolder(localView);
//        localView.setTag(localObject);
//        ((MessageItemViewHolder)localObject).update(paramItemSearchResult,localView);
      } else if(paramItemSearchResult.type == SuperSearching.SearchType.App){
        localView = LayoutInflater.from(this.mContext).inflate(R.layout.search_in_app, null);
        TextView tv= (TextView) localView.findViewById(R.id.name);
        tv.setText(paramItemSearchResult.data.toString());
        final String s=paramItemSearchResult.data.toString();
        tv.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            SearchUtil.getInstance().launchActivity(mActivity,s);
          }
        });
      }else {
        localView = LayoutInflater.from(this.mContext).inflate(R.layout.contacts_list_item, null);
        localObject = new ContactItemViewHolder(localView);
        localView.setTag(localObject);
        ((ContactItemViewHolder)localObject).update(paramItemSearchResult,localView);
      }

      return localView;
    }

    public int getCount()
    {
      return this.mItems.size();
    }

    public Object getItem(int paramInt)
    {
      return this.mItems.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      return prepareConvertView((SuperSearching.ItemSearchResult)this.mItems.get(paramInt));
    }



    private class ContactItemViewHolder implements SearchItemViewHolder
    {
      private ImageView mAvatar;
      private TextView mDescription;
      private View mNameTypeWrapper;
      private TextView mType;

      public ContactItemViewHolder(View view)
      {
        this.mNameTypeWrapper = this.mDescription = (TextView)view.findViewById(R.id.name);
        this.mAvatar = (ImageView)view.findViewById(R.id.photo);
        this.mType = (TextView)view.findViewById(R.id.type);
      }
  //// FIXME: 2/8/17 
      @Override
      public void update(SuperSearching.ItemSearchResult paramItemSearchResult, View paramView) {

      }

      @Override
      public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {

      }

      //      public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
//      {
//        Contact  localContact = (Contact)((SuperSearching.ItemSearchResult)SearchFragment.this.mAdapter.getItem(paramInt)).data;
//        ContactOperator.getContactsOperator(localContact, mActivity)
//                .viewDetails(
//                        CommonEventDetailActivity.VIEW_PERSONAL_CONTACT,
//                        "",
//                        RCMConstants.COMPANY_FROM_CONTACT_LIST);
//      }
//
//      public void update(SuperSearching.ItemSearchResult paramItemSearchResult, View paramView) {
//        final Contact displayContact = (Contact)paramItemSearchResult.data;
//        mDescription.setText(displayContact.getDisplayName());
//
//        //   mAvatar.setVisibility(mCurrentTab == BaseContactsFragment.Tabs.COMPANY ? View.GONE : View.VISIBLE);
//        if (displayContact.getType() == Contact.ContactType.DEVICE) {
//          DeviceContact contact = (DeviceContact) displayContact;
//          Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contact.getContactId());
//          uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
//          Glide.with(mContext)
//                  .load(uri)
//                  .asBitmap()
//                  .diskCacheStrategy(DiskCacheStrategy.NONE)
//                  .transform(new RoundedCornersTransformation(mContext, 180, 0))
//                  .placeholder(R.drawable.ic_contact_list_picture)
//                  .into(mAvatar);
//          mType.setText("");
//        } else if(displayContact.getType() == Contact.ContactType.CLOUD_COMPANY) {
//
//          CompanyContact companyContact = (CompanyContact)displayContact;
//          String profilePath = null;
//          if(companyContact.getId() == CurrentUserSettings.getSettings().getCurrentMailboxId()) {
//            profilePath = ProfileImageOperator.getAvatarPath();
//          }else{
//            profilePath = ProfileImageOperator.getProfilePath(companyContact.getEtag(), companyContact.getId());
//          }
//          if(profilePath == null) {
//            Glide.with(mContext)
//                    .load(R.drawable.ic_contact_list_picture)
//                    .asBitmap()
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .into(mAvatar);
//          }else {
//            Glide.with(mContext)
//                    .load(profilePath)
//                    .asBitmap()
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .transform(new RoundedCornersTransformation(mContext, 180, 0))
//                    .placeholder(R.drawable.ic_contact_list_picture)
//                    .into(mAvatar);
//          }
//
//          mType.setText(companyContact.getPin());
//
//        } else {
//          Glide.with(mContext)
//                  .load(R.drawable.ic_contact_list_picture)
//                  .asBitmap()
//                  .diskCacheStrategy(DiskCacheStrategy.NONE)
//                  .into(mAvatar);
//          mType.setText("");
//        }
//      }
      }
    }

//    private class MessageItemViewHolder implements SearchItemViewHolder
//    {
//      private TextView dateView;
//      private ImageView detailItemIcon;
//      private ImageView messageState;
//      private NameTextView nameView;
//      private ProgressBar progressView;
//      private TextView timeView;
//
//      public MessageItemViewHolder(View view)
//      {
//        this.nameView = (NameTextView) view.findViewById(R.id.name);
//        this.timeView = (TextView) view.findViewById(R.id.label);
//        this.dateView = (TextView) view.findViewById(R.id.date);
//        this.messageState = (ImageView) view.findViewById(R.id.presence);
//        this.progressView = (ProgressBar) view.findViewById(R.id.progress);
//        this.detailItemIcon = (ImageView) view.findViewById(R.id.message_detail_item_icon);
//      }
//      private String getClickArrowFlurryFrom(MessageItem mi) {
//        String activityFrom = "";
//        if (mi.messageType.equals(MessageInfo.MessageType.VOICE)) {
//          activityFrom = RCMConstants.EVENT_DETAIL_FROM_MESSAGE_VOICE;
//        } else if (mi.messageType.equals(MessageInfo.MessageType.FAX)) {
//          activityFrom = RCMConstants.EVENT_DETAIL_FROM_MESSAGE_FAX;
//        } else if (mi.messageType.equals(MessageInfo.MessageType.SMS) || mi.messageType.equals(MessageInfo.MessageType.PAGER)) {
//          activityFrom = RCMConstants.EVENT_DETAIL_FROM_MESSAGE_TEXT;
//        }
//        return activityFrom;
//      }
//
//      private boolean isFromText(MessageItem paramMessageItem)
//      {
//        return (paramMessageItem.messageType.equals(MessageInfo.MessageType.SMS)) || (paramMessageItem.messageType.equals(MessageInfo.MessageType.PAGER));
//      }
//
//      private void setDetailIcon(final MessageItem mi) {
//        boolean showDetailIcon = true;
//        this.detailItemIcon.setVisibility(showDetailIcon ? View.VISIBLE : View.INVISIBLE);
//        this.detailItemIcon.setOnClickListener(new View.OnClickListener() {
//
//          @Override
//          public void onClick(View v) {
//            if (mi.groupType == RCMDataStore.MessagesTable.GROUP_TYPE_GROUPED) {
//              Intent intent = new Intent(mActivity, MessageViewRecipientsActivity.class);
//              intent.putExtra(RCMConstants.EXTRA_MESSAGE_GROUP_ID, mi.conversationId);
//              intent.putExtra(RCMConstants.EXTRA_EVENT_DETAIL_FROM, getClickArrowFlurryFrom(mi));
//              intent.putExtra(RCMConstants.EXTRA_EVENT_DETAIL_FILTER_FROM, isFromText(mi));
//              try {
//                startActivity(intent);
//              } catch (ActivityNotFoundException e) {
//                if (LogSettings.ENGINEERING) {
//                  EngLog.e(TAG, "show group EventDetail()", e);
//                }
//              }
//            } else {
//              Uri uri = UriHelper.getUri(RCMProvider.MESSAGES, CurrentUserSettings.getSettings().getCurrentMailboxId(), mi._id);
//              Intent intent = new Intent(mActivity, CommonEventDetailActivity.class);
//              intent.setData(uri);
//              intent.putExtra(RCMConstants.EXTRA_EVENT_DETAIL_TYPE, CommonEventDetailActivity.EVENT_DETAIL_TYPE_MESSAGE);
//              intent.putExtra(RCMConstants.EXTRA_COMPANY_CONTACT_NEED_LIGHT_BLUE, true);
//              intent.putExtra(RCMConstants.EXTRA_EVENT_DETAIL_FROM, getClickArrowFlurryFrom(mi));
//              intent.putExtra(RCMConstants.EXTRA_EVENT_DETAIL_FILTER_FROM, isFromText(mi));
//              intent.putExtra(RCMConstants.EXTRA_EVENT_DETAIL_FROM_MSG_TYPE, mi.messageType.ordinal());
//              boolean isSMS = mi.messageType.equals(MessageInfo.MessageType.SMS) || mi.messageType.equals(MessageInfo.MessageType.PAGER);
//              intent.putExtra(RCMConstants.isSMS, isSMS);
//              try {
//                startActivity(intent);
//              } catch (ActivityNotFoundException e) {
//                if (LogSettings.ENGINEERING) {
//                  EngLog.e(TAG, "showEventDetail()", e);
//                }
//              }
//            }
//          }
//        });
//      }
//
//      private void setMessageIcon(MessageItem mi) {
//        if (mi.syncStatus == RCMDataStore.SyncStatusEnum.SYNC_STATUS_LOADED) {
//          setMessageIconForLoaded(mi);
//        } else if ((MessageInfo.MessageType.VOICE.equals(mi.messageType) || MessageInfo.MessageType.FAX.equals(mi.messageType))
//                && (mi.syncStatus == RCMDataStore.SyncStatusEnum.SYNC_STATUS_LOADING ||
//                MessagesHandler.isOnList(mActivity, mi.msgId, MessagesHandler.MessageBodyLoader.sf_LOAD_PRIORITY_HIGH))) {
//          setMessageIconForLoading(mi);
//        } else {
//          setMessageIconForDefault(mi);
//        }
//      }
//
//      private void setDisplayName(MessageItem paramMessageItem)
//      {
//        this.nameView.setText(paramMessageItem.displayName);
//      }
//      private void setMessageIconForDefault(MessageItem mi) {
//        this.messageState.setVisibility(View.VISIBLE);
//
//        if (MessageInfo.UNREAD == mi.readStatus) {
//          setMessageIconForDefaultWhenUnread(mi);
//        } else {
//          setMessageIconForDefaultWhenRead(mi);
//        }
//        this.progressView.setVisibility(View.GONE);
//      }
//
//      private void setMessageIconForDefaultWhenRead(MessageItem mi) {
//        switch (mi.messageType) {
//          case VOICE:
//            this.messageState.setImageResource(R.drawable.ic_voice_opened_in_queue);
//            break;
//
//          case FAX:
//            this.messageState.setImageResource(R.drawable.ic_fax_opened_in_queue);
//            break;
//
//          case SMS:
//            this.messageState.setImageResource(R.drawable.ic_text_opened);
//            break;
//
//          case PAGER:
//            if (mi.pagerToDepartment || mi.groupType == RCMDataStore.MessagesTable.GROUP_TYPE_GROUPED) {
//              this.messageState.setImageResource(R.drawable.ic_group_opened);
//            } else {
//              this.messageState.setImageResource(R.drawable.ic_text_opened);
//            }
//            break;
//          default:
//            break;
//        }
//        this.nameView.setTextColor(getResources().getColor(R.color.messages_name_read));
//      }
//
//      private void setMessageIconForDefaultWhenUnread(MessageItem mi) {
//        switch (mi.messageType) {
//          case VOICE:
//            this.messageState.setImageResource(R.drawable.ic_voice_unopened_in_queue);
//            break;
//
//          case FAX:
//            this.messageState.setImageResource(R.drawable.ic_fax_unopened_in_queue);
//            break;
//
//          case SMS:
//            this.messageState.setImageResource(R.drawable.ic_text_unopened);
//            break;
//
//          case PAGER:
//            if (mi.pagerToDepartment || mi.groupType == RCMDataStore.MessagesTable.GROUP_TYPE_GROUPED) {
//              this.messageState.setImageResource(R.drawable.ic_group_unopened);
//            } else {
//              this.messageState.setImageResource(R.drawable.ic_text_unopened);
//            }
//            break;
//
//          default:
//            break;
//        }
//        this.nameView.setTextColor(getResources().getColor(R.color.messages_name_not_read));
//      }
//
//      private void setMessageIconForLoaded(MessageItem mi) {
//        this.messageState.setVisibility(View.VISIBLE);
//        this.progressView.setVisibility(View.GONE);
//
//        if (MessageInfo.UNREAD == mi.readStatus) {
//          if (mi.messageType.equals(MessageInfo.MessageType.VOICE)) {
//            this.messageState.setImageResource(R.drawable.ic_voicemail_unopened);
//          } else if (mi.messageType.equals(MessageInfo.MessageType.FAX)) {
//            this.messageState.setImageResource(R.drawable.ic_fax_unopened);
//          } else if (mi.messageType.equals(MessageInfo.MessageType.SMS)) {
//            this.messageState.setImageResource(R.drawable.ic_text_unopened);
//          } else if (mi.messageType.equals(MessageInfo.MessageType.PAGER)) {
//            if (mi.pagerToDepartment || mi.groupType == RCMDataStore.MessagesTable.GROUP_TYPE_GROUPED) {
//              this.messageState.setImageResource(R.drawable.ic_group_unopened);
//            } else {
//              this.messageState.setImageResource(R.drawable.ic_text_unopened);
//            }
//          }
//          this.nameView.setTextColor(getResources().getColor(R.color.messages_name_not_read));
//        } else {
//          if (mi.messageType.equals(MessageInfo.MessageType.VOICE)) {
//            this.messageState.setImageResource(R.drawable.ic_voicemail_opened);
//          } else if (mi.messageType.equals(MessageInfo.MessageType.FAX)) {
//            this.messageState.setImageResource(R.drawable.ic_fax_opened);
//          } else if (mi.messageType.equals(MessageInfo.MessageType.SMS)) {
//            this.messageState.setImageResource(R.drawable.ic_text_opened);
//          } else if (mi.messageType.equals(MessageInfo.MessageType.PAGER)) {
//            if (mi.pagerToDepartment || mi.groupType == RCMDataStore.MessagesTable.GROUP_TYPE_GROUPED) {
//              this.messageState.setImageResource(R.drawable.ic_group_opened);
//            } else {
//              this.messageState.setImageResource(R.drawable.ic_text_opened);
//            }
//          }
//          this.nameView.setTextColor(getResources().getColor(R.color.messages_name_read));
//        }
//      }
//
//      private void setMessageIconForLoading(MessageItem mi) {
//        this.messageState.setVisibility(View.GONE);
//        this.progressView.setVisibility(View.VISIBLE);
//        this.progressView.bringToFront();
//
//        if (MessageInfo.UNREAD == mi.readStatus) {
//          this.progressView.setEnabled(true);
//          this.nameView.setTextColor(getResources().getColor(R.color.messages_name_not_read));
//        } else {
//          this.progressView.setEnabled(false);
//          this.nameView.setTextColor(getResources().getColor(R.color.messages_name_read));
//        }
//      }
//
//      private void startMessageDetail(MessageItem mi, int messagesCount, int messagePosition) {
//        Intent intent = new Intent();
//        intent.setClass(mActivity, MessageDetailActivity.class);
//        intent.putExtra(RCMConstants.EXTRA_MESSAGE_ID, mi.msgId);
//        intent.putExtra(RCMConstants.EXTRA_MESSAGE_POSITION, messagePosition);
//        intent.putExtra(RCMConstants.EXTRA_MESSAGES_COUNT, messagesCount);    // one item of the list is search bar, so need to reduce one
//        startActivity(intent);
//      }
//
//      private void startTextMessages(MessageItem mi) {
//        Intent intent = new Intent();
//        intent.setClass(mActivity, TextMessages.class);
//        intent.putExtra(RCMConstants.EXTRA_TEXTMESSAGES_FROM, RCTitleBarWithDropDownFilter.STATE_ALL);
//        Uri uri = UriHelper.getUri(RCMProvider.MESSAGES, CurrentUserSettings.getSettings().getCurrentMailboxId(), mi._id);
//        intent.setData(uri);
//        intent.putExtra(RCMConstants.EXTRA_MESSAGES_TYPE, mi.messageType.getValue());
//        RingCentralApp.setConversionId(mi.conversationId);
//        startActivity(intent);
//      }
//
//      public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
//      {
//        MessageItem localMessageItem;
//        try
//        {
//          localMessageItem = (MessageItem)((SuperSearching.ItemSearchResult)SearchFragment.this.mAdapter.getItem(paramInt)).data;
//          if (localMessageItem == null)
//          {
//            MktLog.e("[RC]SearchFragment", "onItemClick(): position = " + paramInt + "; id = " + paramLong + ", failed, mi is null.");
//            return;
//          }
//          if ((MessageInfo.MessageType.SMS.equals(localMessageItem.messageType)) || (MessageInfo.MessageType.PAGER.equals(localMessageItem.messageType)))
//          {
//            startTextMessages(localMessageItem);
//            return;
//          }
//        }
//        catch (Exception localException)
//        {
//          MktLog.e("[RC]SearchFragment", "onItemClick(): position = " + paramInt + "; id = " + paramLong + ", failed : " + localException.toString());
//          return;
//        }
//        startMessageDetail(localMessageItem, -1 + paramAdapterView.getCount(), paramInt - 1);
//      }
//
//      public void update(SuperSearching.ItemSearchResult item, View view)
//      {
//        final MessageItem mi = (MessageItem)item.data;
//        setDisplayName(mi);
//        String duration;
//        MessageItemViewHolder itemView = this;
//        if (mi.messageType.equals(MessageInfo.MessageType.VOICE)) {
//          duration = LabelsUtils.getLengthLabel(mi.duration);
//          itemView.timeView.setText(duration);
//        } else if (mi.messageType.equals(MessageInfo.MessageType.FAX)) {
//          duration = (mi.duration > 1) ? getResources().getString(R.string.fax_pages, mi.duration)
//                  : getResources().getString(R.string.fax_page, mi.duration);
//          itemView.timeView.setText(duration);
//        } else if (mi.messageStatus != null
//                && (mi.messageStatus.equals(RCMDataStore.MessagesTable.MESSAGE_STATUS_DELIVERY_FAILED)
//                || mi.messageStatus.equals(RCMDataStore.MessagesTable.MESSAGE_STATUS_SENDING_FAILED))) {
//          itemView.timeView.setText(R.string.message_send_failure);
//        } else if ((mi.messageType.equals(MessageInfo.MessageType.SMS) || mi.messageType.equals(MessageInfo.MessageType.PAGER))
//                && mi.sendStatus == RCMDataStore.MessagesTable.SEND_STATUS_SEND_INIT && mi.direction == RCMDataStore.MessagesTable.DIRECTION_OUTBOUND) {
//          itemView.timeView.setText(R.string.message_sending);
//        } else if (mi.restErrorCode != 0 && (mi.messageType.equals(MessageInfo.MessageType.SMS) || mi.messageType.equals(MessageInfo.MessageType.PAGER))) {
//          itemView.timeView.setText(R.string.message_send_failure);
//        } else if (mi.messageType.equals(MessageInfo.MessageType.SMS) || mi.messageType.equals(MessageInfo.MessageType.PAGER)) {
//          itemView.timeView.setText(mi.subject);
//        }
//
//        itemView.dateView.setText(LabelsUtils.getRelativeDateLabel(mi.timeInMillis));
//
//        setDetailIcon(mi);
//        setMessageIcon(mi);
//      }
//    }

    }