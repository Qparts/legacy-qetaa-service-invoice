package qetaa.service.invoice.model.purchase;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="inv_purchase_return")
public class PurchaseReturn implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "inv_purchase_return_id_seq_gen", sequenceName = "inv_purchase_return_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_purchase_return_id_seq_gen")
	@Column(name = "id", updatable=false)
	private long id;
	@JoinColumn(name="purchase_id")
	@ManyToOne
	private Purchase purchase;
	@Column(name="return_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date returnDate;
	@Column(name="method")
	private char method;
	@Column(name="returned_by")
	private int returnedBy;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="transaction_type")
	private char transactionType;
	@Transient
	private List<PurchaseReturnProduct> purchaseReturnProducts;
	@Transient
	private List<PurchasePayment> purchasePayments;
	
	
	
	
	public char getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(char transactionType) {
		this.transactionType = transactionType;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Purchase getPurchase() {
		return purchase;
	}
	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}
	public Date getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}
	public char getMethod() {
		return method;
	}
	public void setMethod(char method) {
		this.method = method;
	}
	public int getReturnedBy() {
		return returnedBy;
	}
	public void setReturnedBy(int returnedBy) {
		this.returnedBy = returnedBy;
	}
	public long getCartId() {
		return cartId;
	}
	public void setCartId(long cartId) {
		this.cartId = cartId;
	}
	public List<PurchaseReturnProduct> getPurchaseReturnProducts() {
		return purchaseReturnProducts;
	}
	public void setPurchaseReturnProducts(List<PurchaseReturnProduct> purchaseReturnProducts) {
		this.purchaseReturnProducts = purchaseReturnProducts;
	}
	public List<PurchasePayment> getPurchasePayments() {
		return purchasePayments;
	}
	public void setPurchasePayments(List<PurchasePayment> purchasePayments) {
		this.purchasePayments = purchasePayments;
	}
	
	
	
	
	
	

}
