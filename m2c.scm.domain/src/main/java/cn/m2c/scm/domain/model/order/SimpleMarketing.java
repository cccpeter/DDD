package cn.m2c.scm.domain.model.order;

import cn.m2c.ddd.common.domain.model.IdentifiedDomainObject;
/***
 * 简单营销策略
 * @author fanjc
 *
 */
public class SimpleMarketing extends IdentifiedDomainObject {

	/**订单ID*/
	private String orderId;
	/**优惠券ID*/
	private String marketingId;
	/**1可用，0不可用*/
	private Integer status = 1;
	
	public SimpleMarketing() {
		super();
	}
	
	public SimpleMarketing(String orderId, String couponId) {
		super();
	}
	
	public void disenabled() {
		status = 0;
	}
}