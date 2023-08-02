package qetaa.service.invoice.model.purchase;

public class PurchaseProductItem {

	private Long purchaseProductId;
	private Long walletItemId;
	private Integer purchaseProductQuantity;
	private Integer vendorId;
	
	
	public Integer getVendorId() {
		return vendorId;
	}
	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
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
	public Integer getPurchaseProductQuantity() {
		return purchaseProductQuantity;
	}
	public void setPurchaseProductQuantity(Integer purchaseProductQuantity) {
		this.purchaseProductQuantity = purchaseProductQuantity;
	}
	
	
}
