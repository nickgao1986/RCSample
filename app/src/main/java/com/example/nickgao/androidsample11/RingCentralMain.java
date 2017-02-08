package com.example.nickgao.androidsample11;

/**
 * Created by nick.gao on 2/8/17.
 */

public class RingCentralMain {

    public static final String TAB = "TAB";
    public static final String TAB_SUB = "TAB_SUB";
    public static final String TAB_TAG = "TAB_TAG";

    public enum MainActivities {
        Messages {
            @Override
            public String toString() {
                return "Messages";
            }
        },
        Calllog {
            @Override
            public String toString() {
                return "CallLog";
            }
        },
        Contacts {
            @Override
            public String toString() {
                return "Contacts";
            }
        },
        Ringout {
            @Override
            public String toString() {
                return "Ringout";
            }
        },
        Text {
            @Override
            public String toString() {
                return "Text";
            }
        },
        Favorites {
            @Override
            public String toString() {
                return "Favorites";
            }
        },
        Fax {
            @Override
            public String toString() {
                return "Fax";
            }
        },
        Conferencing {
            @Override
            public String toString() {
                return "Conferencing";
            }
        },
        Meetings {
            @Override
            public String toString() {
                return "Meetings";
            }
        },
        RCDocuments {
            @Override
            public String toString() {
                return "RCDocuments";
            }
        },
        Glip{
            @Override
            public String toString(){
                return "Glip";
            }
        }
        ,Search{
            @Override
            public String toString(){
                return "Search";
            }
        }
    }
}
