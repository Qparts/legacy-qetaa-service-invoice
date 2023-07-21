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

import qetaa.service.invoice.model.sales.SalesReturnProduct;

@Entity
@Table(name="inv_purchase_return_product")
public class PurchaseReturnProduct implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "inv_purchase_return_product_id_seq_gen", sequenceName = "inv_purchase_return_product_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_purchase_return_product_id_seq_gen")
	@Column(name = "id", updatable=false)
	private long id;
	@Column(name="product_id")
	private long productId;
	@JoinColumn(name="return_id")
	@ManyToOne
	private PurchaseReturn purchaseReturn;
	@JoinColumn(name="purchase_product_id")
	@ManyToOne
	private PurchaseProduct purchaseProduct;
	@Column(name="quantity")
	private int quantity;
	@Column(name="unit_cost")
	private double unitCost;
	@Column(name="unit_cost_wv")
	private double unitCostWv;
	@JoinColumn(name="sales_return_product_id")
	@ManyToOne
	private SalesReturnProduct salesReturnProduct;
	
	
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
	public PurchaseReturn getPurchaseReturn() {
		return purchaseReturn;
	}
	public void setPurchaseReturn(PurchaseReturn purchaseReturn) {
		this.purchaseReturn = purchaseReturn;
	}
	public PurchaseProduct getPurchaseProduct() {
		return purchaseProduct;
	}
	public void setPurchaseProduct(PurchaseProduct purchaseProduct) {
		this.purchaseProduct = purchaseProduct;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}
	public double getUnitCostWv() {
		return unitCostWv;
	}
	public void setUnitCostWv(double unitCostWv) {
		this.unitCostWv = unitCostWv;
	}
	public SalesReturnProduct getSalesReturnProduct() {
		return salesReturnProduct;
	}
	public void setSalesReturnProduct(SalesReturnProduct salesReturnProduct) {
		this.salesReturnProduct = salesReturnProduct;
	}

}
