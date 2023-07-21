package qetaa.service.invoice.model.purchase;

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

@Entity
@Table(name="inv_purchase")
public class Purchase implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "inv_purchase_id_seq_gen", sequenceName = "inv_purchase_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_purchase_id_seq_gen")
	@Column(name = "id", updatable=false)
	private long id;
	@Column(name="vendor_id")
	private int vendorId;
	@Column(name="make_id")
	private int makeId;
	@Column(name="transaction_type")
	private char transactionType;
	@Column(name="payment_status")
	private char paymentStatus;
	@Column(name="purchase_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date purchaseDate;
	@Column(name="created_by")
	private int createdBy;
	@Column(name="due_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dueDate;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="customer_id")
	private long customerId;
	
	@Transient
	private List<PurchaseProduct> purchaseProducts;
	@Transient
	private List<PurchasePayment> purchasePayments;
	
	@Transient
	private List<PurchaseReturn> purchaseReturns;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getVendorId() {
		return vendorId;
	}
	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
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
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public List<PurchaseProduct> getPurchaseProducts() {
		return purchaseProducts;
	}
	public void setPurchaseProducts(List<PurchaseProduct> purchaseProducts) {
		this.purchaseProducts = purchaseProducts;
	}
	public List<PurchasePayment> getPurchasePayments() {
		return purchasePayments;
	}
	public void setPurchasePayments(List<PurchasePayment> purchasePayments) {
		this.purchasePayments = purchasePayments;
	}
	public long getCartId() {
		return cartId;
	}
	public void setCartId(long cartId) {
		this.cartId = cartId;
	}
	public int getMakeId() {
		return makeId;
	}
	public void setMakeId(int makeId) {
		this.makeId = makeId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public List<PurchaseReturn> getPurchaseReturns() {
		return purchaseReturns;
	}
	public void setPurchaseReturns(List<PurchaseReturn> purchaseReturns) {
		this.purchaseReturns = purchaseReturns;
	}
	
	
	
	
	
	
}
