package qetaa.service.invoice.model.sales;

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
@Table(name="inv_sales_payment")
public class SalesPayment implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "inv_sales_payment_id_seq_gen", sequenceName = "inv_sales_payment_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_sales_payment_id_seq_gen")
	@Column(name = "id", updatable=false)
	private long id;
	@JoinColumn(name="sales_id")
	@ManyToOne
	private Sales sales;
	@JoinColumn(name="sales_return_id")
	@ManyToOne
	private SalesReturn salesReturn;
	@Column(name="amount")
	private double amount;
	@Column(name="payment_ref")
	private String paymentRef;
	@Column(name="method")
	private char method;
	@Column(name="payment_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	@Column(name="payment_provider")
	private String provider;
	@Column(name="credit_fees")
	private Double creditFees;
	@Column(name="bank_id")
	private Integer bankId;
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
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public Double getCreditFees() {
		return creditFees;
	}
	public void setCreditFees(Double creditFees) {
		this.creditFees = creditFees;
	}
	public Integer getBankId() {
		return bankId;
	}
	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}
	public SalesReturn getSalesReturn() {
		return salesReturn;
	}
	public void setSalesReturn(SalesReturn salesReturn) {
		this.salesReturn = salesReturn;
	}
	
	
	
}
