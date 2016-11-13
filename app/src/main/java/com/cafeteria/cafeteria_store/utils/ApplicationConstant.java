package com.cafeteria.cafeteria_store.utils;

/**
 * Created by anael on 13/11/16.
 */

public class ApplicationConstant{
        /**
         * Constant for Web Services
         */

        /**
         * Global server
         */
//    public final static String SERVER_IP = "cafeteriaserver.eu-gb.mybluemix.net";

        /**
         * Anael IP
         */
        public final static String SERVER_IP = "192.168.43.91";

        /**
         * Shira IP
         */
//    public final static String SERVER_IP = "192.168.43.231";

        /**
         * Moshe IP
         */
        //public final static String SERVER_IP = "10.0.0.146";


        public final static String USER_VALIDATION_URL = "http://" + SERVER_IP +
                ":8080/CafeteriaServer/rest/users/isUserExist";

//
//    public final static String USER_VALIDATION_URL = "http://" + SERVER_IP +
//            "/rest/users/isUserExist";
    }

