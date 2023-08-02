package qetaa.service.invoice.model.sales.contract;

import java.io.Serializable;

import qetaa.service.invoice.model.sales.SalesReturn;

public class SalesReturnWallet implements Serializable{ 

	private static final long serialVersionUID = 1L;
	private SalesReturn salesReturn;
	private String customerName;
	private Long customerId;
	private Integer bankId;
	private Double discountPercentage;
	
	
	public SalesReturn getSalesReturn() {
		return salesReturn;
	}
	public void setSalesReturn(SalesReturn salesReturn) {
		this.salesReturn = salesReturn;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public Integer getBankId() {
		return bankId;
	}
	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}
	public Double getDiscountPercentage() {
		return discountPercentage;
	}
	public void setDiscountPercentage(Double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	
	
	
}
