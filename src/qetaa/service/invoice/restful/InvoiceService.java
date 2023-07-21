package qetaa.service.invoice.restful;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import qetaa.service.invoice.dao.DAO;
import qetaa.service.invoice.filters.Secured;
import qetaa.service.invoice.filters.SecuredCustomer;
import qetaa.service.invoice.filters.SecuredUser;
import qetaa.service.invoice.filters.ValidApp;
import qetaa.service.invoice.helpers.AppConstants;
import qetaa.service.invoice.helpers.Helper;
import qetaa.service.invoice.model.purchase.Purchase;
import qetaa.service.invoice.model.purchase.PurchasePayment;
import qetaa.service.invoice.model.purchase.PurchaseProduct;
import qetaa.service.invoice.model.purchase.PurchaseReturn;
import qetaa.service.invoice.model.purchase.PurchaseReturnProduct;
import qetaa.service.invoice.model.sales.Sales;
import qetaa.service.invoice.model.sales.SalesPayment;
import qetaa.service.invoice.model.sales.SalesProduct;
import qetaa.service.invoice.model.sales.SalesReturn;
import qetaa.service.invoice.model.sales.SalesReturnProduct;;

@Path("/")
public class InvoiceService {
	@EJB
	private DAO dao;

	@Secured
	@GET
	@SecuredCustomer
	@ValidApp
	@SecuredUser
	public void test() {

	}
	
