package app.atelier.classes;

public class Constants {
    public static final String BASE_URL = "http://atelier.hardtask.info/api/";
    public static final String USER_PREF = "user-pref";
    public static final String SKIP_PREF = "skip-pref";
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







    public final static String[] ShoppingCartType = {"ShoppingCart", "Wishlist"};

    public final static String ContactUsId = "4";

    public final static String TermsConditionId = "3";

    public final static int NormalUserRoleId = 3;

    public final static int GuestUserRoleId = 4;

    public final static int KnetId = 1;

    public final static int CreditCardId = 2;


    public final static String DOWNLOAD_INVOICE = BASE_URL + "/" + "orders/aaa/pdf";

    public final static String ORDER_COMPLETE_STATUS = "complete";

    public final static String ORDER_PENDING_STATUS = "pending";

    public final static String PAYMENT_STATUS_PAID = "paid";

    public final static String PAYMENT_STATUS_PENDING = "pending";

    public final static String PAYMENT_METHOD_NAME = "Payments.Hesabe";

    public static final String CODE="Code";
}
