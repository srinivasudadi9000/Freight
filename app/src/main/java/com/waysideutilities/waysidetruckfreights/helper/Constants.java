package com.waysideutilities.waysidetruckfreights.helper;

/**
 * Created by XenoSoft2 on 1/3/2017.
 */
public class Constants {
    public static final String MY_PREFS_NAME = "Freight";
    public static final int CAMERA_REQUEST_CODE = 11;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 22;
    public static final int GALLARY_REQUEST_CODE = 66;
    public static final int IMG_SHRINK_WIDTH = 1024;
    public static final int IMG_SHRINK_HEIGHT = 1024;
    public static final int ASK_CALL_PERMISSIONS = 102;
    public static final int REQUEST_PERMISSIONS = 20;
    public static final String CHECKSUM_GENERATION_URL = "http://www.waysideutilities.com/api/patym_kit/generateChecksum.php";
    public static final String TRANSACTION_STATUS_URL = "http://www.waysideutilities.com/api/patym_kit/TxnStatus.php";

    public static final String MID = "MID";
    public static final String MID_VALUE = "WaySid96823888037250";//Production WaySid96823888037250; staging Waysid26935139145669
    public static final String CUST_ID_VALUE = "wayside.apps@gmail.com";
    public static final String INDUSTRY_TYPE_ID_VALUE = "Retail109";//Production Retail109; staging Retail
    public static final String CHANNEL_ID_VALUE = "WAP";//Production WAP; staging WAP
    public static final String WEBSITE_VALUE = "WaySidWAP";//Production WaySidWAP; staging APP_STAGING
    public static final String CHECKSUMHASH = "CHECKSUMHASH";
    public static final String ORDERID = "ORDERID";
    public static final String MESSAGE = "The rest of the payment should be dealt between the two parties(cargo provider and truck owner solely).Wayside Truck Freight doesn't involve in that transaction.Necessary taxes(GST,TDS, etc.) will applicable as per the government norms.";
}
