package com.example.nickgao.utils;

import android.content.Context;

import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SuperSearching
{
  protected Context mContext;

  public SuperSearching(Context paramContext)
  {
    this.mContext = paramContext;
  }

  protected Context getContext()
  {
    return this.mContext;
  }

  public List<ItemSearchResult> search(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(searchInApp(paramString));
    localArrayList.addAll(searchMessages(paramString));
    localArrayList.addAll(searchContacts(paramString));
    return localArrayList;
  }

  private List<String> searchInApp(String paramString){
    List<String> list=SearchUtil.getInstance().search(paramString);
    ArrayList localArrayList = new ArrayList();
    if(list!=null){
      for(String s:list){
        localArrayList.add(new ItemSearchResult(SearchType.App, s));
      }
    }
    return localArrayList;
  }


  protected List<ItemSearchResult> searchContacts(String paramString)
  {
    List localList = ContactsProvider.getInstance().loadContacts(true, true, true, true, true, paramString);
    ArrayList localArrayList = new ArrayList(localList.size());
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Contact localContact = (Contact)localIterator.next();
      localArrayList.add(new ItemSearchResult(SearchType.Contact, localContact));
    }
    return localArrayList;
  }

  protected List<ItemSearchResult> searchMessages(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    //// FIXME: 2/8/17 
//    Uri localUri = UriHelper.getUri("messages", CurrentUserSettings.getSettings().getCurrentMailboxId());
//    Cursor localCursor = null;
//    try
//    {
//      String[] arrayOfString = { paramString, paramString, paramString, paramString, paramString, paramString, paramString, paramString };
//      localCursor = getContext().getContentResolver().query(localUri, MessagesProjection.SUMMARY_PROJECTION, "REST_subject LIKE '%' || ? || '%' OR FromName LIKE '%' || ? || '%' OR FromPhone LIKE '%' || ? || '%' OR ToName LIKE '%' || ? || '%' OR ToPhone LIKE '%' || ? || '%' OR display_name LIKE '%' || ? || '%' OR REST_to_extensionNumber LIKE '%' || ? || '%' OR bind_display_name LIKE '%' || ? || '%' ", arrayOfString, "CreateDate DESC");
//      if ((localCursor != null) && (localCursor.getCount() != 0))
//      {
//        localCursor.moveToPosition(-1);
//        while (localCursor.moveToNext())
//        {
//          MessageItem localMessageItem = MessagesQuery.readMessageItem(getContext(), localCursor);
//          localArrayList.add(new ItemSearchResult(SearchType.Message, localMessageItem));
//        }
//      }
//    }
//    catch (Throwable localThrowable)
//    {
//      MktLog.e("[RC]Search", "searchMessages error=" + localThrowable.toString());
//    }
//    finally
//    {
//      if (localCursor != null)
//        localCursor.close();
//    }

    return localArrayList;
  }

  public class ItemSearchResult
  {
    public Object data;
    public SuperSearching.SearchType type;

    public ItemSearchResult(SuperSearching.SearchType type, Object data)
    {
      this.type = type;
      this.data = data;
    }
  }

  public enum SearchType
  {
    Message,
    CallLog,
    Contact,
    App,
    UNKNOWN,
  }
}