package app.atelier.classes;

public class Constants {

    public static final String BASE_URL1 = "http://atelierq8.com";
    // public static final String BASE_URL1 = "http://demo.atelierq8.com";

    public static final String BASE_URL = BASE_URL1 + "/api/";
    //http://demo.atelierq8.com/
    public static final String AUTHORIZATION = "Authorization";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String LIMIT = "10";
    public static final String CONTENT_TYPE_VALUE = "application/json";
    public static final String AUTHORIZATION_VALUE = "Bearer " +
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.ey" +
            "JuYmYiOjE1NjczMjYyMzMsImV4cCI6MTg4MjY4NjIzM" +
            "ywiaXNzIjoiaHR0cDovL2F0ZWxpZXIuaGFyZHRhc2suaW5m" +
            "byIsImF1ZCI6WyJodHRwOi8vYXRlbGllci5oYXJkdGFzay5pbm" +
            "ZvL3Jlc291cmNlcyIsImh0X2FwaSJdLCJjbGllbnRfaWQiOiIwZ" +
            "TcxMGMyMi04N2QyLTQzZWMtYjFmNy1iY2E4ZWI5MDE4NDIiLCJz" +
            "dWIiOiIwZTcxMGMyMi04N2QyLTQzZWMtYjFmNy1iY2E4ZWI5MDE4ND" +
            "IiLCJhdXRoX3RpbWUiOjE1NjczMjYyMzEsImlkcCI6ImxvY2FsIiwi" +
            "c2NvcGUiOlsiaHRfYXBpIiwib2ZmbGluZV9hY2Nlc3MiXSwiYW1yIjpbIn" +
            "B3ZCJdfQ.oz7deZ9gisNWpsL5lQqfAmE6baHoprL3iJcp4-3YgW2w9GpAv4wu" +
            "aWeNelY2c2s37ylEbA_R1j42zSNJ37CnF6rHv-1FX4kzJdVVE02Z3XkP4A6KxBeK" +
            "SLYJHp2bjhZIf-ftuFxplpQcAj-Xeu8TVS_nNtObztqSjhISRh6h0jrGDInF0ib" +
            "HnhaFWnAX51oCKlypDDi_gvbnf9cBpAfrBLIgZOUK4ZQhAZ-JlRUpuRwKMPSo7" +
            "uBiGeP_OFIgvyXXLh2pBlxzNtKyZ8BJFktfAdhADDrAKQnpn1kOORUhp14VePvn" +
            "IN90N2-MnH06S-Q5mFY9IUJHXz2luPQ_2hsgWQ";

    public final static int NormalUserRoleId = 3;
    public final static String DOWNLOAD_INVOICE = BASE_URL + "/" + "orders/{orderId}/pdf";
    public final static String ORDER_COMPLETE_STATUS = "complete";
    public final static String PAYMENT_STATUS_PAID = "paid";
    public final static String FACEBOOK = "storeinformationsettings.facebooklink";
    public final static String INSTAGRAM = "storeinformationsettings.instagramlink";
    public final static String TWITTER = "storeinformationsettings.twitterlink";
    public final static String YOUTUBE = "storeinformationsettings.youtubelink";

    public final static String SUCCESS_PAGE = "atelierq8.";
    public final static String ERROR_PAGE = "error.aspx";

    public static final int NOTIFICATION_ID = 100;

    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String PUSH_NOTIFICATION = "pushNotification";

    public static String CMS_NOTIFICATION_IMAGE_URL = BASE_URL1 + "/images/thumbs/";


}
