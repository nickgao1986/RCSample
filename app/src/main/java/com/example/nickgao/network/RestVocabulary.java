package com.example.nickgao.network;


public interface RestVocabulary {

    public static final String ID = "id";
    public static final String URI = "uri";
    public static final String RECORDS = "records";
    public static final String PAGING = "paging";
    public static final String NAVIGATION = "navigation";

    public interface MessageInfoKey {

        public static final String TYPE = "type";
        public static final String DIRECTION = "direction";
        public static final String FROM = "from";
        public static final String TO = "to";
        public static final String CREATION_TIME = "creationTime";
        public static final String LAST_MODIFIED_TIME = "lastModifiedTime";
        public static final String MESSAGE_STATUS = "messageStatus";
        public static final String READ_STATUS = "readStatus";
        public static final String AVAILABILITY = "availability";
        public static final String ATTACHMENTS = "attachments";
        public static final String CONVERSATION_ID = "conversationId";
        public static final String DELIVERY_ERROR_CODE = "deliveryErrorCode";
        public static final String FAX_PAGE_COUNT = "faxPageCount";
        public static final String FAX_RESOLUTION = "faxResolution";
        public static final String PRIORITY = "priority";
        public static final String SMS_DELIVERY_TIME = "smsDeliveryTime";
        public static final String SMS_SENDING_ATTEMPTS_COUNT = "smsSendingAttemptsCount";
        public static final String SUBJECT = "subject";
        public static final String PG_TO_DEPARTMENT = "pgToDepartment";
    }


    public interface CallerInfoKey{

        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String EXTENSION_NUMBER = "extensionNumber";
        public static final String LOCATION = "location";
        public static final String NAME = "name";
    }


    public interface MessageAttachmentInfoKey {

        public static final String CONTENT_TYPE = "contentType";
        public static final String VM_DURATION = "vmDuration";
    }


    public interface MessageTypeEnum {

        public static final String FAX = "Fax";
        public static final String SMS = "SMS";
        public static final String VOICE_MAIL = "VoiceMail";
        public static final String PAGER = "Pager";
        public static final String TEXT = "Text";
        public static final String MULTIPLE = "Multiple"; 

    }

    public interface MessageDirectionEnum {

        public static final String INBOUND = "Inbound";
        public static final String OUTBOUND = "Outbound";

    }

    public interface MessageStatusEnum {

        public static final String QUEUED = "Queued";
        public static final String SENT = "Sent";
        public static final String DELIVERED = "Delivered";
        public static final String DELIVERY_FAILED = "DeliveryFailed";
        public static final String SENDING_FAILED = "SendingFailed";
        public static final String RECEIVED = "Received";
    }

    public interface MessageReadStatusEnum {

        public static final String READ = "Read";
        public static final String UNREAD = "Unread";
    }

    public interface MessageAvailabilityEnum {

        public static final String ALIVE = "Alive";
        public static final String DELETED = "Deleted";
        public static final String PURGED = "Purged";
    }

    public interface FaxResolutionEnum {

        public static final String UNDEFINED = "Undefined";
        public static final String HIGH = "High";
        public static final String LOW = "Low";
    }

    public interface MessagePriorityEnum {

        public static final String NORMAL = "Normal";
        public static final String HIGH = "High";
    }

    public interface SyncInfoKey {

        public static final String SYNC_INFO = "syncInfo";
        public static final String SYNC_TYPE = "syncType";
        public static final String SYNC_TOKEN = "syncToken";
        public static final String SYNC_TIME = "syncTime";
    }

    public interface SyncTypeEnum {

        public static final String FSYNC = "FSync";
        public static final String ISYNC = "ISync";
    }
    
    public interface CallLogEnum {
    	
    	public static final String START_TIME 		= "startTime";
    	public static final String DURATION			= "duration";
    	public static final String TYPE				= "type";
    	public static final String DIRECTION 		= "direction";
    	public static final String ACTION			= "action";
    	public static final String RESULT			= "result";
    	public static final String TO 				= "to";
        public static final String FROM 			= "from";
        public static final String AVAILABILITY		= "availability";
        
        public static final String INBOUND 			= "Inbound";
        public static final String OUTBOUND 		= "Outbound";
        
        public static final String ALIVE 			= "Alive";
        public static final String DELETED 			= "Deleted";
        public static final String PURGED 			= "Purged";
        
    }

    public interface MultipartMsgInfoResponseElementInfoKey {
    	 public static final String HREF 					= "href";
         public static final String STATUS 					= "status";
         public static final String RESPONSE_DESCRIPTION 	= "responseDescription";
    }
    
    public interface GetConferenceInfoKey {
   	 	public static final String HOSTCODE 					= "hostCode";
        public static final String PARTICIPANTCODE 					= "participantCode";
        public static final String PHONENUMBER 	= "phoneNumber";
        public static final String PHONENUMBERS 	= "phoneNumbers";
        public static final String COUNTRY 	= "country";
        public static final String ID 	= "id";
        public static final String URI 	= "uri";
        public static final String NAME 	= "name";
        public static final String ISOCODE 	= "isoCode";
        public static final String CALLINGCODE 	= "callingCode";
        public static final String LOCATION 	= "location";
        public static final String HASGREETING 	= "hasGreeting";
        public static final String DEFAULT 	= "default";
    }
}
