package qetaa.service.invoice.restful;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import qetaa.service.invoice.model.purchase.PurchaseProductItem;
import qetaa.service.invoice.model.purchase.PurchaseReturn;
import qetaa.service.invoice.model.purchase.PurchaseReturnProduct;
import qetaa.service.invoice.model.sales.Sales;
import qetaa.service.invoice.model.sales.SalesPayment;
import qetaa.service.invoice.model.sales.SalesProduct;
import qetaa.service.invoice.model.sales.SalesProductItem;
import qetaa.service.invoice.model.sales.SalesReturn;
import qetaa.service.invoice.model.sales.SalesReturnProduct;
import qetaa.service.invoice.model.sales.contract.SalesHolder;
import qetaa.service.invoice.model.sales.contract.SalesReturnHolder;
import qetaa.service.invoice.model.sales.contract.SalesReturnWallet;;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InvoiceService {
	@EJB
	private DAO dao;

	@Secured
	@GET
	@SecuredCustomer
	@ValidApp
	public void test() {

	}
	
	@SecuredUser
	@GET
	@Path("purchase-products/product/{product-id}")
	public Response getProductPurchases(@PathParam(value="product-id") long productId) {
		try {
			List<PurchaseProduct> pps = dao.getCondition(PurchaseProduct.class, "productId", productId);
			for(PurchaseProduct pp : pps) {
				SalesProduct sp = dao.findCondition(SalesProduct.class, "purchaseProduct.id", pp.getId());
				pp.setSalesProduct(sp);
			}
			System.out.println(pps.size());
		return Response.status(200).entity(pps).build();
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("sales-payment")
	public Response createSalesReceivable(SalesPayment sp) {
		try {
			String checkjpql = "select b from SalesPayment b where b.sales = :value0 and b.amount = :value1 and b.paymentDate = :value2";
			List<SalesPayment> check = dao.getJPQLParams(SalesPayment.class, checkjpql, sp.getSales(), sp.getAmount(),
					sp.getPaymentDate());
			if (check.isEmpty()) {
				dao.persist(sp);
				Set<Sales> sales = new HashSet<Sales>();
				sales.add(sp.getSales());
				updateSalesAfterPayment(sales);
			}
			return Response.status(201).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("replace-purchase-product")
	public Response replacePurchaseProduct(@HeaderParam("Authorization") String authHeader, List<PurchaseProduct> pps) {
		try {
			for (PurchaseProduct pp : pps) {
				dao.update(pp);
				long productId = pp.getProductId();
				SalesProduct sp = pp.getSalesProduct();
				sp.setProductId(productId);
				dao.update(sp);
				// update wallet product id
				long walletItemId = pp.getWalletItemId();
				Map<String, Number> map = new HashMap<String, Number>();
				map.put("walletItemId", walletItemId);
				map.put("productId", productId);
				Response r = this.putSecuredRequest(AppConstants.PUT_REPLACE_WALLET_ITEM_PRODUCT_ID, map, authHeader);
				if (r.getStatus() == 201) {
					
				} else
					throw new Exception();
			}
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@POST
	@Path("purchase-payment")
	public Response createPurchasePayable(PurchasePayment pp) {
		try {
			String checkjpql = "select b from PurchasePayment b where b.purchase = :value0 and b.amount = :value1 and b.paymentDate = :value2";
			List<PurchasePayment> check = dao.getJPQLParams(PurchasePayment.class, checkjpql, pp.getPurchase(),
					pp.getAmount(), pp.getPaymentDate());
			if (check.isEmpty()) {
				dao.persist(pp);
			}
			Set<Purchase> purchases = new HashSet<Purchase>();
			purchases.add(pp.getPurchase());
			updatePurchaseAfterPayment(purchases);
			return Response.status(201).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("purchase-payments")
	public Response createPurchasePayables(List<PurchasePayment> pps) {
		try {
			Set<Purchase> purchases = new HashSet<Purchase>();
			for (PurchasePayment pp : pps) {
				purchases.add(pp.getPurchase());
				String checkjpql = "select b from PurchasePayment b where b.purchase = :value0 and b.amount = :value1 and b.paymentDate = :value2";
				List<PurchasePayment> check = dao.getJPQLParams(PurchasePayment.class, checkjpql, pp.getPurchase(),
						pp.getAmount(), pp.getPaymentDate());
				if (check.isEmpty()) {
					dao.persist(pp);
				}
			}
			updatePurchaseAfterPayment(purchases);

			return Response.status(201).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	/*
	 * Each purchase must have the products, payments, returns, retun payments set
	 */
	private void updatePurchaseAfterPayment(Set<Purchase> purchases) throws Exception {
		for (Purchase purchase : purchases) {
			String jpql = "select sum(b.amount) from PurchasePayment b where b.purchase = :value0";
			Double paid = dao.findJPQLParams(Double.class, jpql, purchase);
			jpql = "select sum(b.unitCostWv * b.quantity) from PurchaseProduct b where b.purchase = :value0 ";
			Double itemCost = dao.findJPQLParams(Double.class, jpql, purchase);
			if (itemCost == null) {
				itemCost = 0D;
			}
			jpql = "select sum(b.amount) from PurchasePayment b where b.purchaseReturn.purchase = :value0";
			Double returned = dao.findJPQLParams(Double.class, jpql, purchase);
			if (returned == null) {
				returned = 0D;
			}
			if ((paid - (itemCost + returned)) < 0.2 && (paid - (itemCost + returned) > -0.2)) {
				purchase.setPaymentStatus('P');
				dao.update(purchase);
			}
		}
	}

	private void updateSalesAfterPayment(Set<Sales> sales) throws Exception {
		for (Sales sale : sales) {
			String jpql = "select sum(b.amount) from SalesPayment b where b.sales = :value0";
			Double paid = dao.findJPQLParams(Double.class, jpql, sale);

			// get delivery fees
			jpql = "select b.deliveryFees from Sales b where b.id = :value0 ";
			Double delFees = dao.findJPQLParams(Double.class, jpql, sale.getId());
			if (delFees == null) {
				delFees = 0D;
			}

			// get items sales with vat
			jpql = "select sum(b.unitSalesWv * b.quantity) from SalesProduct b where b.sales = :value0 ";
			Double itemSales = dao.findJPQLParams(Double.class, jpql, sale);
			if (itemSales == null) {
				itemSales = 0D;
			}
			// get discount promotions
			jpql = "select b.promotionDiscount from Sales b where b.id = :value0 ";
			Double promDiscount = dao.findJPQLParams(Double.class, jpql, sale.getId());
			if (promDiscount == null) {
				promDiscount = 0D;
			}

			Double netPay = itemSales + delFees - promDiscount;
			// get total returned fees
			jpql = "select sum(b.amount) from SalesPayment b where b.salesReturn.sales = :value0";
			Double returned = dao.findJPQLParams(Double.class, jpql, sale);
			if (returned == null) {
				returned = 0D;
			}

			if ((paid - (netPay + returned)) < 0.2 && (paid - (netPay + returned) > -0.2)) {
				sale.setPaymentStatus('P');
				dao.update(sale);
			}
		}
	}

	@SecuredUser
	@GET
	@Path("receivables")
	public Response getSalesPayables() {
		try {
			List<Sales> salesOrders = dao.getConditionOrdered(Sales.class, "paymentStatus", 'O', "salesDate", "asc");
			for (Sales sale : salesOrders) {
				putSalesProducts(sale);
				putSalesPayments(sale);
				putSalesReturns(sale);
			}
			return Response.status(200).entity(salesOrders).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("payables")
	public Response getPurchasePayables() {
		try {
			List<Purchase> purchases = dao.getConditionOrdered(Purchase.class, "paymentStatus", 'O', "purchaseDate",
					"asc");
			for (Purchase purchase : purchases) {
				putPurchaseProducts(purchase);
				putPurchaseReturns(purchase);
				putPurchasePayments(purchase);
			}
			return Response.status(200).entity(purchases).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("payables/vendor/{param}")
	public Response getVendorPurchasePayables(@PathParam(value = "param") int vendorId) {
		try {
			List<Purchase> purchases = dao.getTwoConditionsOrdered(Purchase.class, "paymentStatus", "vendorId", 'O',
					vendorId, "purchaseDate", "asc");
			for (Purchase purchase : purchases) {
				putPurchaseProducts(purchase);
				putPurchaseReturns(purchase);
				putPurchasePayments(purchase);
			}
			return Response.status(200).entity(purchases).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	private void putSalesReturns(Sales sales) {
		List<SalesReturn> returns = dao.getCondition(SalesReturn.class, "sales", sales);
		for (SalesReturn sr : returns) {
			List<SalesReturnProduct> prs = dao.getCondition(SalesReturnProduct.class, "salesReturn", sr);
			sr.setSalesReturnProducts(prs);
		}
		sales.setSalesReturns(returns);
	}

	private void putPurchaseReturns(Purchase purchase) {
		List<PurchaseReturn> returns = dao.getCondition(PurchaseReturn.class, "purchase", purchase);
		for (PurchaseReturn pr : returns) {
			List<PurchaseReturnProduct> prs = dao.getCondition(PurchaseReturnProduct.class, "purchaseReturn", pr);
			List<PurchasePayment> pays = dao.getCondition(PurchasePayment.class, "purchaseReturn", pr);
			pr.setPurchasePayments(pays);
			pr.setPurchaseReturnProducts(prs);
		}
		purchase.setPurchaseReturns(returns);
	}

	private void putPurchaseProducts(Purchase purchase) {
		List<PurchaseProduct> items = dao.getCondition(PurchaseProduct.class, "purchase", purchase);
		purchase.setPurchaseProducts(items);
	}

	private void putSalesProducts(Sales sales) {
		List<SalesProduct> items = dao.getCondition(SalesProduct.class, "sales", sales);
		sales.setSalesProducts(items);
	}

	private void putSalesReturnProducts(SalesReturn salesReturn) {
		List<SalesReturnProduct> items = dao.getCondition(SalesReturnProduct.class, "salesReturn", salesReturn);
		salesReturn.setSalesReturnProducts(items);
	}

	private void putPurchasePayments(Purchase purchase) {
		List<PurchasePayment> payments = dao.getCondition(PurchasePayment.class, "purchase", purchase);
		purchase.setPurchasePayments(payments);
	}

	private void putSalesPayments(Sales sales) {
		List<SalesPayment> payments = dao.getCondition(SalesPayment.class, "sales", sales);
		sales.setSalesPayments(payments);
	}

	private void putSalesReturnPayments(SalesReturn salesReturn) {
		List<SalesPayment> payments = dao.getCondition(SalesPayment.class, "salesReturn", salesReturn);
		salesReturn.setSalesPayments(payments);
	}

	@SecuredUser
	@GET
	@Path("incomplete-purchases")
	public Response getIncompletePurchases() {
		try {
			String jpql = "select * from inv_purchase b where b.payment_status = 'I' or (b.payment_status = 'O'"
					+ " and b.completed\\:\\:date + '1 day'\\:\\:interval > now()) order by b.cart_id,b.vendor_id";
			// List<Purchase> purchases = dao.getJPQLParams(Purchase.class, jpql, 'I', 'O');
			List<Purchase> purchases = dao.getNative(Purchase.class, jpql);
			return Response.status(200).entity(purchases).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("complete-purchase-costs")
	public Response completePurchaseCosts(Purchase purchase) {
		try {
			for (PurchaseProduct pp : purchase.getPurchaseProducts()) {
				pp.setPurchase(purchase);
				dao.update(pp);
			}
			purchase.setCompleted(new Date());
			purchase.setPaymentStatus('O');
			dao.update(purchase);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("incomplete-purchase/{param}")
	public Response getIncompletePurchase(@PathParam(value = "param") long pid) {
		try {
			String sql = "select * from inv_purchase b where b.id = " + pid + " and b.payment_status = 'I' or ("
					+ "b.id = " + pid
					+ " and b.payment_status = 'O' and b.completed\\:\\:date + '1 day'\\:\\:interval > now())";
			List<Purchase> list = dao.getNative(Purchase.class, sql);
			if (list.isEmpty()) {
				return Response.status(404).build();
			}
			Purchase purchase = list.get(0);
			// purchase products
			List<PurchaseProduct> pps = dao.getCondition(PurchaseProduct.class, "purchase.id", purchase.getId());
			for (PurchaseProduct pp : pps) {
				SalesProduct sp = dao.findCondition(SalesProduct.class, "purchaseProduct", pp);
				pp.setSalesProduct(sp);
			}
			purchase.setPurchaseProducts(pps);
			// payments
			List<PurchasePayment> payments = dao.getCondition(PurchasePayment.class, "purchase", purchase);
			purchase.setPurchasePayments(payments);
			// returns
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
	@POST
	@Path("search-purchases")
	public Response searchPurchases(Map<String, Object> map) {
		try {
			List<Number> productIds = (List<Number>) map.get("productIds");
			List<Number> customerIds = (List<Number>) map.get("customerIds");
			Number fromTime = (Number) map.get("from");
			Number toTime = (Number) map.get("to");
			Number makeId = (Number) map.get("makeId");
			Number cartId = (Number) map.get("cartId");
			Number vendorId = (Number) map.get("vendorId");

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

			if (makeId != null && makeId.intValue() != 0) {
				sql = sql + " and make_id = " + makeId.intValue();
			}

			if (vendorId != null && vendorId.intValue() != 0) {
				sql = sql + " and vendor_id = " + vendorId.intValue();
			}

			if (fromTime != null && toTime == null) {
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
			for (Purchase s : purchases) {
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

			else if (promoCode != null) {
				sql = sql + " and promotion_id = '" + promoCode + "' ";
			}

			if (makeId != null && makeId.intValue() != 0) {
				sql = sql + " and make_id = " + makeId.intValue();
			}

			if (fromTime != null && toTime == null) {
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
			for (Sales s : sales) {
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

	private void createSalesProducts(Sales sales, String authHeader) {

		List<SalesProductItem> spis = new ArrayList<>();
		for (SalesProduct sp : sales.getSalesProducts()) {
			// check if created before
			String jpql = "select b from SalesProduct b where b.sales.id = :value0 and b.productId = :value1 and b.purchaseProduct.id = :value2";
			List<SalesProduct> check = dao.getJPQLParams(SalesProduct.class, jpql, sales.getId(), sp.getProductId(),
					sp.getPurchaseProduct().getId());
			if (check.isEmpty()) {
				sp.setSales(sales);
				dao.persist(sp);
				SalesProductItem spi = new SalesProductItem();
				spi.setPurchaseProductId(sp.getPurchaseProduct().getId());
				spi.setSalesProductId(sp.getId());
				spi.setSalesProductQuantity(sp.getQuantity());
				spi.setWalletItemId(sp.getPurchaseProduct().getWalletItemId());
				spis.add(spi);
			}
		}
		Response r = putSecuredRequest(AppConstants.PUT_SALES_WALLET_ITEM, spis, authHeader);
	}

	private void createPayment(Sales sales) {
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
	}

	private void createSalesStock(Sales sales, String authHeader) {
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
	}

	@SecuredUser
	@PUT
	@Path("sales")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateSales(@HeaderParam("Authorization") String authHeader, Sales sales) {
		try {
			sales.setSalesDate(new Date());
			dao.update(sales);
			createSalesProducts(sales, authHeader);
			createPayment(sales);
			createSalesStock(sales, authHeader);
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
		purchase.setCreated(new Date());
		dao.update(purchase);
		createPurchaseProducts(purchase, authHeader);
		createPayment(purchase);
		createPurchaseStock(purchase, authHeader);
		return Response.status(201).build();
	}

	private void createPurchaseProducts(Purchase purchase, String authHeader) {
		List<PurchaseProductItem> ppis = new ArrayList<>();
		for (PurchaseProduct pp : purchase.getPurchaseProducts()) {
			// check if created before
			String jpql = "select b from PurchaseProduct b where b.purchase.id = :value0 and b.productId = :value1 and b.walletItemId = :value2";
			List<PurchaseProduct> check = dao.getJPQLParams(PurchaseProduct.class, jpql, purchase.getId(),
					pp.getProductId(), pp.getWalletItemId());
			if (check.isEmpty()) {
				pp.setPurchase(purchase);
				dao.persist(pp);
				PurchaseProductItem ppi = new PurchaseProductItem();
				ppi.setPurchaseProductId(pp.getId());
				ppi.setWalletItemId(pp.getWalletItemId());
				ppi.setPurchaseProductQuantity(pp.getQuantity());
				ppi.setVendorId(purchase.getVendorId());
				ppis.add(ppi);
			}
		}
		Response r = putSecuredRequest(AppConstants.PUT_PURCHASED_WALLET_ITEM, ppis, authHeader);
	}

	private void createPayment(Purchase purchase) {
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
	}

	private void createPurchaseStock(Purchase purchase, String authHeader) {
		List<Map<String, Long>> list = new ArrayList<>();
		for (PurchaseProduct pp : purchase.getPurchaseProducts()) {
			Map<String, Long> map = new HashMap<String, Long>();
			map.put("cartId", purchase.getCartId());
			map.put("purchaseId", purchase.getId());
			map.put("quantity", Integer.valueOf(pp.getQuantity()).longValue());
			map.put("productId", pp.getProductId());
			list.add(map);
		}
		postSecuredRequest(AppConstants.POST_PRODUCT_STOCK, list, authHeader);
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
				this.putPurchaseProducts(p);
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
			createPurchaseReturnPayment(purchaseReturn);
			// remove stock
			removeStockPurchaseReturn(purchaseReturn, authHeader);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	private void createPurchaseReturnPayment(PurchaseReturn purchaseReturn) {
		PurchasePayment payment = purchaseReturn.getPurchasePayments().get(0);
		payment.setPaymentDate(new Date());
		payment.setPurchaseReturn(purchaseReturn);
		dao.persist(payment);
		// update purchase
	}

	private void createSalesReturnPayment(SalesReturn salesReturn) {
		SalesPayment payment = salesReturn.getSalesPayments().get(0);
		payment.setPaymentDate(new Date());
		payment.setSalesReturn(salesReturn);
		dao.persist(payment);
	}

	private void removeStockPurchaseReturn(PurchaseReturn purchaseReturn, String authHeader) {
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
	}

	private void addStockSalesReturn(SalesReturn salesReturn, String authHeader) {
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
		this.putSecuredRequest(AppConstants.PUT_RETURN_SALES_PRODUCT_STOCK, list, authHeader);
	}

	@SecuredUser
	@PUT
	@Path("sales-return")
	public Response updateSalesReturn(@HeaderParam("Authorization") String authHeader, SalesReturnWallet srw) {
		try {
			SalesReturn salesReturn = srw.getSalesReturn();
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

			this.createSalesReturnPayment(salesReturn);
			this.addStockSalesReturn(salesReturn, authHeader);
			this.createWalletSalesReturn(srw, authHeader);
			// create wallet sales return
			return Response.status(201).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	private void createWalletSalesReturn(SalesReturnWallet srw, String authHeader) {
		Response r = this.postSecuredRequest(AppConstants.POST_NEW_WALLET_SALES_RETURN,
				srw.getSalesReturn().getCartId(), authHeader);
		Long walletId = r.readEntity(Long.class);
		Map<String, Object> wallet = new HashMap<String, Object>();
		wallet.put("id", walletId);
		wallet.put("customerId", srw.getCustomerId());
		wallet.put("customerName", srw.getCustomerName());
		wallet.put("cartId", srw.getSalesReturn().getCartId());
		wallet.put("bankId", srw.getBankId());
		wallet.put("bankConfirmedBy", srw.getSalesReturn().getReturnBy());
		wallet.put("discountPercentage", srw.getDiscountPercentage());
		wallet.put("walletItems", initWalletItems(srw, walletId));
		Response r2 = this.putSecuredRequest(AppConstants.PUT_SALES_RETURN_WALLET, wallet, authHeader);
		if (r2.getStatus() == 201) {
			
		} else {
			System.out.println("wallet not updated " + r2.getStatus());
		}

	}

	private List<Map<String, Object>> initWalletItems(SalesReturnWallet srw, Long walletId) {
		List<Map<String, Object>> maps = new ArrayList<>();
		double returnFees = 0;
		for (SalesReturnProduct srp : srw.getSalesReturn().getSalesReturnProducts()) {
			Map<String, Object> map = new HashMap<String, Object>();
			returnFees += srp.getReturnDeductionFees();
			map.put("walletId", walletId);
			map.put("productId", srp.getProductId());
			map.put("itemType", 'P');
			map.put("itemNumber", null);
			map.put("itemDesc", null);
			map.put("quantity", srp.getQuantity());
			map.put("cartId", srw.getSalesReturn().getCartId());
			map.put("unitSales", srp.getUnitSales());
			double discount = srp.getUnitSales() * srw.getDiscountPercentage();
			map.put("unitSalesWv", srp.getUnitSalesWv());
			map.put("unitSalesNet", srp.getUnitSales() - discount);
			map.put("unitSalesNetWv", srp.getUnitSalesWv() - discount);
			map.put("unitQuotedCost", srp.getUnitCost());
			map.put("unitQuotedCostWv", srp.getUnitCostWv());
			map.put("status", 'R');
			map.put("refundedItemId", null);
			map.put("refundNote", null);
			map.put("purchasedItemId", srp.getPurchaseProduct().getId());
			SalesProduct sp = dao.findCondition(SalesProduct.class, "purchaseProduct", srp.getPurchaseProduct());
			map.put("soldItemId", sp.getId());
			maps.add(map);

		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("walletId", walletId);
		map.put("productId", null);
		map.put("itemType", 'D');
		map.put("itemNumber", null);
		map.put("itemDesc", "Delivery - رسوم التوصيل");
		map.put("quantity", 0);
		map.put("cartId", srw.getSalesReturn().getCartId());
		map.put("unitSales", srw.getSalesReturn().getReturnedDeliveryFees());
		double vat = srw.getSalesReturn().getReturnedDeliveryFees() * srw.getSalesReturn().getVatPercentage();
		double discount = srw.getSalesReturn().getReturnedDeliveryFees() * srw.getDiscountPercentage();
		map.put("unitSalesWv", srw.getSalesReturn().getReturnedDeliveryFees() + vat);
		map.put("unitSalesNet", srw.getSalesReturn().getReturnedDeliveryFees() - discount);
		map.put("unitSalesNetWv", srw.getSalesReturn().getReturnedDeliveryFees() - discount + vat);
		map.put("unitQuotedCost", 0D);
		map.put("unitQuotedCostWv", 0D);
		map.put("status", 'R');
		map.put("purchasedItemId", null);
		map.put("soldItemId", null);
		maps.add(map);

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("walletId", walletId);
		map2.put("cartId", srw.getSalesReturn().getCartId());
		map2.put("itemDesc", "Return Fees - رسوم الترجيع");
		map2.put("itemNumber", "");
		map2.put("itemType", 'F');
		map2.put("productId", null);
		map2.put("quantity", 0);
		map2.put("status", 'R');
		map2.put("unitQuotedCost", 0D);
		map2.put("unitQuotedCostWv", 0D);
		map2.put("unitSales", returnFees);
		map2.put("unitSalesWv", returnFees);
		map2.put("unitSalesNet", returnFees);
		map2.put("unitSalesNetWv", returnFees);
		map2.put("purchasedItemId", null);
		map2.put("soldItemId", null);
		maps.add(map2);
		return maps;
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
			p.setPaymentStatus('I');
			p.setTransactionType('T');
			p.setVendorId(0);
			dao.persist(p);
			return Response.status(200).entity(p.getId()).build();
		} catch (Exception ex) {
			ex.printStackTrace();
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
				putSalesProducts(s);
				putSalesPayments(s);
				putSalesReturns(s);
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

			this.putPurchaseProducts(purchase);
			this.putPurchasePayments(purchase);
			this.putPurchaseReturns(purchase);
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

			this.putSalesProducts(sales);
			this.putSalesPayments(sales);
			this.putSalesReturns(sales);
			return Response.status(200).entity(sales).build();

		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("sales-report")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postSalesReport3(Map<String, Object> map) {
		try {
			Integer year = ((Number) map.get("year")).intValue();
			Integer month = ((Number) map.get("month")).intValue();
			Date from = Helper.getDateStartOfMonth(year, month);
			Date to = Helper.getDateEndOfMonth(year, month);
			String transaction = (String) map.get("transaction");
			String paymentStatus = (String) map.get("paymentStatus");
			Integer makeId = ((Number) map.get("makeId")).intValue();
			Helper h = new Helper();
			String sql = "select * from inv_sales b where b.sales_date between '" + h.getDateFormat(from) + "' and '"
					+ h.getDateFormat(to) + "'";
			if (!transaction.equals("A")) {
				sql += " and transaction_type = '" + transaction.charAt(0) + "'";
			}
			if (!paymentStatus.equals("A")) {
				sql += " and payment_status = '" + paymentStatus.charAt(0) + "'";
			}
			if (makeId != 0) {
				sql += " and make_id = " + makeId;
			}
			sql += "order by b.sales_date";
			List<Sales> salesWithCompletePurchase = dao.getNative(Sales.class, sql);
			List<SalesHolder> holders = new ArrayList<SalesHolder>();
			for (Sales s : salesWithCompletePurchase) {
				this.putSalesProducts(s);
				this.putSalesPayments(s);
				SalesHolder sh = new SalesHolder();
				sh.setSales(s);

				// init purchases
				List<Purchase> purchases = dao.getCondition(Purchase.class, "cartId", s.getCartId());
				boolean allCompleted = true;
				for (Purchase purchase : purchases) {
					if (purchase.getPaymentStatus() == 'I') {
						allCompleted = false;
					}
					this.putPurchasePayments(purchase);
					this.putPurchaseProducts(purchase);
					this.putPurchaseReturns(purchase);
				}
				sh.setPurchasesComplete(allCompleted);
				sh.setPurchases(purchases);
				holders.add(sh);
			}

			return Response.status(200).entity(holders).build();
		} catch (Exception ex) {
			ex.printStackTrace();
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
				this.putSalesProducts(s);
				this.putSalesPayments(s);
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

	@SecuredUser
	@POST
	@Path("sales-return-report")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postSalesReturnReport3(Map<String, Object> map) {
		try {
			Integer year = ((Number) map.get("year")).intValue();
			Integer month = ((Number) map.get("month")).intValue();
			Date from = Helper.getDateStartOfMonth(year, month);
			Date to = Helper.getDateEndOfMonth(year, month);
			Integer makeId = ((Number) map.get("makeId")).intValue();
			Helper h = new Helper();

			String sql = "select * from inv_sales_return b where b.return_date between '" + h.getDateFormat(from)
					+ "' and '" + h.getDateFormat(to) + "'";
			if (makeId != 0) {
				sql += " and b.sales_id in (select c.id from inv_sales c where c.id = b.sales_id and c.make_id = "
						+ makeId + ")";
			}

			List<SalesReturn> salesReturnWithCompletePurchase = dao.getNative(SalesReturn.class, sql);
			List<SalesReturnHolder> holders = new ArrayList<SalesReturnHolder>();
			for (SalesReturn s : salesReturnWithCompletePurchase) {
				this.putSalesReturnProducts(s);
				this.putSalesReturnPayments(s);
				SalesReturnHolder sh = new SalesReturnHolder();
				sh.setSalesReturn(s);

				// init purchases
				List<Purchase> purchases = dao.getCondition(Purchase.class, "cartId", s.getCartId());
				boolean allCompleted = true;
				for (Purchase purchase : purchases) {
					if (purchase.getPaymentStatus() == 'I') {
						allCompleted = false;
					}
					this.putPurchasePayments(purchase);
					this.putPurchaseProducts(purchase);
					this.putPurchaseReturns(purchase);
				}
				sh.setPurchases(purchases);
				sh.setPurchasesComplete(allCompleted);
				holders.add(sh);
			}

			return Response.status(200).entity(holders).build();
		} catch (Exception ex) {
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
