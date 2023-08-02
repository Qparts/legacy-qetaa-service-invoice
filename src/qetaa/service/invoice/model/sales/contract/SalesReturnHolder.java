package qetaa.service.invoice.model.sales.contract;

import java.util.List;

import qetaa.service.invoice.model.purchase.Purchase;
import qetaa.service.invoice.model.sales.SalesReturn;

public class SalesReturnHolder {
	private SalesReturn salesReturn;
	private List<Purchase> purchases;
	private boolean purchasesComplete;
	
	public SalesReturn getSalesReturn() {
		return salesReturn;
	}
	public void setSalesReturn(SalesReturn salesReturn) {
		this.salesReturn = salesReturn;
	}
	public List<Purchase> getPurchases() {
		return purchases;
	}
	public void setPurchases(List<Purchase> purchases) {
		this.purchases = purchases;
	}
	public boolean isPurchasesComplete() {
		return purchasesComplete;
	}
	public void setPurchasesComplete(boolean purchasesComplete) {
		this.purchasesComplete = purchasesComplete;
	}
	
	
	

	
}
