package cn.m2c.scm.domain.model.order;

import cn.m2c.ddd.common.domain.model.ConcurrencySafeEntity;
import cn.m2c.ddd.common.domain.model.DomainEventPublisher;
import cn.m2c.scm.domain.model.order.log.event.OrderOptLogEvent;
/***
 * 售后订单实体
 * @author fanjc
 * created date 2017年10月20日
 * copyrighted@m2c
 */
public class SaleAfterOrder extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 1L;
	/**申请售后人*/
	private String userId;
	/**客户发送退换货单号信息*/
	private ExpressInfo backExpress;
	/**商家发送换货单号信息*/
	private ExpressInfo sendExpress;
	/**售后单号*/
	private String saleAfterNo;
	/**主订单号*/
	private String orderId;
	/**商家订单id*/
	private String dealerOrderId;
	/**商家 Id*/
	private String dealerId;
	/**商品 Id*/
	private String goodsId;
	/**SKU Id*/
	private String skuId;
	/**退货数*/
	private Integer backNum;
	/**退货原因*/
	private String reason;
	/**拒绝原因*/
	private String rejectReason;
	/**状态，0申请退货,1申请换货,2申请退款,3拒绝,4同意(退换货),5客户寄出,6商家收到,7商家寄出,8客户收到,9同意退款, 10确认退款*/
	private Integer status;
	/**售后单类型*/
	private Integer orderType;
	/**退款金额*/
	private Long backMoney;
	
	/**退货原因*/
	private Integer reasonCode;
	/**拒绝原因*/
	private Integer rejectReasonCode;
	
	public SaleAfterOrder() {
		super();
	}
	
	public SaleAfterOrder(String saleAfterNo, String userId, String orderId
			,String dealerOrderId, String dealerId, String goodsId, String skuId
			,String reason, int backNum, int status, int orderType, long backMoney
			, int reasonCode) {
		this.reason = reason;
		this.saleAfterNo = saleAfterNo;
		this.userId = userId;
		this.orderId = orderId;
		this.dealerOrderId = dealerOrderId;
		this.goodsId = goodsId;
		this.skuId = skuId;
		this.backNum = backNum;
		this.status = status;
		this.orderType = orderType;
		this.backMoney = backMoney;
		this.reasonCode = reasonCode;
		
		this.dealerId = dealerId;
	}
	/***
	 * 同意售后申请
	 */
	public void agreeApply(String userId) {
		if (status < 4 && status != 3)
			status = 4;
		else 
			return;
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(saleAfterNo, null, "同意售后申请！", userId));
	}
	/***
	 * 拒绝申请
	 */
	public boolean rejectSute(String r, int rCode, String userId) {
		
		if (status < 3)
			status = 3;
		else
			return false;
		rejectReason = r;
		rejectReasonCode = rCode;
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(saleAfterNo, null, "拒绝售后申请！", userId));
		return true;
	}
	/**
	 * 客户退货的发货
	 * @param e
	 * @return
	 */
	public boolean clientShip(ExpressInfo e, String userId) {
		if (status != 4)
			return false;
		backExpress = e;
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(saleAfterNo, null, "客户退货！", userId));
		return true;
	}
	
	/**
	 * 商家发货
	 * @param e
	 * @return
	 */
	public boolean dealerShip(ExpressInfo e, String userId) {
		if (status < 5)
			return false;
		sendExpress = e;
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(saleAfterNo, null, "商家换货发货！", userId));
		return true;
	}
	/***
	 * 商家确认收货
	 */
	public boolean dealerConfirmRev(String userId) {
		if (status < 5)
			return false;
		status = 6;
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(saleAfterNo, null, "商家确认收货", userId));
		return true;
	}
	
	/***
	 * 用户确认收货
	 */
	public boolean userConfirmRev(String userId) {
		if (status < 7)
			return false;
		status = 8;
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(saleAfterNo, null, "确认收货", userId));
		return true;
	}
	
	/***
	 * 同意退款
	 */
	public boolean agreeBackMoney(String userId) {
		if (status < 4)
			return false;
		status = 9;
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(saleAfterNo, null, "同意退款", userId));
		return true;
	}
	
	/***
	 * 确认退款
	 */
	public boolean confirmBackMoney(String userId) {
		if (status < 9)
			return false;
		status = 10;
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(saleAfterNo, null, "确认退款", userId));
		return true;
	}
	/***
	 * 创建售后申请
	 */
	public void createApply() {
		DomainEventPublisher.instance().publish(new OrderOptLogEvent(saleAfterNo, null, "创建售后申请成功", userId));
	}
	
	public boolean isSame(String sku) {
		return sku.equals(skuId);
	}
}