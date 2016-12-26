package com.cafeteria.cafeteria_store.utils;

import com.cafeteria.cafeteria_store.data.Cafeteria;

import java.util.Calendar;

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
//    public final static String SERVER_IP = "192.168.1.19";

    /**
     * Shira IP
     */
//    public final static String SERVER_IP = "192.168.43.140";

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
//    public static final String GET_ORDERS_URL = "http://" + SERVER_IP +
//            ":8080/CafeteriaServer/rest/data/getOrders";
//    public static final String GET_ORDERS_READY_URL = "http://" + SERVER_IP +
//            ":8080/CafeteriaServer/rest/data/getOrdersReady";
//    public static final String UPDATE_ORDER_DELIVERED = "http://" + SERVER_IP +
//            ":8080/CafeteriaServer/rest/data/updateOrderDelivered";
//    public static final String UPDATE_ORDER_READY = "http://" + SERVER_IP +
//            ":8080/CafeteriaServer/rest/data/updateOrderReady";
//
//    public static final String GET_SERVINGS_URL = "http://" + SERVER_IP +
//            ":8080/CafeteriaServer/rest/data/getServings";
//
//    public static final String UPDATE_ITEM_URL = "http://" + SERVER_IP +
//            ":8080/CafeteriaServer/rest/data/updateItem";
//    public static final String UPDATE_EXTRA_URL = "http://" + SERVER_IP +
//            ":8080/CafeteriaServer/rest/data/updateExtra";
//    public static final String UPDATE_MAIN_URL = "http://" + SERVER_IP +
//            ":8080/CafeteriaServer/rest/data/updateMain";
//    public static final String UPDATE_SERVINGS_URL = "http://" + SERVER_IP +
//            ":8080/CafeteriaServer/rest/data/updateServing";

    public final static String GET_SERVERS = "http://time2eat.eu-gb.mybluemix.net/rest/server/getServers";

    public static final String GET_MAINS_URL = "/rest/data/getMains";
    public static final String GET_EXTRAS_URL = "/rest/data/getExtras";
    public static final String GET_ITEMS_URL = "/rest/data/getItems";
    public static final String GET_ORDERS_URL =  "/rest/data/getOrders";
    public static final String UPDATE_ORDER_DELIVERED =  "/rest/data/updateOrderDelivered";
    public static final String UPDATE_ORDER_READY = "/rest/data/updateOrderReady";
    public static final String GET_ORDERS_READY_URL = "/rest/data/getOrdersReady";

    public static final String GET_SERVINGS_URL = "/rest/data/getServings";
    public static final String UPDATE_ITEM_URL = "/rest/data/updateItem";
    public static final String UPDATE_EXTRA_URL = "/rest/data/updateExtra";
    public static final String UPDATE_MAIN_URL =  "/rest/data/updateMain";
    public static final String UPDATE_SERVINGS_URL = "/rest/data/updateServing";

    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_FORMAT = "dd-MM-yyyy";


    public static final String ORDER_MATCH = "The scanned code match this order";
    public static final String ORDER_DONT_MATCH ="The scanned code do not match this order";

    public static String getAddress(String name){
        switch(name){
            case GET_MAINS_URL:
                return "http://" + getCafeteria().getServerIp() + GET_MAINS_URL;
            case GET_EXTRAS_URL:
                return "http://" + getCafeteria().getServerIp() + GET_EXTRAS_URL;
            case GET_ITEMS_URL:
                return "http://" + getCafeteria().getServerIp() + GET_ITEMS_URL;
            case GET_ORDERS_URL:
                return "http://" + getCafeteria().getServerIp() + GET_ORDERS_URL;
            case UPDATE_ORDER_DELIVERED:
                return "http://" + getCafeteria().getServerIp() + UPDATE_ORDER_DELIVERED;
            case UPDATE_ORDER_READY:
                return "http://" + getCafeteria().getServerIp() + UPDATE_ORDER_READY;
            case GET_ORDERS_READY_URL:
                return "http://" + getCafeteria().getServerIp() + GET_ORDERS_READY_URL;
            case GET_SERVINGS_URL:
                return "http://" + getCafeteria().getServerIp() + GET_SERVINGS_URL;
            case UPDATE_ITEM_URL:
                return "http://" + getCafeteria().getServerIp() + UPDATE_ITEM_URL;
            case UPDATE_EXTRA_URL:
                return "http://" + getCafeteria().getServerIp() + UPDATE_EXTRA_URL;
            case UPDATE_MAIN_URL:
                return "http://" + getCafeteria().getServerIp() + UPDATE_MAIN_URL;
            case UPDATE_SERVINGS_URL:
                return "http://" + getCafeteria().getServerIp() + UPDATE_SERVINGS_URL;
            default:
                return name;
        }
    }

    private static Cafeteria cafeteria;
    public static Cafeteria getCafeteria() {
        return cafeteria;
    }

    public static void setCafeteria(Cafeteria cafeteria) {
        ApplicationConstant.cafeteria = cafeteria;
    }
}