	@SecuredUser
	@POST
	@Path("search-purchases")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchPurchases(Map<String, Object> map) {
		try {
			List<Number> productIds = (List<Number>) map.get("productIds");
			List<Number> customerIds = (List<Number>) map.get("customerIds");
			Number fromTime = (Number) map.get("from");
			Number toTime = (Number) map.get("to");
			Number makeId = (Number) map.get("makeId");
			Number cartId = (Number) map.get("cartId");
			
			String sql = "select * from inv_purchase where id > 0 ";
			
			if (cartId != null && cartId.intValue() > 0) {
				sql = sql + "and cart_id = " + cartId;
			}
			
			if (!customerIds.isEmpty()) {
				sql = sql + "and customer_id in (0";
				for (Number cid : customerIds) {
					sql = sql + "," + cid.longValue();
				}
				sql = sql + ")";
			}
			
			if(makeId != null && makeId.intValue() != 0) {
				sql = sql + " and make_id = " + makeId.intValue();
			}
			
			if(fromTime != null && toTime == null) {
				toTime = new Date().getTime();
			}
			
			if (fromTime != null && toTime != null && fromTime.longValue() != 0 && toTime.longValue() != 0) {
				Helper h = new Helper();
				String fromString = h.getDateFormat(new Date(fromTime.longValue()), "yyyy-MM-dd");
				String toString = h.getDateFormat(new Date(toTime.longValue()), "yyyy-MM-dd");
				sql = sql + " and purchase_date\\:\\:date between '" + fromString + "' and '" + toString + "' ";
			}
			if (!productIds.isEmpty()) {
				sql = sql + " and id in (select p.purchase_id from inv_purchase_product p where p.product_id in (0";
				for (Number pid : productIds) {
					sql = sql + "," + pid.longValue();
				}
				sql = sql + "))";
			}
			List<Purchase> purchases = dao.getNative(Purchase.class, sql);
			for(Purchase s : purchases) {
				List<PurchaseProduct> sp = dao.getCondition(PurchaseProduct.class, "purchase", s);
				s.setPurchaseProducts(sp);
				List<PurchasePayment> ss = dao.getCondition(PurchasePayment.class, "purchase", s);
				s.setPurchasePayments(ss);
			}
			return Response.status(200).entity(purchases).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("search-sales")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchSales(Map<String, Object> map) {
		try {
			List<Number> productIds = (List<Number>) map.get("productIds");
			List<Number> customerIds = (List<Number>) map.get("customerIds");
			Number fromTime = (Number) map.get("from");
			Number toTime = (Number) map.get("to");
			String courrier = (String) map.get("courrier");
			Number promoCode = (Number) map.get("promoCode");
			Number makeId = (Number) map.get("makeId");
			Number cartId = (Number) map.get("cartId");
			
			String sql = "select * from inv_sales where id > 0 ";
			
			if (cartId != null && cartId.intValue() > 0) {
				sql = sql + "and cart_id = " + cartId;
			}
			
			if (!customerIds.isEmpty()) {
				sql = sql + "and customer_id in (0";
				for (Number cid : customerIds) {
					sql = sql + "," + cid.longValue();
				}
				sql = sql + ")";
			}
			if (courrier != null && courrier.trim().length() > 0) {
				sql = sql + " and courrier_name = '" + courrier + "' ";
			}
			
			else if(promoCode != null) {
				sql = sql + " and promotion_id = '" + promoCode + "' ";
			}
			
			if(makeId != null && makeId.intValue() != 0) {
				sql = sql + " and make_id = " + makeId.intValue();
			}
			
			if(fromTime != null && toTime == null) {
				toTime = new Date().getTime();
			}
			
			if (fromTime != null && toTime != null && fromTime.longValue() != 0 && toTime.longValue() != 0) {
				Helper h = new Helper();
				String fromString = h.getDateFormat(new Date(fromTime.longValue()), "yyyy-MM-dd");
				String toString = h.getDateFormat(new Date(toTime.longValue()), "yyyy-MM-dd");
				sql = sql + " and sales_date\\:\\:date between '" + fromString + "' and '" + toString + "' ";
			}
			if (!productIds.isEmpty()) {
				sql = sql + " and id in (select p.sales_id from inv_sales_product p where p.product_id in (0";
				for (Number pid : productIds) {
					sql = sql + "," + pid.longValue();
				}
				sql = sql + "))";
			}
			List<Sales> sales = dao.getNative(Sales.class, sql);
			for(Sales s : sales) {
				List<SalesProduct> sp = dao.getCondition(SalesProduct.class, "sales", s);
				s.setSalesProducts(sp);
				List<SalesPayment> ss = dao.getCondition(SalesPayment.class, "sales", s);
				s.setSalesPayments(ss);
			}
			return Response.status(200).entity(sales).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("sales")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateSales(@HeaderParam("Authorization") String authHeader, Sales sales) {
		try {
			sales.setSalesDate(new Date());
			dao.update(sales);
			for (SalesProduct sp : sales.getSalesProducts()) {
				// check if created before
				String jpql = "select b from SalesProduct b where b.sales.id = :value0 and b.productId = :value1 and b.purchaseProduct.id = :value2";
				List<SalesProduct> check = dao.getJPQLParams(SalesProduct.class, jpql, sales.getId(), sp.getProductId(),
						sp.getPurchaseProduct().getId());
				if (check.isEmpty()) {
					sp.setSales(sales);
					dao.persist(sp);
				}
			}

			// create payment
			if (sales.getTransactionType() == 'C') {
				String jpql = "select b from SalesPayment b where b.sales.id = :value0";
				List<SalesPayment> check = dao.getJPQLParams(SalesPayment.class, jpql, sales.getId());
				if (check.isEmpty()) {
					SalesPayment payment = sales.getSalesPayments().get(0);
					payment.setPaymentDate(new Date());
					payment.setSales(sales);
					dao.persist(payment);
				}
			}

			List<Map<String, Long>> list = new ArrayList<>();
			for (SalesProduct sp : sales.getSalesProducts()) {
				Map<String, Long> map = new HashMap<String, Long>();
				map.put("cartId", sales.getCartId());
				map.put("purchaseId", sp.getPurchaseProduct().getPurchase().getId());
				map.put("quantity", Integer.valueOf(sp.getQuantity()).longValue());
				map.put("productId", sp.getProductId());
				list.add(map);
			}
			putSecuredRequest(AppConstants.PUT_PRODUCT_STOCK, list, authHeader);
			return Response.status(201).build();

		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	// idempotent, update newly created
	@SecuredUser
	@PUT
	@Path("purchase")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePurchase(@HeaderParam("Authorization") String authHeader, Purchase purchase) {
		purchase.setPurchaseDate(new Date());
		dao.update(purchase);
		for (PurchaseProduct pp : purchase.getPurchaseProducts()) {
			// check if created before
			String jpql = "select b from PurchaseProduct b where b.purchase.id = :value0 and b.productId = :value1";
			List<PurchaseProduct> check = dao.getJPQLParams(PurchaseProduct.class, jpql, purchase.getId(),
					pp.getProductId());
			if (check.isEmpty()) {
				pp.setPurchase(purchase);
				dao.persist(pp);
			}
		}

		// create payment
		if (purchase.getTransactionType() == 'C') {
			String jpql = "select b from PurchasePayment b where b.purchase.id = :value0";
			List<PurchasePayment> check = dao.getJPQLParams(PurchasePayment.class, jpql, purchase.getId());
			if (check.isEmpty()) {
				PurchasePayment payment = purchase.getPurchasePayments().get(0);
				payment.setPaymentDate(new Date());
				payment.setPurchase(purchase);
				dao.persist(payment);
			}
		}

		List<Map<String, Long>> list = new ArrayList<>();
		for (PurchaseProduct pp : purchase.getPurchaseProducts()) {
			Map<String, Long> map = new HashMap<String, Long>();
			map.put("cartId", purchase.getCartId());
			map.put("purchaseId", purchase.getId());
			map.put("quantity", Integer.valueOf(pp.getQuantity()).longValue());
			map.put("productId", pp.getProductId());
			list.add(map);
		}
		Response r = this.postSecuredRequest(AppConstants.POST_PRODUCT_STOCK, list, authHeader);
		return Response.status(201).build();
	}

	@SecuredUser
	@POST
	@Path("new-purchase-return")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEmptyPurchaseReturn() {
		try {
			PurchaseReturn pr = new PurchaseReturn();
			pr.setCartId(0);
			pr.setMethod('R');
			pr.setReturnedBy(0);
			pr.setTransactionType('C');
			dao.persist(pr);
			return Response.status(200).entity(pr.getId()).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("purchases/cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartPurchase(@PathParam(value = "param") long cartId) {
		try {
			List<Purchase> purchases = dao.getCondition(Purchase.class, "cartId", cartId);
			for (Purchase p : purchases) {
				List<PurchaseProduct> pps = dao.getCondition(PurchaseProduct.class, "purchase", p);
				p.setPurchaseProducts(pps);
			}
			return Response.status(200).entity(purchases).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("sales-returns/purchase-not-returned")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesReturnsAvailableForPurchaseReturns(@HeaderParam("Authorization") String authHeader) {
		try {
			Response r = this.getSecuredRequest(AppConstants.GET_SALES_RETURN_IDS_FROM_STOCK, authHeader);
			if (r.getStatus() == 200) {
				List<Long> returnIds = r.readEntity(new GenericType<List<Long>>() {
				});
				if (returnIds == null || returnIds.isEmpty()) {
					return Response.status(404).build();
				} else {
					List<SalesReturn> salesReturns = new ArrayList<>();
					for (Long id : returnIds) {
						SalesReturn sr = dao.find(SalesReturn.class, id);
						salesReturns.add(sr);
					}
					return Response.status(200).entity(salesReturns).build();
				}
			} else {
				return Response.status(404).build();
			}
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("sales-return/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesReturn(@PathParam(value = "param") long srid) {
		try {
			SalesReturn sr = dao.find(SalesReturn.class, srid);
			List<SalesReturnProduct> srps = dao.getCondition(SalesReturnProduct.class, "salesReturn", sr);
			sr.setSalesReturnProducts(srps);
			return Response.status(200).entity(sr).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("purchase-return")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePurchaseReturn(@HeaderParam("Authorization") String authHeader,
			PurchaseReturn purchaseReturn) {
		try {
			purchaseReturn.setReturnDate(new Date());
			dao.update(purchaseReturn);
			for (PurchaseReturnProduct prp : purchaseReturn.getPurchaseReturnProducts()) {
				String jpql = "select b from PurchaseReturnProduct b where b.purchaseReturn.id = :value0 and b.purchaseProduct.id = :value1";
				List<PurchaseReturnProduct> check = dao.getJPQLParams(PurchaseReturnProduct.class, jpql,
						purchaseReturn.getId(), prp.getPurchaseProduct().getId());
				if (check.isEmpty()) {
					prp.setPurchaseReturn(purchaseReturn);
					dao.persist(prp);
				} else {
					throw new Exception();
				}
			}
			// create credit payment
			PurchasePayment payment = purchaseReturn.getPurchasePayments().get(0);
			payment.setPaymentDate(new Date());
			payment.setPurchaseReturn(purchaseReturn);
			dao.persist(payment);

			// remove stock
			List<Map<String, Long>> list = new ArrayList<>();
			for (PurchaseReturnProduct prp : purchaseReturn.getPurchaseReturnProducts()) {
				Map<String, Long> map = new HashMap<String, Long>();
				map.put("cartId", prp.getSalesReturnProduct().getSalesReturn().getCartId());
				map.put("purchaseId", prp.getPurchaseProduct().getPurchase().getId());
				map.put("quantity", Integer.valueOf(prp.getQuantity()).longValue());
				map.put("productId", prp.getProductId());
				map.put("salesReturnId", prp.getSalesReturnProduct().getSalesReturn().getId());
				list.add(map);
			}
			putSecuredRequest(AppConstants.PUT_RETURN_PURCHASE_PRODUCT_STOCK, list, authHeader);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("sales-return")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateSalesReturn(@HeaderParam("Authorization") String authHeader, SalesReturn salesReturn) {
		try {
			salesReturn.setReturnDate(new Date());
			dao.update(salesReturn);
			for (SalesReturnProduct srp : salesReturn.getSalesReturnProducts()) {
				// check if created before
				String jpql = "select b from SalesReturnProduct b where b.salesReturn.id = :value0 and b.purchaseProduct.id = :value1";
				List<SalesReturnProduct> check = dao.getJPQLParams(SalesReturnProduct.class, jpql, salesReturn.getId(),
						srp.getPurchaseProduct().getId());
				if (check.isEmpty()) {
					srp.setSalesReturn(salesReturn);
					dao.persist(srp);
				}
			}

			List<Map<String, Long>> list = new ArrayList<>();
			for (SalesReturnProduct srp : salesReturn.getSalesReturnProducts()) {
				Map<String, Long> map = new HashMap<String, Long>();
				map.put("cartId", salesReturn.getCartId());
				map.put("purchaseId", srp.getPurchaseProduct().getPurchase().getId());
				map.put("quantity", Integer.valueOf(srp.getQuantity()).longValue());
				map.put("productId", srp.getProductId());
				map.put("salesReturnId", salesReturn.getId());
				list.add(map);
			}

			putSecuredRequest(AppConstants.PUT_RETURN_SALES_PRODUCT_STOCK, list, authHeader);

			//

			return Response.status(201).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@POST
	@Path("new-sales-return")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEmptySalesReturn() {
		try {
			SalesReturn sr = new SalesReturn();
			sr.setCartId(0);
			sr.setMethod('R');
			sr.setReturnBy(0);
			sr.setShipmentFees(0);
			sr.setVatPercentage(0);
			dao.persist(sr);
			return Response.status(200).entity(sr.getId()).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	// idempotent, create sales and return ID
	@SecuredUser
	@POST
	@Path("new-sales")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEmptySales() {
		try {
			Sales s = new Sales();
			s.setCartId(0);
			s.setCourrierName("");
			s.setCreatedBy(0);
			s.setMakeId(0);
			s.setCustomerId(0);
			s.setDeliveryFees(0);
			s.setPaymentStatus('O');
			s.setPromotionDiscount(0);
			s.setShipmentFees(0);
			s.setShipmentReference("");
			s.setTransactionType('C');
			s.setVatPercentage(0);
			dao.persist(s);
			return Response.status(200).entity(s.getId()).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	// idempotent, create empty purchase and return ID
	@SecuredUser
	@POST
	@Path("new-purchase")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEmptyPurchase() {
		try {
			Purchase p = new Purchase();
			p.setPurchaseDate(new Date());
			p.setMakeId(0);
			p.setPaymentStatus('O');
			p.setTransactionType('T');
			p.setVendorId(0);
			dao.persist(p);
			return Response.status(200).entity(p.getId()).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("sales/cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesFromCart(@PathParam(value = "param") long cartId) {
		try {
			List<Sales> sales = dao.getCondition(Sales.class, "cartId", cartId);
			for (Sales s : sales) {
				List<SalesProduct> products = dao.getCondition(SalesProduct.class, "sales", s);
				s.setSalesProducts(products);
				List<SalesPayment> payments = dao.getCondition(SalesPayment.class, "sales", s);
				s.setSalesPayments(payments);
				List<SalesReturn> returns = dao.getCondition(SalesReturn.class, "sales", s);
				for (SalesReturn sr : returns) {
					List<SalesReturnProduct> prs = dao.getCondition(SalesReturnProduct.class, "salesReturn", sr);
					sr.setSalesReturnProducts(prs);
				}
				s.setSalesReturns(returns);
			}
			return Response.status(200).entity(sales).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}
	
	@SecuredUser
	@GET
	@Path("purchase/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPurchaseFromId(@PathParam(value = "param") long pId) {
		try {
			Purchase purchase = dao.find(Purchase.class, pId);
			if (purchase == null) {
				return Response.status(404).build();
			}

			List<PurchaseProduct> products = dao.getCondition(PurchaseProduct.class, "purchase", purchase);
			purchase.setPurchaseProducts(products);
			List<PurchasePayment> payments = dao.getCondition(PurchasePayment.class, "purchase", purchase);
			purchase.setPurchasePayments(payments);
			List<PurchaseReturn> returns = dao.getCondition(PurchaseReturn.class, "purchase", purchase);
			for (PurchaseReturn pr : returns) {
				List<PurchaseReturnProduct> prs = dao.getCondition(PurchaseReturnProduct.class, "purchaseReturn", pr);
				pr.setPurchaseReturnProducts(prs);
			}
			purchase.setPurchaseReturns(returns);
			return Response.status(200).entity(purchase).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("sales/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesFromId(@PathParam(value = "param") long salesId) {
		try {
			Sales sales = dao.find(Sales.class, salesId);
			if (sales == null) {
				return Response.status(404).build();
			}

			List<SalesProduct> products = dao.getCondition(SalesProduct.class, "sales", sales);
			sales.setSalesProducts(products);
			List<SalesPayment> payments = dao.getCondition(SalesPayment.class, "sales", sales);
			sales.setSalesPayments(payments);
			List<SalesReturn> returns = dao.getCondition(SalesReturn.class, "sales", sales);
			for (SalesReturn sr : returns) {
				List<SalesReturnProduct> prs = dao.getCondition(SalesReturnProduct.class, "salesReturn", sr);
				sr.setSalesReturnProducts(prs);
			}
			sales.setSalesReturns(returns);

			return Response.status(200).entity(sales).build();

		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("/sales-report/year/{param}/month/{param2}/method/{param3}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesReport(@PathParam(value = "param") int year, @PathParam(value = "param2") int month,
			@PathParam(value = "param3") String method) {
		try {
			Date from = Helper.getDateStartOfMonth(year, month);
			Date to = Helper.getDateEndOfMonth(year, month);

			List<Sales> sales = new ArrayList<>();
			String jpql = "select b from Sales b where b.salesDate between :value0 and :value1";

			if (method.equals("A")) {
				jpql = jpql + " order by b.salesDate asc";
				sales = dao.getJPQLParams(Sales.class, jpql, from, to);
			} else {
				jpql = jpql + " and b.id in (" + "select c.sales.id from SalesPayment c where c.method = :value2)";
				jpql = jpql + " order by b.salesDate asc";
				sales = dao.getJPQLParams(Sales.class, jpql, from, to, method.charAt(0));
			}

			for (Sales s : sales) {
				List<SalesProduct> sps = dao.getCondition(SalesProduct.class, "sales", s);
				s.setSalesProducts(sps);
				List<SalesPayment> spp = dao.getCondition(SalesPayment.class, "sales", s);
				s.setSalesPayments(spp);
			}
			return Response.status(200).entity(sales).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("/sales-return-report/year/{param}/month/{param2}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesReturnReport(@PathParam(value = "param") int year, @PathParam(value = "param2") int month) {
		try {
			Date from = Helper.getDateStartOfMonth(year, month);
			Date to = Helper.getDateEndOfMonth(year, month);

			String jpql = "select b from SalesReturn b where b.returnDate between :value0 and :value1 order by b.returnDate";
			List<SalesReturn> salesReturn = dao.getJPQLParams(SalesReturn.class, jpql, from, to);

			for (SalesReturn sr : salesReturn) {
				List<SalesReturnProduct> srps = dao.getCondition(SalesReturnProduct.class, "salesReturn", sr);
				sr.setSalesReturnProducts(srps);
			}
			return Response.status(200).entity(salesReturn).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	public <T> Response postSecuredRequest(String link, T t, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.post(Entity.entity(t, "application/json"));// not secured
		return r;
	}

	public <T> Response putSecuredRequest(String link, T t, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.put(Entity.entity(t, "application/json"));
		return r;
	}

	public Response getSecuredRequest(String link, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.get();
		return r;
	}

}
