package cn.m2c.scm.application.goods.command;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import cn.m2c.common.MCode;
import cn.m2c.ddd.common.AssertionConcern;
import cn.m2c.scm.domain.NegativeException;

/**
 * 新增商品保障
 * */
public class GoodsGuaranteeAddCommand extends AssertionConcern implements Serializable {
	
	private String guaranteeId;    //商品保障id
	private String guaranteeName;  //商品保障名
	private String guaranteeDesc;  //商品保障描述
	private String dealerId;       //商家id
	
	public GoodsGuaranteeAddCommand(String guaranteeId, String guaranteeName, 
			String guaranteeDesc, String dealerId) throws NegativeException{
		if(StringUtils.isEmpty(guaranteeId)){
			throw new NegativeException(MCode.V_1, "请刷新页面获取商品保障ID");
		}
		if(StringUtils.isEmpty(guaranteeName) || StringUtils.isEmpty(guaranteeName.replaceAll(" ", "")) || guaranteeName.length() > 10) {
			throw new NegativeException(MCode.V_1, "商品保障名称必须为1-10字符");
		}
		if(StringUtils.isEmpty(dealerId)){
			throw new NegativeException(MCode.V_1, "未获取到商家ID");
		}
		this.guaranteeId   = guaranteeId;
		this.guaranteeName = guaranteeName;
		this.guaranteeDesc = guaranteeDesc;
		this.dealerId      = dealerId;
	}
	
	public String getGuaranteeId(){
		return guaranteeId;
	}
	
	public String getGuaranteeName(){
		return guaranteeName;
	}
	
	public String getGuaranteeDesc(){
		return guaranteeDesc;
	}
	
	public String getDealerId(){
		return dealerId;
	}

	@Override
	public String toString() {
		return "GoodsGuaranteeAddCommand [guaranteeId=" + guaranteeId + ", guaranteeName=" + guaranteeName
				+ ", guaranteeDesc=" + guaranteeDesc + ", dealerId=" + dealerId
				+ "]";
	}
	
}
