package qetaa.service.invoice.model.sales;

public class SalesProductItem {

	private Long salesProductId;
	private Long purchaseProductId;
	private Long walletItemId;
	private Integer salesProductQuantity;
	
	
	
	public Long getSalesProductId() {
		return salesProductId;
	}
	public void setSalesProductId(Long salesProductId) {
		this.salesProductId = salesProductId;
	}
	public Integer getSalesProductQuantity() {
		return salesProductQuantity;
	}
	public void setSalesProductQuantity(Integer salesProductQuantity) {
		this.salesProductQuantity = salesProductQuantity;
	}
	public Long getPurchaseProductId() {
		return purchaseProductId;
	}
	public void setPurchaseProductId(Long purchaseProductId) {
		this.purchaseProductId = purchaseProductId;
	}
	public Long getWalletItemId() {
		return walletItemId;
	}
	public void setWalletItemId(Long walletItemId) {
		this.walletItemId = walletItemId;
	}
	
}
