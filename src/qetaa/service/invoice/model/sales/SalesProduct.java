package qetaa.service.invoice.model.sales;

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

import qetaa.service.invoice.model.purchase.PurchaseProduct;

@Entity
@Table(name="inv_sales_product")
public class SalesProduct implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "inv_sales_product_id_seq_gen", sequenceName = "inv_sales_product_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_sales_product_id_seq_gen")
	@Column(name = "id", updatable=false)
	private long id;
	@JoinColumn(name="sales_id")
	@ManyToOne
	private Sales sales;
	@Column(name="product_id")
	private long productId;
	@Column(name="quantity")
	private int quantity;
	@Column(name="unit_sales")
	private double unitSales;
	@Column(name="unit_sales_wv")
	private double unitSalesWv;
	@Column(name="unit_cost")
	private double unitCost;
	@Column(name="unit_cost_wv")
	private double unitCostWv;
	@JoinColumn(name="purchase_product_id")
	@ManyToOne
	private PurchaseProduct purchaseProduct;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Sales getSales() {
		return sales;
	}
	public void setSales(Sales sales) {
		this.sales = sales;
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
	public double getUnitSales() {
		return unitSales;
	}
	public void setUnitSales(double unitSales) {
		this.unitSales = unitSales;
	}
	public double getUnitSalesWv() {
		return unitSalesWv;
	}
	public void setUnitSalesWv(double unitSalesWv) {
		this.unitSalesWv = unitSalesWv;
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
	public PurchaseProduct getPurchaseProduct() {
		return purchaseProduct;
	}
	public void setPurchaseProduct(PurchaseProduct purchaseProduct) {
		this.purchaseProduct = purchaseProduct;
	}
	
	
}
