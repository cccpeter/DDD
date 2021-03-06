package cn.m2c.scm.application.order.command;

import org.apache.commons.lang3.StringUtils;

import cn.m2c.common.MCode;
import cn.m2c.ddd.common.AssertionConcern;
import cn.m2c.scm.domain.NegativeException;
/***
 * 支付请求命令
 * @author fanjc
 * created date 2017年10月19日
 * copyrighted@m2c
 */
public class ConfirmSkuCmd extends AssertionConcern {
	
	private String orderId;
	
	private String userId;
	
	private String dealerOrderId;
	
	private String skuId;
	
	private int sortNo;
	
	public ConfirmSkuCmd(String orderNo, String userId, String skuId
			, String dealerOrderId, int sortNo) throws NegativeException {
		
		orderId = orderNo;
		
		this.userId = userId;
		
		if (StringUtils.isEmpty(orderId)) {
			throw new NegativeException(MCode.V_1, "订单号参数为空(orderId)！");
		}
		
		if (StringUtils.isEmpty(userId)) {
			throw new NegativeException(MCode.V_1, "用户ID参数为空(userId)！");
		}
		
		if (StringUtils.isEmpty(skuId)) {
			throw new NegativeException(MCode.V_1, "确认商品的参数为空(skuId)！");
		}
		this.skuId = skuId;
		
		if (StringUtils.isEmpty(dealerOrderId)) {
			throw new NegativeException(MCode.V_1, "商家订单号参数为空(dealerOrderId)！");
		}
		this.dealerOrderId = dealerOrderId;
		this.sortNo = sortNo;
	}
	

	public int getSortNo() {
		return sortNo;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getUserId() {
		return userId;
	}

	public String getDealerOrderId() {
		return dealerOrderId;
	}

	public String getSkuId() {
		return skuId;
	}	
	
}
