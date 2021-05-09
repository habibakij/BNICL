package com.csi.bnic.Utility;

/**
 * Created by Jahid on 28/7/19.
 */
public class Constants {
    public static int SPLASH_TIME_OUT = 0;

    public static class SharedPrefItem{
        public static  final String globalPreferenceInsurance = "INSURANCE";
        public static  final String globalPreferenceInfo = "INFO";
        public static  final String PLAN_NAME = "PLAN_NAME";
        public static  final String PLAN_ID = "PLAN_ID";
        public static  final String VEHICLE_TYPE = "VEHICLE_TYPE";
        public static  final String VEHICLE_ID = "VEHICLE_ID";
        public static  final String SUBTYPE_ID = "SUBTYPE_ID";
        public static  final String DRIVER = "DRIVER";
        public static  final String ENGINE_CAPACITY = "ENGINE_CAPACITY";
        public static  final String PASSENGER = "PASSENGER";
        public static  final String CONDUCTOR = "CONDUCTOR";
        public static  final String HELPER = "HELPER";
        public static  final String PASSENGER_ID = "PASSENGER_ID";
        public static  final String PASSENGER_NO = "PASSENGER_NO";
        public static  final String POLICY_START_DATE = "POLICY_START_DATE";
        public static  final String POLICY_END_DATE = "POLICY_END_DATE";
        public static  final String FULL_NAME = "FULL_NAME";
        public static  final String ADDRESS = "ADDRESS";
        public static  final String CITY = "CITY";
        public static  final String CITY_ID = "CITY_ID";
        public static  final String MAILING_ADDRESS = "MAILING_ADDRESS";
        public static  final String MAILING_CITY = "MAILING_CITY";
        public static  final String MAILING_CITY_ID = "MAILING_CITY_ID";
        public static  final String MOBILE = "MOBILE";
        public static  final String EMAIL = "EMAIL";
        public static  final String BRAND = "BRAND";
        public static  final String MANUFACTURE_YEAR = "MANUFACTURE_YEAR";
        public static  final String REGISTRATION_NUMBER = "REGISTRATION_NUMBER";
        public static  final String REGISTRATION_DATE = "REGISTRATION_DATE";
        public static  final String ENGINE_NUMBER = "ENGINE_NUMBER";
        public static  final String CHASSIS_NUMBER = "CHASSIS_NUMBER";
        public static  final String TOTAL_COST = "TOTAL_COST";
        public static  final String STORE_ID = "bnicllive";
        public static  final String STORE_PASSWORD = "5D89D9DCB840278023";
        /*public static  final String STORE_ID = "bangl5d64ffba2ec45";
        public static  final String STORE_PASSWORD = "bangl5d64ffba2ec45@ssl";*/
        public static  final String TRANS_ID = "123456789098765";
    }
    public static class SharedPrefItemOMP{
        public static  final String globalPreferenceOmpInsurance = "OMP_INSURANCE";
        public static  final String globalPreferenceOmpInfo = "OMP_INFO";
        public static  final String TYPE = "TYPE";
        public static  final String TYPE_ID = "TYPE_ID";
        public static  final String CATEGORY = "CATEGORY";
        public static  final String CATEGORY_ID = "CATEGORY_ID";
        public static  final String BIRTH_DATE = "BIRTH_DATE";
        public static  final String STAY_PERIOD = "STAY_PERIOD";
        public static  final String STAY_PERIOD_ID = "STAY_PERIOD_ID";
        public static  final String STAY_MIN = "STAY_MIN";
        public static  final String STAY_MAX = "STAY_MAX";
        public static  final String FULL_NAME = "FULL_NAME";
        public static  final String PERMANENT_ADDRESS = "PERMANENT_ADDRESS";
        public static  final String PERMANENT_CITY = "PERMANENT_CITY";
        public static  final String MAILING_ADDRESS = "MAILING_ADDRESS";
        public static  final String MAILING_CITY = "MAILING_CITY";
        public static  final String MOBILE = "MOBILE";
        public static  final String EMAIL = "EMAIL";
        public static  final String PASSPORT_NO = "PASSPORT_NO";
        public static  final String PASSPORT_EX = "PASSPORT_EX";
        public static  final String AIRPORT = "AIRPORT";
        public static  final String VISITED_COUNTRY = "VISITED_COUNTRY";
        public static  final String CATEGORY_MEDICAL_NON_MEDICAL = "CATEGORY_MEDICAL_NON_MEDICAL";
    }
    public static class API{
        //public static final String URL = "http://onjonhossain.com/bnicl/api/";
        //public static final String URL = "http://192.168.37.111/work/csinfotech/bnic/api/";

        //public static final String URL2 = "http://192.168.37.93/bnic/api/";

        public static final String URL = "http://online.bnicl.net/api/";
        public static final String PLAN_TYPE = URL+"plan-type/list";
        public static final String CITY_LIST = URL+"city/list";
        public static final String YEAR_LIST = URL+"mfg-year/list";
        public static final String VEHICLE_TYPE = URL+"vehicle-type/list";
        public static final String FIRE_INSURANCE = URL+"fire-insurance/submit";
        public static final String MARINE_INSURANCE = URL+"marine-insurance/submit";
        public static final String MOTOR_INSURANCE = URL+"motor-insurance/submit"; /// note check this......
        public static final String GET_QUOTE = URL+"motor/price-quote";
        public static final String OMP_QUOTE = URL+"omp/price-quote";
        public static final String TYPE_LIST = URL+"insurance-sub-type/list";
        public static final String OMP_CATEGORY_LIST = URL+"insurance-category/list";
        public static final String OMP_SUBMIT = URL+"omp-insurance/submit";
        public static final String PERIOD_LIST = URL+"insurance-omp-charge/list";
        public static final String FACILITY_LIST = URL+"insurance-facility/list";
        public static final String AIRPORT_LIST = URL+"airport/list";
    }
}
