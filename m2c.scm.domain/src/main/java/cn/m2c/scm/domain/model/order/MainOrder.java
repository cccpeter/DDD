package cn.m2c.scm.domain.model.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.m2c.ddd.common.domain.model.ConcurrencySafeEntity;
import cn.m2c.ddd.common.domain.model.DomainEventPublisher;
import cn.m2c.scm.domain.model.order.log.event.OrderOptLogEvent;
/***
 * 主订单实体
 * @author fanjc
 *
 */
public class MainOrder extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 1L;
	
	private String orderId;
	
	private String payNo;
	/**1支付宝，2微信，3...*/
	private Integer payWay;
	
	private Date payTime;
	
	/**订单状态 0待付款，1等发货，2待收货，3完成，4交易完成，5交易关闭，-1已取消*/
	private Integer status = 0;
	/**收货人*/
	ReceiveAddr addr;
	/**以分为单位，商品金额*/
	private Long goodsAmount;
	/**订单总运费*/
	private Long orderFreight;
	/**平台优惠*/
	private Long plateformDiscount;
	/**商家优惠*/
	private Long dealerDiscount;
	/**下单用户ID*/
	private String userId;
	/**备注 留言*/
	private String noted;
	/**商家订单*/
	private List<DealerOrder> dealerOrders;
	/**应用的优惠券*/
	private List<SimpleCoupon> coupons;
	/**应用的营销策略*/
	private List<SimpleMarketing> marketings;
	
	public MainOrder() {
		super();
	}
	// String payNo, int payWay, Date payTime, 
	public MainOrder(String orderId, ReceiveAddr addr, long goodsAmount, long orderFreight, long plateformDiscount
			, long dealerDiscount, String userId, String noted, List<DealerOrder> dealerOrders
			,List<SimpleCoupon> coupons, List<SimpleMarketing> marketings) {
		this.orderId = orderId;
		this.addr = addr;
		this.goodsAmount = goodsAmount;
		this.orderFreight = orderFreight;
		this.plateformDiscount = plateformDiscount;
		this.dealerDiscount = dealerDiscount;
		this.userId = userId;
		this.noted = noted;
		this.dealerOrders = dealerOrders;
		this.coupons = coupons;
		this.marketings = marketings;
	}
	/**
	 * 增加订单
	 */
	public void add() {
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(orderId, null, "订单提交成功", userId));
	}
	/***
	 * 取消订单(用户主动操作，系统自动操作)
	 */
	public boolean cancel() {
		// 检查是否可以取消，只有在未支付的状态下用户可以取消
		if (status != 0) {
			return false;
		}
		status = -1;
		
		for (DealerOrder d : dealerOrders) {
			d.cancel();
		}
		
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(orderId, null, "订单取消成功", userId));
		return true;
	}
	/***
	 * 结算(可暂不做)
	 */
	public void checkout() {
		// 判断是否可以结算
		if (status >=4)
			return;
	}
	/***
	 * 支付成功 // 需要参数
	 */
	public void paySuccess(String payNo, int payWay, Date payTime, String uId) {
		//payNo, payWay, Date payTime
		this.payNo = payNo;
		this.payWay = payWay;
		this.payTime = payTime;
		status = 1;
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(orderId, null, "订单支付成功", uId));
	}
	/***
	 * 获取订单编号
	 * @return
	 */
	public String getOrderId() {
		return orderId;
	}
	
	public int getStatus() {
		if (status != null)
			return status.intValue();
		return 0;
	}
	/***
	 * 获取应付金额
	 * @return
	 */
	public long getActual() {
		return (goodsAmount + orderFreight - plateformDiscount - dealerDiscount);
	}
	/**
	 * 获取营销规则
	 * @return
	 */
	public List<String> getMkIds() {
		List<String> rs = null;
		if (marketings == null || marketings.size()<1)
			return rs;
		rs = new ArrayList<String>();
		for (SimpleMarketing mk : marketings) {
			String mId = mk.getMarketingId();
			if (mId != null)
				rs.add(mId);
		}
		return rs;
	}
}
