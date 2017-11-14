package cn.m2c.scm.application.order.data.bean;

import java.util.Date;

import cn.m2c.ddd.common.persistence.orm.ColumnAlias;

public class AfterSellBean {

	/**
	 * 售后单号
	 */
	@ColumnAlias(value = "after_sell_order_id")
	private String afterSellOrderId;

	/** 订货号 **/
	@ColumnAlias(value = "order_id")
	private String orderId;

	/**
	 * 期望售后
	 */
	@ColumnAlias(value = "order_type")
	private Integer orderType;
	/**
	 * 售后总额
	 */
	@ColumnAlias(value = "back_money")
	private Integer backMoney;
	/**
	 * 订单状态
	 */
	@ColumnAlias(value = "_status")
	private Integer status;
	/**
	 * 商家信息
	 */
	@ColumnAlias(value = "dealer_name")
	private String dealerName;
	/** 规格ID */
	@ColumnAlias(value = "sku_id")
	private String skuId;
	/**
	 * 申请时间
	 */
	@ColumnAlias(value = "created_date")
	private Date createDate;
	
	@ColumnAlias(value = "dealer_order_id")
	private String dealerOrderId;
	
	@ColumnAlias(value = "dealer_id")
	private String dealerId;
	
	@ColumnAlias(value = "goods_id")
	private String goodsId;
	@ColumnAlias(value = "sell_num")
	private Integer sellNum;
	
	@ColumnAlias(value = "goods_name")
	private String goodsName;
	@ColumnAlias(value = "sku_name")
	private String skuName;
	
	@ColumnAlias(value = "goods_type_id")
	private String goodsTypeId;
	
	@ColumnAlias(value = "goods_type")
	private String goodsType;
	
	@ColumnAlias(value = "discount_price")
	private Long discountPrice;
	
	@ColumnAlias(value= "goods_icon")
	private String goodsIcon;
	
	@ColumnAlias(value= "last_updated_date")
	private Date updateTime;
	
	@ColumnAlias(value= "reject_reason")
	private String rejectReason;
	
	@ColumnAlias(value= "reason")
	private String reason;
	
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getGoodsIcon() {
		return goodsIcon;
	}

	public void setGoodsIcon(String goodsIcon) {
		this.goodsIcon = goodsIcon;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getDealerOrderId() {
		return dealerOrderId;
	}

	public void setDealerOrderId(String dealerOrderId) {
		this.dealerOrderId = dealerOrderId;
	}

	public String getDealerId() {
		return dealerId;
	}

	public void setDealerId(String dealerId) {
		this.dealerId = dealerId;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getSellNum() {
		return sellNum;
	}

	public void setSellNum(Integer sellNum) {
		this.sellNum = sellNum;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public String getGoodsTypeId() {
		return goodsTypeId;
	}

	public void setGoodsTypeId(String goodsTypeId) {
		this.goodsTypeId = goodsTypeId;
	}

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public Long getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(Long discountPrice) {
		this.discountPrice = discountPrice;
	}

	public String getAfterSellOrderId() {
		return afterSellOrderId;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public Integer getBackMoney() {
		return backMoney;
	}

	public Integer getStatus() {
		return status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setAfterSellOrderId(String afterSellOrderId) {
		this.afterSellOrderId = afterSellOrderId;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public void setBackMoney(Integer backMoney) {
		this.backMoney = backMoney;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	@Override
	public String toString() {
		return "AfterSellOrderBean [afterSellOrderId=" + afterSellOrderId + ", orderId=" + orderId + ", orderType="
				+ orderType + ", backMoney=" + backMoney + ", status=" + status 
				+ ", skuId=" + skuId + ", createDate=" + createDate + "]";
	}

}
