package com.example.nickgao.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by nathan.wei on 3/19/16.
 */
public class SearchUtil {
    private HashMap<String,Class> map;

    private SearchUtil(){
        map =new HashMap<>();
//        map.put("Application Setting",ActivityUtils.isTablet()? SettingsForTablet.class:Settings.class);
//        map.put("About",ActivityUtils.isTablet()? AboutForTablet.class:About.class);
//        map.put("Voip Calling",ActivityUtils.isTablet()? VoipSettingsForTablet.class:VoipSettings.class);
//        map.put("Region",ActivityUtils.isTablet()? DialingPlanTabletActivity.class:DialingPlanActivity.class);
//        map.put("Caller Id",ActivityUtils.isTablet()? SettingsForTablet.class:Settings.class);
//        map.put("Ringout Mode",ActivityUtils.isTablet()? SettingsForTablet.class:RingOutMode.class);
//        map.put("Cloud Storage",ActivityUtils.isTablet()? SettingsCloudStorageListActivityForTablet.class:SettingsCloudStorageListActivity.class);
//        map.put("Default Launching",ActivityUtils.isTablet()? SettingsCloudStorageListActivityForTablet.class:ClearLaunchAsDefaultActivity.class);
//        map.put("New Text", CreateNewMessage.class);
//        map.put("New Fax", FaxOutMainActivity.class);
//        map.put("Glip", FaxOutMainActivity.class);
    }
    private static SearchUtil instance;
    public static SearchUtil getInstance(){
        if(instance==null){
            instance=new SearchUtil();
        }
        return instance;
    }

    public List<String> search(String str){
        List<String> list = new ArrayList<>();
        Iterator<String> itr= map.keySet().iterator();
        while (itr.hasNext()){
            String key =itr.next();

            if(key.toLowerCase().contains(str)){
                list.add(key);
            }
        }
        return list;
    }

    public void launchActivity(Activity activity, String key){
//        Class c=map.get(key);
//        if("Caller Id".equalsIgnoreCase(key)){
//            final CallerIDDialog dialog = new CallerIDDialog(activity,true, null);
//            dialog.setOnDismiss(new DialogInterface.OnDismissListener() {
//                public void onDismiss(DialogInterface dialogInterface) {
//                    dialog.onDismiss(dialogInterface);
//                }
//            });
//            dialog.setCanceledOnTouchOutSide();
//            dialog.show();
//        }else if("Glip".equalsIgnoreCase(key)){
//           launchGlipApp(activity);
//        }else {
//            if (c != null) {
//                Intent intent = new Intent(activity, c);
//                if("Region".equalsIgnoreCase(key)){
//                    intent.putExtra(DialingPlanActivity.EXTRA_LAUNCH_FROM_SETTINGS, true);
//                }
//                activity.startActivity(intent);
//            }
//        }
    }


}
