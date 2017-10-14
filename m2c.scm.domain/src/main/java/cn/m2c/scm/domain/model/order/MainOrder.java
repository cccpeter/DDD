package cn.m2c.scm.domain.model.order;

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
	private Integer status;
	
	private String province;
	
	private String provinceCode;
	
	private String city;
	
	private String cityCode;
	
	private String area;
	
	private String areaCode;
	
	private String street;
	/**收货联系人*/
	private String revPerson;
	/**联系电话*/
	private String revPhone;
	/**以分为单位，商品金额*/
	private Integer goodsAmount;
	/**订单总运费*/
	private Integer orderFreight;
	/**平台优惠*/
	private Integer plateformDiscount;
	/**商家优惠*/
	private Integer dealerDiscount;
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
	/**
	 * 增加订单
	 */
	public void add() {
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(orderId, null, "订单创建", userId));
	}
}
