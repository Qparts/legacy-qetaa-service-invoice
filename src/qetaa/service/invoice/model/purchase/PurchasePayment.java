package qetaa.service.invoice.model.purchase;

import java.io.Serializable;
import java.util.Date;

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

@Entity
@Table(name="inv_purchase_payment")
public class PurchasePayment implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "inv_purchase_payment_id_seq_gen", sequenceName = "inv_purchase_payment_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_purchase_payment_id_seq_gen")
	@Column(name = "id", updatable=false)
	private long id;
	@JoinColumn(name="purchase_id")
	@ManyToOne
	private Purchase purchase;
	@JoinColumn(name="purchase_return_id")
	@ManyToOne
	private PurchaseReturn purchaseReturn;
	
	@Column(name="amount")
	private double amount;
	@Column(name="payment_ref")
	private String paymentRef;
	@Column(name="method")
	private char method;
	@Column(name="payment_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	@Column(name="paid_by")
	private int paidBy;
	@Column(name="bank_id")
	private Integer bankId;
	
	
	
	
	public PurchaseReturn getPurchaseReturn() {
		return purchaseReturn;
	}
	public void setPurchaseReturn(PurchaseReturn purchaseReturn) {
		this.purchaseReturn = purchaseReturn;
	}
	public Integer getBankId() {
		return bankId;
	}
	public void setBankId(Integer bankId) {
		this.bankId = bankId;
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
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getPaymentRef() {
		return paymentRef;
	}
	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}
	public char getMethod() {
		return method;
	}
	public void setMethod(char method) {
		this.method = method;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public int getPaidBy() {
		return paidBy;
	}
	public void setPaidBy(int paidBy) {
		this.paidBy = paidBy;
	}
}
