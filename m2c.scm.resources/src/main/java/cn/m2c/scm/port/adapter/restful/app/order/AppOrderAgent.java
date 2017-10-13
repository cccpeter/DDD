package cn.m2c.scm.port.adapter.restful.app.order;

import cn.m2c.common.MCode;
import cn.m2c.common.MPager;
import cn.m2c.common.MResult;
import cn.m2c.scm.application.order.OrderApplication;
import cn.m2c.scm.application.order.data.representation.OrderNo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 订单
 * @author fanjc
 */
@RestController
@RequestMapping("/order")
public class AppOrderAgent {

    private final static Logger LOGGER = LoggerFactory.getLogger(AppOrderAgent.class);

    @Autowired
    OrderApplication orderApp;
    /**
     * 获取订单号
     * @return
     */
    @RequestMapping(value = "/id", method = RequestMethod.GET)
    public ResponseEntity<MResult> getOrderNo() {
    	MResult result = new MResult(MCode.V_1);
        try {
        	OrderNo orderId = orderApp.getOrderNo();
        	if (orderId != null && orderId.getOrderId() != null) {
        		result.setContent(orderId);
        		result.setStatus(MCode.V_200);
        	}
        	else {
        		result.setStatus(1); // 需要重新获取
        	}
        } catch (Exception e) {
            LOGGER.error("get order no error, e:", e);
            result = new MResult(MCode.V_400, e.getMessage());
        }
        return new ResponseEntity<MResult>(result, HttpStatus.OK);
    }

    /**
     * 提交订单     *
     * @param goodsId 商品ID
     * @return
     */
    @RequestMapping(value = "/app/add", method = RequestMethod.POST)
    public ResponseEntity<MResult> submitOrder(
            @RequestParam(value = "goodses", required = false) String goodses
            ,@RequestParam(value = "userId", required = false) String userId
            ,@RequestParam(value = "orderId", required = false) String orderId
            ,@RequestParam(value = "invoice", required = false) String invoice
            ,@RequestParam(value = "addr", required = false) String addr
            ,@RequestParam(value = "noted", required = false) String noted
            ,@RequestParam(value = "coupons", required = false) String coupons) {
    	MResult result = new MResult(MCode.V_1);
        try {
            Map map = new HashMap<>();
            map.put("goodsName", "跑步机");
            map.put("goodsImageUrl", "http://dl.m2c2017.com/3pics/20170822/W8bq135021.jpg");
            map.put("goodsPrice", 249000);
            result.setContent(map);
            result.setStatus(MCode.V_200);
        } catch (Exception e) {
            LOGGER.error("goods Detail Exception e:", e);
            result = new MResult(MCode.V_400, e.getMessage());
        }
        return new ResponseEntity<MResult>(result, HttpStatus.OK);
    }
    
    /**
     * 获取订单列表
     * @param userId 当前登录用户ID,app用户id
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<MPager> getOrderListByUser(
            @RequestParam(value = "userId", required = false) String userId
            ,@RequestParam(value = "pageIndex", required = false) int pageIndex
            ,@RequestParam(value = "pageNum", required = false) int pageNum
            ,@RequestParam(value = "status", required = false) int status
            ) {
    	MPager result = new MPager(MCode.V_1);
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("goodsName", "跑步机");
            map.put("goodsImageUrl", "http://dl.m2c2017.com/3pics/20170822/W8bq135021.jpg");
            map.put("goodsPrice", 249000);
            
            
            List<Map<String, Object>> orderList = new ArrayList<>();
            orderList.add(map);
            
            result.setContent(orderList);
            result.setPager(1, 1, 2);
            result.setStatus(MCode.V_200);
        } catch (Exception e) {
            LOGGER.error("get order list error, e:", e);
            result = new MPager(MCode.V_400, e.getMessage());
        }
        return new ResponseEntity<MPager>(result, HttpStatus.OK);
    }
}