package qetaa.service.invoice.model.purchase;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import qetaa.service.invoice.model.sales.SalesProduct;

@Entity
@Table(name="inv_purchase_product")
public class PurchaseProduct implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "inv_purchase_product_id_seq_gen", sequenceName = "inv_purchase_product_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_purchase_product_id_seq_gen")
	@Column(name = "id", updatable=false)
	private long id;
	@Column(name="product_id")
	private long productId;	
	@Column(name="quantity")
	private int quantity;
	@JoinColumn(name="purchase_id")
	@ManyToOne
	private Purchase purchase;
	@Column(name="unit_cost")
	private Double unitCost;
	@Column(name="unit_cost_wv")
	private Double unitCostWv;
	@Column(name="wallet_item_id")
	private Long walletItemId;
	
	
	@Transient
	private SalesProduct salesProduct;
	
	
	
	
	public SalesProduct getSalesProduct() {
		return salesProduct;
	}
	public void setSalesProduct(SalesProduct salesProduct) {
		this.salesProduct = salesProduct;
	}
	public Long getWalletItemId() {
		return walletItemId;
	}
	public void setWalletItemId(Long walletId) {
		this.walletItemId = walletId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Purchase getPurchase() {
		return purchase;
	}
	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}
	public Double getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}
	public Double getUnitCostWv() {
		return unitCostWv;
	}
	public void setUnitCostWv(Double unitCostWv) {
		this.unitCostWv = unitCostWv;
	}
	
}
