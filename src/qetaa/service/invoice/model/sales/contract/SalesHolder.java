package qetaa.service.invoice.model.sales.contract;

import java.util.List;

import qetaa.service.invoice.model.purchase.Purchase;
import qetaa.service.invoice.model.sales.Sales;

public class SalesHolder {

	private Sales sales;
	private List<Purchase> purchases;
	private boolean purchasesComplete;
	
	
	public boolean isPurchasesComplete() {
		return purchasesComplete;
	}
	public void setPurchasesComplete(boolean purchasesComplete) {
		this.purchasesComplete = purchasesComplete;
	}
	public Sales getSales() {
		return sales;
	}
	public void setSales(Sales sales) {
		this.sales = sales;
	}
	public List<Purchase> getPurchases() {
		return purchases;
	}
	public void setPurchases(List<Purchase> purchases) {
		this.purchases = purchases;
	}
	
	
	
}
