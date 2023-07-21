package qetaa.service.invoice.model.sales;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="inv_sales")
public class Sales implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "inv_sales_id_seq_gen", sequenceName = "inv_sales_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_sales_id_seq_gen")
	@Column(name = "id", updatable=false)
	private long id;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="make_id")
	private int makeId;
	@Column(name="customer_id")
	private long customerId;
	@Column(name="sales_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date salesDate;
	@Column(name="due_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dueDate;
	@Column(name="transaction_type")
	private char transactionType;
	@Column(name="payment_status")
	private char paymentStatus;
	@Column(name="created_by")
	private int createdBy;
	
	@Column(name="delivery_fees")
	private double deliveryFees;
	@Column(name="vat_percentage")
	private double vatPercentage;
	@Column(name="shipment_fees")
	private double shipmentFees;
	@Column(name="promotion_discount")
	private double promotionDiscount;
	@Column(name="promotion_id")
	private Integer promotionId;
	@Column(name="courrier_name")
	private String courrierName;
	@Column(name="shipment_reference")
	private String shipmentReference;
	
	@Transient
	private List<SalesProduct> salesProducts;
	@Transient
	private List<SalesPayment> salesPayments;
	
	@Transient
	private List<SalesReturn> salesReturns;
	
	

	@JsonIgnore
	public double getTotalPartsWvAmount() {
		double total = 0;
		if(this.salesProducts != null) {
			for(SalesProduct sp : salesProducts) {
				total  = total + sp.getUnitSalesWv() * sp.getQuantity();
			}
		}
		return total;
	}
	
	@JsonIgnore
	public double getTotalDeliveryFees() {
		return deliveryFees + deliveryFees * this.vatPercentage;
	}
	
	@JsonIgnore
	public double getTotalSales() {
		return this.getTotalPartsAmount() + this.deliveryFees;
	}
	
	@JsonIgnore
	public double getTotalPartsAmount() {
		double total = 0;
		if(this.salesProducts != null) {
			for(SalesProduct sp : salesProducts) {
				total  = total + sp.getUnitSales() * sp.getQuantity();
			}
		}
		return total;
	}
	
	public int getMakeId() {
		return makeId;
	}
	public void setMakeId(int makeId) {
		this.makeId = makeId;
	}
	public List<SalesProduct> getSalesProducts() {
		return salesProducts;
	}
	public void setSalesProducts(List<SalesProduct> salesProducts) {
		this.salesProducts = salesProducts;
	}
	public List<SalesPayment> getSalesPayments() {
		return salesPayments;
	}
	public void setSalesPayments(List<SalesPayment> salesPayments) {
		this.salesPayments = salesPayments;
	}
	public double getDeliveryFees() {
		return deliveryFees;
	}
	public void setDeliveryFees(double deliveryFees) {
		this.deliveryFees = deliveryFees;
	}
	public double getVatPercentage() {
		return vatPercentage;
	}
	public void setVatPercentage(double vatPercentage) {
		this.vatPercentage = vatPercentage;
	}
	public String getShipmentReference() {
		return shipmentReference;
	}
	public void setShipmentReference(String shipmentReference) {
		this.shipmentReference = shipmentReference;
	}
	public double getShipmentFees() {
		return shipmentFees;
	}
	public void setShipmentFees(double shipmentFees) {
		this.shipmentFees = shipmentFees;
	}

	public double getPromotionDiscount() {
		return promotionDiscount;
	}
	public void setPromotionDiscount(double promotionDiscount) {
		this.promotionDiscount = promotionDiscount;
	}
	public Integer getPromotionId() {
		return promotionId;
	}
	public void setPromotionId(Integer promotionId) {
		this.promotionId = promotionId;
	}
	public String getCourrierName() {
		return courrierName;
	}
	public void setCourrierName(String courrierName) {
		this.courrierName = courrierName;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCartId() {
		return cartId;
	}
	public void setCartId(long cartId) {
		this.cartId = cartId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public Date getSalesDate() {
		return salesDate;
	}
	public void setSalesDate(Date salesDate) {
		this.salesDate = salesDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public char getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(char transactionType) {
		this.transactionType = transactionType;
	}
	public char getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(char paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public List<SalesReturn> getSalesReturns() {
		return salesReturns;
	}
	public void setSalesReturns(List<SalesReturn> salesReturns) {
		this.salesReturns = salesReturns;
	}
	
	
	
	
}
