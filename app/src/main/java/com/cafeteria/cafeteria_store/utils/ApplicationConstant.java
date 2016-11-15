package com.cafeteria.cafeteria_store.utils;

/**
 * Created by anael on 13/11/16.
 */

public class ApplicationConstant {
    /**
     * Constant for Web Services
     */

    /**
     * Global server
     */
    public final static String SERVER_IP = "time2eat.eu-gb.mybluemix.net";

    /**
     * Anael IP
     */
//    public final static String SERVER_IP = "192.168.1.11";

    /**
     * Shira IP
     */
//    public final static String SERVER_IP = "192.168.43.231";

    /**
     * Moshe IP
     */
    //public final static String SERVER_IP = "10.0.0.146";


//    public static final String GET_MAINS_URL = "http://" + SERVER_IP +
//            ":8080/CafeteriaServer/rest/data/getMains";
//    public static final String GET_EXTRAS_URL = "http://" + SERVER_IP +
//            ":8080/CafeteriaServer/rest/data/getExtras";
//    public static final String GET_ITEMS_URL = "http://" + SERVER_IP +
//            ":8080/CafeteriaServer/rest/data/getItems";


    public static final String GET_MAINS_URL = "http://" + SERVER_IP +
            "/rest/data/getMains";
    public static final String GET_EXTRAS_URL = "http://" + SERVER_IP +
            "/rest/data/getExtras";
    public static final String GET_ITEMS_URL = "http://" + SERVER_IP +
            "/rest/data/getItems";

    public static final String UPDATE_ITEM_URL = "http://" + SERVER_IP +
            "/rest/data/updateItem";
    public static final String UPDATE_EXTRA_URL = "http://" + SERVER_IP +
            "/rest/data/updateExtra";
    public static final String UPDATE_MAIN_URL = "http://" + SERVER_IP +
            "/rest/data/updateMain";
}

