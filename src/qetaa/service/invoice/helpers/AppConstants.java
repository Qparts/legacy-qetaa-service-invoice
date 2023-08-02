package qetaa.service.invoice.helpers;rpackage qetaa.service.invoice.helpers;

public final class AppConstants {
	private static final String CUSTOMER_SERVICE = "http://localhost:8080/service-qetaa-customer/rest/";
	private static final String USER_SERVICE = "http://localhost:8080/service-qetaa-user/rest/";
	private static final String PRODUCT_SERVICE = "http://localhost:8080/service-qetaa-product/rest/";
	private static final String PAYMENT_SERVICE = "http://localhost:8080/service-qetaa-payment/rest/";
	
	public static final String CUSTOMER_MATCH_TOKEN = CUSTOMER_SERVICE + "match-token";
	public static final String USER_MATCH_TOKEN = USER_SERVICE + "match-token";
	
	public static final String POST_PRODUCT_STOCK = PRODUCT_SERVICE + "product-stocks";
	public static final String PUT_PRODUCT_STOCK = PRODUCT_SERVICE + "product-stocks";
	public static final String PUT_RETURN_SALES_PRODUCT_STOCK = PRODUCT_SERVICE + "product-stocks/return-sales";
	public static final String PUT_RETURN_PURCHASE_PRODUCT_STOCK = PRODUCT_SERVICE + "product-stocks/return-purchase";
	public static final String GET_SALES_RETURN_IDS_FROM_STOCK = PRODUCT_SERVICE +"sales-return-ids-in-stock";
	public static final String PUT_PURCHASED_WALLET_ITEM = PAYMENT_SERVICE + "wallet-item-purchase";
	public static final String PUT_SALES_WALLET_ITEM = PAYMENT_SERVICE + "wallet-item-sales";
	
	public final static String POST_NEW_WALLET_SALES_RETURN = PAYMENT_SERVICE + "new-wallet/sales-return";
	public final static String PUT_SALES_RETURN_WALLET = PAYMENT_SERVICE + "sales-return-wallet";
	public final static String PUT_REPLACE_WALLET_ITEM_PRODUCT_ID = PAYMENT_SERVICE + "replace-wallet-item-product";
}
