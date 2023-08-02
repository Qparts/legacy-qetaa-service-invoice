package qetaa.service.invoice.model.sales;

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
@Table(name="inv_sales_return")
public class SalesReturn implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "inv_sales_return_id_seq_gen", sequenceName = "inv_sales_return_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_sales_return_id_seq_gen")
	@Column(name = "id", updatable=false)
	private long id;
	
	@JoinColumn(name="sales_id")
	@ManyToOne
	private Sales sales;
	
	@Column(name="return_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date returnDate;
	@Column(name="method")
	private char method;
	@Column(name="returned_by")
	private int returnBy;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="shipment_fees")
	private double shipmentFees;
	@Column(name="courrier_name")
	private String courrierName;
	@Column(name="shipment_reference")
	private String shipmentReference;
	@Column(name="vat_percentge")
	private double vatPercentage;
	@Column(name="promotion_discount")
	private Double promotionDiscount;
	@Column(name="promotion_id")
	private Integer promotionId;
	@Column(name="bank_id")
	private Integer bankId;
	@Column(name="returned_delivery_fees")
	private Double returnedDeliveryFees;
	
	@Transient
	private List<SalesReturnProduct> salesReturnProducts;
	@Transient
	private List<SalesPayment> salesPayments;
	
	
	
	
	
	public Integer getBankId() {
		return bankId;
	}
	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}
	public List<SalesReturnProduct> getSalesReturnProducts() {
		return salesReturnProducts;
	}
	public void setSalesReturnProducts(List<SalesReturnProduct> salesReturnProducts) {
		this.salesReturnProducts = salesReturnProducts;
	}
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
	public int getReturnBy() {
		return returnBy;
	}
	public void setReturnBy(int returnBy) {
		this.returnBy = returnBy;
	}
	public long getCartId() {
		return cartId;
	}
	public void setCartId(long cartId) {
		this.cartId = cartId;
	}
	public double getShipmentFees() {
		return shipmentFees;
	}
	public void setShipmentFees(double shipmentFees) {
		this.shipmentFees = shipmentFees;
	}
	public String getCourrierName() {
		return courrierName;
	}
	public void setCourrierName(String courrierName) {
		this.courrierName = courrierName;
	}
	public String getShipmentReference() {
		return shipmentReference;
	}
	public void setShipmentReference(String shipmentReference) {
		this.shipmentReference = shipmentReference;
	}
	public double getVatPercentage() {
		return vatPercentage;
	}
	public void setVatPercentage(double vatPercentage) {
		this.vatPercentage = vatPercentage;
	}
	public Double getPromotionDiscount() {
		return promotionDiscount;
	}
	public void setPromotionDiscount(Double promotionDiscount) {
		this.promotionDiscount = promotionDiscount;
	}
	public Integer getPromotionId() {
		return promotionId;
	}
	public void setPromotionId(Integer promotionId) {
		this.promotionId = promotionId;
	}
	public List<SalesPayment> getSalesPayments() {
		return salesPayments;
	}
	public void setSalesPayments(List<SalesPayment> salesPayments) {
		this.salesPayments = salesPayments;
	}
	public Double getReturnedDeliveryFees() {
		return returnedDeliveryFees;
	}
	public void setReturnedDeliveryFees(Double returnedDeliveryFees) {
		this.returnedDeliveryFees = returnedDeliveryFees;
	}
	
	
	
	

}
