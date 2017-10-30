package cn.m2c.scm.port.adapter.restful.web.dealerorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.m2c.common.MCode;
import cn.m2c.common.MResult;
import cn.m2c.scm.application.order.DealerOrderApplication;
import cn.m2c.scm.application.order.SaleAfterOrderApp;
import cn.m2c.scm.domain.NegativeException;

@RestController
@RequestMapping("/scheduled")
public class ScheduledAgent {

	private final static Logger LOGGER = LoggerFactory.getLogger(ScheduledAgent.class);

	@Autowired
	DealerOrderApplication dealerOrderApplication;
	
	@Autowired
	SaleAfterOrderApp saleAfterOrderApplication;

	/**
	 * 判断是否已满足确认收货条件<待收货状态下七天后自动收货为完成状态>
	 * 
	 * @return
	 */
	@RequestMapping(value = "/statusFinishied", method = RequestMethod.PUT)
	public ResponseEntity<MResult> estimateOrderCinfim() {
		MResult result = new MResult(MCode.V_1);

		try {
			dealerOrderApplication.updateOrderStatus();
			result.setStatus(MCode.V_200);
		} catch (NegativeException ne) {
			result = new MResult(ne.getStatus(), ne.getMessage());
		} catch (Exception e) {
			LOGGER.error("修改订单状态发生错误", e);
			result = new MResult(MCode.V_400, e.getMessage());
		}
		return new ResponseEntity<MResult>(result, HttpStatus.OK);
	}

	/**
	 * 完成状态下超过7天变更为交易完成
	 * @return
	 */
	@RequestMapping(value = "/statusDealFinished",method = RequestMethod.PUT)
	public ResponseEntity<MResult> dealFinished() {
		
		MResult result = new MResult(MCode.V_1);

		try {
			dealerOrderApplication.updateDealFinished();
			result.setStatus(MCode.V_200);
		} catch (NegativeException ne) {
			result = new MResult(ne.getStatus(), ne.getMessage());
		} catch (Exception e) {
			LOGGER.error("修改订单状态发生错误", e);
			result = new MResult(MCode.V_400, e.getMessage());
		}
		return new ResponseEntity<MResult>(result, HttpStatus.OK);
	}
	
	/**
	 * 待付款状态下超过24H小时变更为已取消
	 * @return
	 */
	
	@RequestMapping(value = "/statusWaitPay",method = RequestMethod.PUT)
	public ResponseEntity<MResult> waitPay() {
		MResult result = new MResult(MCode.V_1);
		System.out.println("请求过来le ------------------------------------------");
		try {
			dealerOrderApplication.updateWaitPay();
			result.setStatus(MCode.V_200);
		} catch (NegativeException ne) {
			result = new MResult(ne.getStatus(), ne.getMessage());
		} catch (Exception e) {
			LOGGER.error("修改订单状态发生错误", e);
			result = new MResult(MCode.V_400, e.getMessage());
		}
		return new ResponseEntity<MResult>(result, HttpStatus.OK);
	}
	
	/**
	 * 商家同意售后状态下7天变更为交易关闭
	 * @return
	 */
	@RequestMapping(value = "/statusAgreeAfterSell",method = RequestMethod.PUT)
	public ResponseEntity<MResult> statusAgreeAfterSell() {
		MResult result = new MResult(MCode.V_1);

		try {
			saleAfterOrderApplication.updataStatusAgreeAfterSale();
			result.setStatus(MCode.V_200);
		} catch (NegativeException ne) {
			result = new MResult(ne.getStatus(), ne.getMessage());
		} catch (Exception e) {
			LOGGER.error("修改订单状态发生错误", e);
			result = new MResult(MCode.V_400, e.getMessage());
		}
		return new ResponseEntity<MResult>(result, HttpStatus.OK);
	}
}