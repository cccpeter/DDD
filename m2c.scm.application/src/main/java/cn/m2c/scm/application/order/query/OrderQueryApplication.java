package cn.m2c.scm.application.order.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import cn.m2c.common.JsonUtils;
import cn.m2c.common.MCode;
import cn.m2c.common.RedisUtil;
import cn.m2c.ddd.common.port.adapter.persistence.springJdbc.SupportJdbcTemplate;
import cn.m2c.scm.application.order.command.ExpressInfoBean;
import cn.m2c.scm.application.order.command.GetOrderCmd;
import cn.m2c.scm.application.order.data.bean.AppOrderBean;
import cn.m2c.scm.application.order.data.bean.AppOrderDtl;
import cn.m2c.scm.application.order.data.bean.DealerOrderBean;
import cn.m2c.scm.application.order.data.bean.OrderDetailBean;
import cn.m2c.scm.application.order.data.bean.OrderExpressBean;
import cn.m2c.scm.application.order.data.bean.OrderExpressDetailBean;
import cn.m2c.scm.application.order.data.bean.ShipExpressBean;
import cn.m2c.scm.application.order.data.bean.SkuNumBean;
import cn.m2c.scm.application.order.data.bean.UserOrderStatic;
import cn.m2c.scm.application.order.data.representation.OptLogBean;
import cn.m2c.scm.domain.NegativeException;
import cn.m2c.scm.domain.service.order.OrderService;

/**
 * 订单查询
 * @author fanjc
 * created date 2017年10月17日
 * copyrighted@m2c
 */
@Service
public class OrderQueryApplication {
	/**调试打日志用*/
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderQueryApplication.class);
	
	@Resource
    private SupportJdbcTemplate supportJdbcTemplate;
	@Autowired
	OrderService orderService;

    public SupportJdbcTemplate getSupportJdbcTemplate() {
        return supportJdbcTemplate;
    }
    /***
     * 商家平台，获取订单列表
     * @return
     */
    public List<DealerOrderBean> getDealerOrderList(String dealerId) {
    	return null;
    }
    
    /***
     * 获取商家订单操作日志列表
     * @return
     * @throws NegativeException 
     */
    public List<OptLogBean> getDealerOrderOptLog(String orderId, String dealerOrderId, Integer pageNum, Integer rows) throws NegativeException {
    	List<OptLogBean> logList = null;
		try {
			StringBuilder sql = new StringBuilder(200);
			List<Object> params = new ArrayList<>(2);
			sql.append("SELECT order_no, dealer_order_no, opt_content, opt_user, created_date FROM t_scm_order_opt_log ");
			sql.append(" WHERE order_no=?");
			if(orderId!= null && !"".equals(orderId)){
				params.add(orderId);
			}
			
			if (!StringUtils.isEmpty(dealerOrderId)) {
				sql.append(" AND ((dealer_order_no=? AND _type=2) OR _type=1)");
				params.add(dealerOrderId);
			}
			sql.append(" ORDER BY created_date DESC ");
			sql.append(" LIMIT ?,?");
			params.add(rows*(pageNum - 1));
			params.add(rows);
			System.out.println("----操作日志列表："+sql.toString());
			logList = this.getSupportJdbcTemplate().queryForBeanList(sql.toString(), OptLogBean.class, params.toArray());
		} catch (Exception e) {
			LOGGER.error("---查询日志时出错 ",e);
			throw new NegativeException(500, "查询操作日志时出错 ");
		}
		return logList;
    }
    
    /***
     * 满足条件的记录条数
     * @return
     * @throws NegativeException 
     */
    public Integer getDealerOrderOptLogTotal(String orderId, String dealerOrderId,Integer pageNum,Integer rows) throws NegativeException {
    	Integer total = 0;
		try {
			StringBuilder sql = new StringBuilder(200);
			List<Object> params = new ArrayList<>(2);
			sql.append("SELECT count(1) FROM t_scm_order_opt_log ");
			sql.append(" WHERE order_no=?");
			params.add(orderId);
			/*if (!StringUtils.isEmpty(dealerOrderId)) {
				sql.append(" AND dealer_order_no=?");
				params.add(dealerOrderId);
			}*/
			total = this.getSupportJdbcTemplate().jdbcTemplate().queryForObject(sql.toString(), params.toArray(), Integer.class);
		} catch (Exception e) {
			LOGGER.error("---查询日志条数时出错 ",e);
			throw new NegativeException(500, "查询操作日志条数时出错 ");
		}
		return total;
    }
    
    /***
     * 根据订单号获取订单下的优惠券
     * @param orderId
     * @return
     */
    public List<String> getCouponsByOrderId(String orderId) throws NegativeException {
    	if (StringUtils.isEmpty(orderId))
    		return null;
    	LOGGER.info("+++++orderId++++++"+orderId);
    	List<String> rs = null;
    	try {
    		rs = supportJdbcTemplate.jdbcTemplate().queryForList("select coupon_user_id from t_scm_order_coupon_used where order_id=? and _status=1 ", String.class, orderId);
    		LOGGER.info("+++++数据库读取的优惠券id列表++++++"+JsonUtils.toStr(rs));
    	}
    	catch (Exception e) {
    		LOGGER.error("===fanjc==获取订单下的优惠券出错",e);
			throw new NegativeException(500, "获取订单下的优惠券出错");
    	}
    	return rs;
    }
    
    /***
     * 根据订单号获取订单下的商品ID及数量
     * @param orderId
     * @return
     */
    public Map<String, Integer> getSkusByOrderId(String orderId) throws NegativeException {
    	if (StringUtils.isEmpty(orderId))
    		return null;
    	Map<String, Integer> rs = null;
    	try {
    		List<SkuNumBean> ls = supportJdbcTemplate.queryForBeanList("select sku_id, sell_num from t_scm_order_detail where order_id=?", SkuNumBean.class, orderId);
    		
    		if (ls == null || ls.size() < 1)
    			return rs;
    		
    		rs = new HashMap<String, Integer>();
    		for (SkuNumBean sb : ls) {
    			rs.put(sb.getSkuId(), sb.getNum());
    		}
    	}
    	catch (Exception e) {
    		LOGGER.error("===fanjc==获取订单下的SKU及数量出错",e);
			throw new NegativeException(500, "获取订单下的SKU及数量出错");
    	}
    	return rs;
    }
    /**
     * 获取商家订单详情
     * @param dealerOrderId
     * @throws NegativeException 
     */
	public DealerOrderBean getDealerOrder(String dealerOrderId) throws NegativeException {
		DealerOrderBean dealerOrderBean = null;
		String sql = "SELECT * FROM t_scm_order_dealer WHERE 1=1 AND dealer_order_id=?";
		try {
			dealerOrderBean = supportJdbcTemplate.queryForBean(sql, DealerOrderBean.class,dealerOrderId);
			if(dealerOrderBean!=null){
				dealerOrderBean.setOrderDtls(getOrderDetail(dealerOrderBean.getDealerOrderId()));
			}
		} catch (Exception e) {
			LOGGER.error("商家订单查询出错",e);
			throw new NegativeException(500, "商家订单查询出错");
		}
		return dealerOrderBean;
	}
	/**
	 * 根据商家订单id获取
	 * @param dealerOrderId
	 * @return
	 * @throws NegativeException 
	 */
	private List<OrderDetailBean> getOrderDetail(String dealerOrderId) throws NegativeException {
		List<OrderDetailBean> orderList = null;
		String sql = "SELECT * FROM t_scm_order_detail WHERE dealer_order_id=?";
		try {
			orderList = this.supportJdbcTemplate.queryForBeanList(sql, OrderDetailBean.class, dealerOrderId);
			//去掉订单中的审核通过的退货单
			if(orderList!=null && orderList.size()>0){
				for (int i = 0; i < orderList.size(); i++) {
					if(checkIsReturnOrder(orderList.get(i).getSkuId(),orderList.get(i).getDealerOrderId())){
						orderList.remove(i);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("订单查询出错",e);
			throw new NegativeException(500, "订单查询出错");
		}
		return orderList;
	}
	/**
	 * 判断此sku是否走售后流程（如果售后流程就不显示出来）
	 * @param skuId
	 * @param dealerOrderId
	 * @throws NegativeException 
	 */
	private boolean checkIsReturnOrder(String skuId, String dealerOrderId) throws NegativeException {
		boolean isReturnOrder = false;
		List<Object> param = new ArrayList<Object>();
		try {
			param.add(skuId);
			param.add(dealerOrderId);
			String sql = "SELECT count(*) FROM t_scm_order_after_sell WHERE _status=9 AND sku_id=? AND dealer_order_id=?";
			Integer returnOrderCount =this.supportJdbcTemplate.jdbcTemplate().queryForObject(sql,Integer.class,param.toArray()); 
			if(returnOrderCount!=null && returnOrderCount==1){
				isReturnOrder =  true;
			}
		} catch (Exception e) {
			LOGGER.error("---判断sku是否是售后单出错",e);
			throw new NegativeException(500, "判断sku是否是售后单出错");
		}
		return isReturnOrder;
	}
	/**
	 * 查询所有的物流公司信息
	 * @return
	 * @throws NegativeException 
	 */
	public List<OrderExpressBean> getAllExpress() throws NegativeException {
		List<OrderExpressBean> expressList = null;
		try {
			String sql = "SELECT * FROM t_scm_order_exp_company WHERE exp_status=1 ORDER BY exp_company_code";
			expressList = this.getSupportJdbcTemplate().queryForBeanList(sql, OrderExpressBean.class);
		} catch (Exception e) {
			LOGGER.error("---查询物流公司列表出错",e);
			throw new NegativeException(500, "查询物流公司列表出错");
		}
		return expressList;
	}
	/***
	 * 获取APP订单列表页面
	 * @param userId
	 * @param status
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws NegativeException
	 */
	public List<AppOrderBean> getAppOrderList(String userId, Integer status, Integer commentStatus,
			int pageIndex, int pageSize, String keyword) throws NegativeException {
		List<AppOrderBean> result = null;
		try {
			List<Object> params = new ArrayList<>(4);
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT a.province_code, b.coupon_discount, a.province, a.city, a.city_code, a.area_code, a.area_county, a.street_addr\r\n");
			sql.append(", a.order_freight, a.order_id, a.goods_amount, a.plateform_discount, a.dealer_discount\r\n")
			.append(", b.order_freight dOrderFreight, b.goods_amount dGoodsAmount, b.plateform_discount dPlateformDiscount, b.dealer_discount dDealerDiscount\r\n")
			.append(", b.invoice_code, b.invoice_header, b.invoice_name, b.invoice_type, a.created_date, b._status, a.coupon_discount mainCouponDiscount\r\n") 
			.append(", b.dealer_id, d.shop_name, b.dealer_order_id\r\n") 
			.append("FROM t_scm_order_dealer b \r\n")
			.append("LEFT OUTER JOIN t_scm_order_main a ON a.order_id=b.order_id \r\n") 
			.append("LEFT OUTER JOIN t_scm_dealer c ON c.dealer_id = b.dealer_id \r\n")
			.append("LEFT OUTER JOIN t_scm_dealer_shop d ON d.dealer_id = c.dealer_id \r\n")
			.append("WHERE a.user_id=?  AND a.del_flag=0 AND b.del_flag=0 ");
			params.add(userId);
			
			if (commentStatus != null && commentStatus == 1) {
				sql.append(" AND b.dealer_order_id in (SELECT DISTINCT e.dealer_order_id FROM t_scm_order_detail e WHERE e.comment_status=0 AND e._status IN (?))");//,?,?
				status = 3;
				params.add(status);
				// 只能查正常的已经收货的可评价的订单
				//params.add(4);
				//params.add(5);
			}
			else if (status != null && status==2) {
				sql.append(" AND b._status IN (?, ?) ");
				params.add(1);
				params.add(2);
			}
			else if (status != null && status> -2) {
				sql.append(" AND b._status=?");
				params.add(status);
			}
			
			if (!StringUtils.isEmpty(keyword)) {
				sql.append(" AND (b.order_id LIKE concat('%',?,'%') OR (b.dealer_order_id IN (SELECT DISTINCT e.dealer_order_id FROM t_scm_order_detail e WHERE e.goods_name LIKE concat('%',?,'%'))))");
				params.add(keyword);
				params.add(keyword);
			}
			sql.append(" ORDER BY a.order_id DESC, b.dealer_order_id DESC, a.created_date DESC");
			
			sql.append(" LIMIT ?,? ");
			params.add((pageIndex - 1) * pageSize);
			params.add(pageSize);
			
			result = this.supportJdbcTemplate.queryForBeanList(sql.toString(), AppOrderBean.class, params.toArray());
			
			if (result != null) {
				int sz = result.size();
				String tmpOrderId = null; 
				AppOrderBean tmp = null;
				for (int i=sz - 1; i> -1; i--) {
					AppOrderBean o = result.get(i);
					if (o.getStatus() <= 0 && !o.getOrderId().equals(tmpOrderId)) {
						tmp = o;
						tmpOrderId = o.getOrderId();
						o.setCouponDiscount(o.getMainCouponDiscount());
						sql.delete(0, sql.length());
						sql.append("SELECT a.goods_icon, a.goods_name, a.goods_title, a.sku_name, a.sku_id, a.sell_num, a.discount_price, a.freight, ")
						.append(" a.goods_amount, b._status afterStatus, a.goods_id, a.goods_type_id, a.sort_no\r\n") 
						.append(" FROM t_scm_order_detail a LEFT OUTER JOIN t_scm_order_after_sell b ON b.order_id=a.order_id AND b.dealer_order_id = a.dealer_order_id AND b._status NOT IN(-1,3) ")
						.append(" AND b.sku_id=a.sku_id AND b.sort_no=a.sort_no \r\n")
						.append(" WHERE a.order_id=? ");
						o.setGoodses(this.supportJdbcTemplate.queryForBeanList(sql.toString(), 
								OrderDetailBean.class, new Object[] {tmpOrderId}));
					}
					else if (o.getStatus() <= 0 && o.getOrderId().equals(tmpOrderId)) {
						result.remove(i);
						o.setCouponDiscount(o.getMainCouponDiscount());
						/*tmp.setDealerDiscount(tmp.getDealerDiscount() + o.getDealerDiscount());
						tmp.setPlateFormDiscount(tmp.getPlateFormDiscount() + o.getPlateFormDiscount());
						tmp.setGoodAmount(tmp.getGoodAmount() + o.getGoodAmount());
						tmp.setOderFreight(tmp.getOderFreight() + o.getOderFreight());*/
					}
					else {
						tmpOrderId = o.getOrderId();
						sql.delete(0, sql.length());
						sql.append("SELECT a.goods_icon, a.goods_name, a.goods_title, a.sku_name, a.sku_id, a.sell_num, a.discount_price, a.freight, a.goods_amount\r\n")
						.append(", a.express_way , a.express_phone, a.express_no , a.express_code, a.express_name, a.sort_no\r\n")
						.append(", a.comment_status , b._status afterStatus, a.goods_id, a.goods_type_id FROM t_scm_order_detail a\r\n")
						.append(" LEFT OUTER JOIN t_scm_order_after_sell b ON b.order_id=a.order_id AND b.dealer_order_id = a.dealer_order_id AND b._status NOT IN (-1, 3) AND b.sku_id=a.sku_id")
						.append(" AND b.sort_no=a.sort_no \r\n")
						.append(" WHERE a.order_id=? AND a.dealer_order_id=?");
						o.setGoodses(this.supportJdbcTemplate.queryForBeanList(sql.toString(), 
								OrderDetailBean.class, new Object[] {tmpOrderId, o.getDealerOrderId()}));
					}
				}
			}
			
		} catch (Exception e) {
			LOGGER.error("---查询APP订单列表出错"+e.getMessage(),e);
	           StackTraceElement stackTraceElement= e.getStackTrace()[0]; 
	           LOGGER.error("File=" + stackTraceElement.getFileName()); 
	           LOGGER.error("Line=" + stackTraceElement.getLineNumber()); 
	           LOGGER.error("Method=" + stackTraceElement.getMethodName()); 
	           
			throw new NegativeException(500, "查询APP订单列表出错");
		}
		return result;
	}
	
	/***
	 * 获取APP订单列表页面 总数
	 * @param userId
	 * @param status
	 * @return
	 * @throws NegativeException
	 */
	public Integer getAppOrderListTotal(String userId, Integer status, Integer commentStatus
			,String keyword) throws NegativeException {
		Integer result = 0;
		try {
			List<Object> params = new ArrayList<>(2);
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count(1) FROM t_scm_order_dealer b \r\n")
			.append("LEFT OUTER JOIN t_scm_order_main a ON a.order_id=b.order_id \r\n") 
			.append("LEFT OUTER JOIN t_scm_dealer c ON c.dealer_id = b.dealer_id \r\n")
			.append("WHERE a.user_id=? AND a.del_flag=0 AND b.del_flag=0 ");
			params.add(userId);
			
			if (commentStatus != null && commentStatus == 1) {
				sql.append(" AND b.dealer_order_id in (SELECT e.dealer_order_id FROM t_scm_order_detail e WHERE e.comment_status=0 AND e._status=?)");
				status = 3;
				params.add(status);
			}
			else if (status != null && status==2) {
				sql.append(" AND b._status IN (?, ?) ");
				params.add(1);
				params.add(2);
			}
			else if (status != null && status> -2) {
				sql.append(" AND b._status=?");
				params.add(status);
			}
			
			if (!StringUtils.isEmpty(keyword)) {
				sql.append(" AND (b.order_id LIKE concat('%',?,'%') OR (b.dealer_order_id IN (SELECT e.dealer_order_id FROM t_scm_order_detail e WHERE e.goods_name LIKE concat('%',?,'%'))))");
				params.add(keyword);
				params.add(keyword);
			}
			
			result = this.getSupportJdbcTemplate().jdbcTemplate().queryForObject(sql.toString(), params.toArray(), Integer.class);
		} catch (Exception e) {
			LOGGER.error("---查询APP订单列表出错 ",e);
			throw new NegativeException(500, "查询APP订单列表出错");
		}
		return result;
	}
	/***
	 * 获取订单详情
	 * @param cmd
	 * @return
	 * @throws NegativeException 
	 */
	public AppOrderDtl getOrderDtl(GetOrderCmd cmd) throws NegativeException {
		AppOrderDtl result = null;
		try {
			List<Object> params = new ArrayList<>(4);
			StringBuilder sql = new StringBuilder();
			
			if (StringUtils.isEmpty(cmd.getDealerOrderId())) {
				
				sql.append("SELECT a.province_code, a.coupon_discount, a.province, a.city, a.city_code, a.area_code, a.area_county, a.street_addr\r\n")
				.append(",a.post_code, a.order_freight, a.order_id, a.goods_amount, a.plateform_discount, a.dealer_discount\r\n")
				.append(", b.invoice_code, b.invoice_header, b.invoice_name, b.invoice_type, a.created_date, b._status\r\n") 
				.append(", b.dealer_id, d.shop_name, b.dealer_order_id, b.rev_phone, b.rev_person, a.pay_way, a.pay_no\r\n") 
				.append("FROM t_scm_order_dealer b \r\n")
				.append("LEFT OUTER JOIN t_scm_order_main a ON a.order_id=b.order_id \r\n") 
				.append("LEFT OUTER JOIN t_scm_dealer c ON c.dealer_id = b.dealer_id \r\n")
				.append("LEFT OUTER JOIN t_scm_dealer_shop d ON b.dealer_id = d.dealer_id \r\n")
				.append("WHERE a.user_id=? AND b.del_flag=0 ");
				params.add(cmd.getUserId());		
				
				if (!StringUtils.isEmpty(cmd.getOrderId())) {
					sql.append(" AND b.order_id =?");
					params.add(cmd.getOrderId());
				}
				if (!StringUtils.isEmpty(cmd.getDealerOrderId())) {
					sql.append(" AND b.dealer_order_id =?");
					params.add(cmd.getDealerOrderId());
				}
				sql.append(" limit 1");
			}
			else {
				sql.append("SELECT b.province_code, b.coupon_discount, b.province, b.city, b.city_code, b.area_code, b.area_county, b.street_addr\r\n")
				.append(", b.post_code, a.order_freight, a.order_id, a.goods_amount, a.plateform_discount, a.dealer_discount, d.customer_service_tel\r\n")
				.append(", b.invoice_code, b.invoice_header, b.invoice_name, b.invoice_type, a.created_date, b._status\r\n") 
				.append(", b.order_freight dOrderFreight, b.goods_amount dGoodsAmount, b.plateform_discount dPlateformDiscount, b.dealer_discount dDealerDiscount\r\n")
				.append(", b.dealer_id, d.shop_name, b.dealer_order_id,b.rev_phone, b.rev_person, a.pay_way, a.pay_no, a.coupon_discount mainCouponDiscount\r\n") 
				.append("FROM t_scm_order_dealer b \r\n")
				.append("LEFT OUTER JOIN t_scm_order_main a ON a.order_id=b.order_id \r\n") 
				.append("LEFT OUTER JOIN t_scm_dealer c ON c.dealer_id = b.dealer_id \r\n")
				.append("LEFT OUTER JOIN t_scm_dealer_shop d ON b.dealer_id = d.dealer_id \r\n")
				.append("WHERE a.user_id=? AND b.del_flag=0 ");
				params.add(cmd.getUserId());		
				
				if (!StringUtils.isEmpty(cmd.getOrderId())) {
					sql.append(" AND b.order_id =?");
					params.add(cmd.getOrderId());
				}
				if (!StringUtils.isEmpty(cmd.getDealerOrderId())) {
					sql.append(" AND b.dealer_order_id =?");
					params.add(cmd.getDealerOrderId());
				}
			}
			result = this.supportJdbcTemplate.queryForBean(sql.toString(), AppOrderDtl.class, params.toArray());
			
			if (result != null) {
				sql.delete(0, sql.length());
				sql.append("SELECT a.goods_icon, a.goods_name, a.goods_title, a.sku_name, a.sku_id, a.sell_num, a.discount_price, a.freight, a.goods_amount, a.sort_no, a.is_special, a.special_price \r\n")
				.append(", b._status afterStatus, a.goods_id, a.goods_type_id, a.express_no, a.express_code, a.express_name, a.express_way, a.comment_status, a.is_change,a.change_price ")
				.append(" FROM t_scm_order_detail a ")
				.append(" LEFT OUTER JOIN t_scm_order_after_sell b ON b.order_id=a.order_id AND b.dealer_order_id = a.dealer_order_id AND b._status NOT IN(-1, 3) AND a.sku_id=b.sku_id")
				.append(" AND b.sort_no=a.sort_no \r\n")
				.append(" WHERE a.order_id=? ");
				
				Object [] pa = null;
				if (result.getStatus() >= 1) {
					sql.append("AND a.dealer_order_id=?");
					pa = new Object[] {result.getOrderId(), result.getDealerOrderId()};
				}
				else {
					if(result.getMainCouponDiscount() > 0)
						result.setCouponDiscount(result.getMainCouponDiscount());
					pa = new Object[] {result.getOrderId()};
				}
				result.setGoodses(this.supportJdbcTemplate.queryForBeanList(sql.toString(), 
						OrderDetailBean.class, pa));
				
				sql.delete(0, sql.length());
				sql.append("SELECT count(1) FROM t_scm_order_after_sell a ")
				.append(" WHERE a.dealer_order_id=? ");
				pa = new Object[] {result.getDealerOrderId()};
				Integer count = this.supportJdbcTemplate.jdbcTemplate().queryForObject(sql.toString(), pa, Integer.class);
				if (count > 0)
					result.setHasSaleAfter(count);
			}
			
		} catch (Exception e) {
			LOGGER.error("---查询APP订单详情出错",e);
			throw new NegativeException(500, "查询APP订单详情出错");
		}
		return result;
	}
	
	/***
	 * 获取APP可售后的订单列表
	 * @param userId
	 * @param status
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws NegativeException
	 */
	public List<AppOrderBean> getMaySaleAfterList(String userId, int pageIndex, int pageSize) throws NegativeException {
		List<AppOrderBean> result = null;
		try {
			List<Object> params = new ArrayList<>(4);
			StringBuilder sql = new StringBuilder();
			/*sql.append("SELECT a.province_code, a.province, a.city, a.city_code, a.area_code, a.area_county, a.street_addr\r\n")
			.append(", a.order_freight, a.order_id, a.goods_amount, a.plateform_discount, a.dealer_discount\r\n")
			.append(", b.invoice_code, b.invoice_header, b.invoice_name, b.invoice_type, a.created_date, b._status\r\n") 
			.append(", b.dealer_id, c.dealer_name, b.dealer_order_id\r\n") 
			.append("FROM t_scm_order_dealer b \r\n")
			.append("LEFT OUTER JOIN t_scm_order_main a ON a.order_id=b.order_id \r\n") 
			.append("LEFT OUTER JOIN t_scm_dealer c ON c.dealer_id = b.dealer_id \r\n")
			.append("WHERE a.user_id=?  AND b.del_flag=0 AND (b._status IN (1, 2, 3))")
			.append("AND b.dealer_order_id IN (SELECT cc.dealer_order_id FROM t_scm_order_detail cc \r\n")
			.append("WHERE cc.sort_no NOT IN(SELECT aa.sort_no FROM t_scm_order_detail aa, t_scm_order_after_sell bb ")
			.append("WHERE aa.dealer_order_id=b.dealer_order_id AND aa.dealer_order_id=bb.dealer_order_id AND aa.sku_id=bb.sku_id AND bb._status NOT IN(-1, 3) AND aa.sort_no = bb.sort_no ")
			.append("AND cc.dealer_order_id = aa.dealer_order_id )) ");*/
			
			sql.append("SELECT a.province_code, a.province, a.city, a.city_code, a.area_code, a.area_county, a.street_addr\r\n")
					.append(", a.order_freight, a.order_id, a.goods_amount, a.plateform_discount, a.dealer_discount\r\n")
					.append(", b.invoice_code, b.invoice_header, b.invoice_name, b.invoice_type, a.created_date, b._status\r\n")
					.append(", b.dealer_id, c.dealer_name, b.dealer_order_id\r\n")
					.append("FROM t_scm_order_dealer b \r\n")
					.append("LEFT OUTER JOIN t_scm_order_main a ON a.order_id=b.order_id\r\n")
					.append("LEFT OUTER JOIN t_scm_dealer c ON c.dealer_id = b.dealer_id \r\n")
					.append("WHERE a.user_id=?  AND b.del_flag=0 AND (b._status IN (1, 2, 3, 4, 5))\r\n")      //添加售后保护期已过的订单
					.append("AND b.dealer_order_id IN (SELECT cc.dealer_order_id FROM t_scm_order_detail cc \r\n")
					.append("WHERE (cc.sort_no = 0 AND cc.sort_no NOT IN(SELECT aa.sort_no FROM t_scm_order_detail aa, t_scm_order_after_sell bb \r\n")
					.append("WHERE aa.dealer_order_id = b.dealer_order_id \r\n")
					.append("AND aa.dealer_order_id=bb.dealer_order_id \r\n")
					.append("AND aa.sku_id=bb.sku_id \r\n")
					.append("AND bb._status NOT IN(-1, 3) \r\n")
					.append("AND aa.sort_no = bb.sort_no \r\n")
					.append("AND cc.dealer_order_id = aa.dealer_order_id)) OR (\r\n")
					.append("cc.sort_no NOT IN(SELECT aa.sort_no FROM t_scm_order_detail aa, t_scm_order_after_sell bb \r\n")
					.append("WHERE aa.dealer_order_id = b.dealer_order_id \r\n")
					.append("AND aa.dealer_order_id=bb.dealer_order_id \r\n")
					.append("AND aa.sku_id=bb.sku_id \r\n")
					.append("AND bb._status NOT IN(-1, 3) \r\n")
					.append("AND aa.sort_no = bb.sort_no \r\n")
					.append("AND cc.dealer_order_id = aa.dealer_order_id)\r\n")
					.append("))\r\n");
			
			params.add(userId);
			
			sql.append(" ORDER BY a.order_id DESC, a.created_date DESC ");
			
			sql.append(" LIMIT ?,? ");
			params.add((pageIndex - 1) * pageSize);
			params.add(pageSize);
			
			System.out.println("outside SQL--------------------->"+sql);
			result = this.supportJdbcTemplate.queryForBeanList(sql.toString(), AppOrderBean.class, params.toArray());
			if (result != null) {
				int sz = result.size();
				String tmpOrderId = null; 
				for (int i=sz - 1; i> -1; i--) {
					AppOrderBean o = result.get(i);
					tmpOrderId = o.getOrderId();
					sql.delete(0, sql.length());
					sql.append("SELECT a.goods_icon, a.goods_name, a.goods_title, a.sku_name, a.sku_id, a.sell_num, a.discount_price, a.freight, a.goods_amount\r\n")
					.append(", a.comment_status ,a.goods_id, a.goods_type_id, a.is_change, a.change_price, a.sort_no, a.is_special, a.special_price FROM t_scm_order_detail a \r\n")
					.append(" WHERE a.order_id=? AND a.dealer_order_id=? AND ((a.sort_no=0 AND a.sku_id NOT IN (SELECT b.sku_id FROM t_scm_order_after_sell b WHERE b._status NOT IN(-1, 3) AND b.dealer_order_id=a.dealer_order_id AND b.order_id=a.order_id AND b.sort_no=a.sort_no))")
					.append(" OR (a.sort_no !=0 AND a.sort_no NOT IN (SELECT b.sort_no FROM t_scm_order_after_sell b WHERE b._status NOT IN(-1, 3) AND b.dealer_order_id=a.dealer_order_id AND b.order_id=a.order_id AND b.sort_no=a.sort_no)))")
					;
					System.out.println("Inside SQL----------------------------->"+sql);
					o.setGoodses(this.supportJdbcTemplate.queryForBeanList(sql.toString(), 
							OrderDetailBean.class, new Object[] {tmpOrderId, o.getDealerOrderId()}));
				}
			}
			
		} catch (Exception e) {
			LOGGER.error("---查询APP可售后的订单列表出错"+e.getMessage(),e);
			throw new NegativeException(500, "查询APP可售后的订单列表出错");
		}
		return result;
	}
	
	/***
	 * 获取APP可售后的订单列表
	 * @param userId
	 * @param status
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws NegativeException
	 */
	public Integer getMaySaleAfterListTotal(String userId) throws NegativeException {
		Integer result = 0;
		try {
			List<Object> params = new ArrayList<>(4);
			StringBuilder sql = new StringBuilder();
			/*sql.append("SELECT count(1) FROM t_scm_order_dealer b \r\n")
			.append("LEFT OUTER JOIN t_scm_order_main a ON a.order_id=b.order_id \r\n") 
			.append("LEFT OUTER JOIN t_scm_dealer c ON c.dealer_id = b.dealer_id \r\n")
			.append("WHERE a.user_id=?  AND b.del_flag=0 AND (b._status IN (1, 2, 3))")
			.append("AND b.dealer_order_id IN (SELECT DISTINCT aa.dealer_order_id FROM t_scm_order_detail aa \r\n")
			.append("WHERE aa.sku_id NOT IN (SELECT bb.sku_id FROM t_scm_order_after_sell bb WHERE bb._status NOT IN(-1, 3) AND bb.dealer_order_id=aa.dealer_order_id AND bb.order_id=aa.order_id AND bb.sort_no=aa.sort_no))");*/
			sql.append("SELECT count(1) FROM t_scm_order_dealer b \r\n")
			.append("LEFT OUTER JOIN t_scm_order_main a ON a.order_id=b.order_id\r\n")
			.append("LEFT OUTER JOIN t_scm_dealer c ON c.dealer_id = b.dealer_id \r\n")
			.append("WHERE a.user_id=?  AND b.del_flag=0 AND (b._status IN (1, 2, 3))\r\n")
			.append("AND b.dealer_order_id IN (SELECT cc.dealer_order_id FROM t_scm_order_detail cc \r\n")
			.append("WHERE (cc.sort_no = 0 AND cc.sort_no NOT IN(SELECT aa.sort_no FROM t_scm_order_detail aa, t_scm_order_after_sell bb \r\n")
			.append("WHERE aa.dealer_order_id = b.dealer_order_id \r\n")
			.append("AND aa.dealer_order_id=bb.dealer_order_id \r\n")
			.append("AND aa.sku_id=bb.sku_id \r\n")
			.append("AND bb._status NOT IN(-1, 3) \r\n")
			.append("AND aa.sort_no = bb.sort_no \r\n")
			.append("AND cc.dealer_order_id = aa.dealer_order_id)) OR (\r\n")
			.append("cc.sort_no NOT IN(SELECT aa.sort_no FROM t_scm_order_detail aa, t_scm_order_after_sell bb \r\n")
			.append("WHERE aa.dealer_order_id = b.dealer_order_id \r\n")
			.append("AND aa.dealer_order_id=bb.dealer_order_id \r\n")
			.append("AND aa.sku_id=bb.sku_id \r\n")
			.append("AND bb._status NOT IN(-1, 3) \r\n")
			.append("AND aa.sort_no = bb.sort_no \r\n")
			.append("AND cc.dealer_order_id = aa.dealer_order_id)\r\n")
			.append("))\r\n");
			
			params.add(userId);
			
			result = this.getSupportJdbcTemplate().jdbcTemplate().queryForObject(sql.toString(), params.toArray(), Integer.class);
			
		} catch (Exception e) {
			LOGGER.error("---查询APP可售后的订单列表出错"+e.getMessage(),e);
			throw new NegativeException(500, "查询APP可售后的订单列表出错");
		}
		return result;
	}
	/**
	 * 调用第三方查询物流信息
	 * @param com
	 * @param nu
	 * @return
	 * @throws NegativeException 
	 */
	public String getExpressJson(String com, String nu) throws NegativeException {
		String expressInfo = null;
		String press = null;
		
		try {
			String key = ("scm.order.press."+nu).trim();
			press = RedisUtil.getString(key);         //取出缓存
			if(StringUtils.isEmpty(press)) {           //缓存没数据调用查询物流信息放入缓存
			 expressInfo = orderService.getExpressInfo(com, nu);       
			 press = expressInfo;
			 RedisUtil.setString(key,30*60,press);
			}
		} catch (Exception e) {
			LOGGER.error("---查询APP物流列表出错"+e.getMessage(),e);
			throw new NegativeException(500, "查询APP物流列表出错");
		}
		return press;
	}
	/***
	 * 获取用户对应的订单统计
	 */
	public UserOrderStatic getUserOrderStatics(String userId) throws NegativeException {
		UserOrderStatic result = null;
		try {
			List<Object> params = new ArrayList<>(4);
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count(1) aa\r\n") // 未付订单数
			.append("FROM t_scm_order_dealer b LEFT OUTER JOIN t_scm_order_main a ON a.order_id = b.order_id\r\n") 
			.append("WHERE	a.user_id = ? AND b._status IN (1, 2)\r\n") 
			.append("UNION ALL\r\n") 
			.append("	SELECT count(1) aa	FROM t_scm_order_main a\r\n") // 待收货数
			.append("	WHERE	a.user_id = ? AND a._status = 0\r\n") 
			.append("	UNION ALL\r\n") 
			.append("  SELECT	count(1) aa	\r\n") // 待评论数
			.append("  FROM t_scm_order_main a,	t_scm_order_dealer b\r\n") 
			.append("	WHERE	a.user_id = ? AND b._status >= 3\r\n") 
			.append("	AND a.order_id=b.order_id AND b.dealer_order_id IN (\r\n") 
			.append("	SELECT DISTINCT	c.dealer_order_id\r\n") 
			.append("	FROM t_scm_order_detail c\r\n")
			.append("    WHERE	c.comment_status = 0 AND c._status=3 )");
			
			params.add(userId);
			params.add(userId);
			params.add(userId);
			
			List<Map<String, Object>> datas = supportJdbcTemplate.jdbcTemplate().queryForList(sql.toString(), params.toArray());
			result = new UserOrderStatic();
			if (datas != null) {
				for (int i=0; i< datas.size(); i++) {
					Map<String, Object> a = datas.get(i);
					switch (i) {
					case 0:
						result.setWaitRecs(((Long)a.get("aa")).intValue());
						break;
					case 1:
						result.setWaitPays(((Long)a.get("aa")).intValue());						
						break;
					case 2:
						result.setWaitComments(((Long)a.get("aa")).intValue());
						break;
					}
				}
			}
			
		} catch (Exception e) {
			LOGGER.error("---查询用户对应的订单统计出错"+e.getMessage(),e);
			throw new NegativeException(500, "查询用户对应的订单统计出错");
		}
		return result;
	}
	
	/**
	 * 查询订单配送详情信息总数
	 * @param dealerOrderId
	 * @return
	 * @throws NegativeException
	 */
	public Integer getOrderExpressDetailTotal(String dealerOrderId) throws NegativeException {
		Integer total = 0;
		try {
			StringBuilder sql = new StringBuilder(200);
			List<Object> params = new ArrayList<>();
			sql.append("SELECT COUNT(1) FROM t_scm_order_detail WHERE dealer_order_id = ?");
			params.add(dealerOrderId);
			total = this.getSupportJdbcTemplate().jdbcTemplate().queryForObject(sql.toString(), params.toArray(), Integer.class);
		} catch (Exception e) {
			LOGGER.error("---查询订单配送详情总数出错"+e.getMessage(),e);
			throw new NegativeException(500, "查询订单配送详情总数出错");
		}
		return total;
	}
	/**
	 * 查询订单配送详情信息
	 * @param dealerOrderId
	 * @return
	 * @throws NegativeException 
	 */
	public List<OrderExpressDetailBean> getOrderExpressDetail(String dealerOrderId) throws NegativeException {
		List<OrderExpressDetailBean> resultList = null;
		try {
			String sql = "SELECT * FROM t_scm_order_detail WHERE dealer_order_id = ? ";
			List<Object> params = new ArrayList<>();
			params.add(dealerOrderId);
			resultList = this.getSupportJdbcTemplate().queryForBeanList(sql, OrderExpressDetailBean.class,params.toArray());
		}catch (Exception e) {
			LOGGER.error("---查询订单配送详情出错"+e.getMessage(),e);
			throw new NegativeException(500, "查询订单配送详情出错");
		}
		return resultList;
	}
	/**
	 * 查询物流信息（订阅模式）
	 * @param com
	 * @param nu
	 * @return
	 * @throws NegativeException 
	 */
	public ExpressInfoBean queryExpress(String com, String nu) throws NegativeException {
		String sql = "SELECT res_data,ship_goods_time,ship_type,com FROM t_scm_express_platform WHERE com=? AND nu=? ";
		ExpressInfoBean result = null;
		try {
			List<Object> params = new ArrayList<>();
			params.add(com);
			params.add(nu);
			result = this.getSupportJdbcTemplate().queryForBean(sql.toString(), ExpressInfoBean.class,params.toArray());
		} catch (Exception e) {
			LOGGER.error("---查询物流信息"+e.getMessage(),e);
			throw new NegativeException(MCode.V_400, "查询物流信息出错");
		}
		return result;
	}
	
	
	/**
	 * 订货号拉取主订单，收货人信息
	 * @param dealerOrderId
	 * @return
	 * @throws NegativeException
	 */
	public ShipExpressBean queryOrderIdByDealerOrderId(String dealerOrderId) throws NegativeException {
		ShipExpressBean expressBean = null;
		try {
			String sql = "SELECT dealer_id, order_id, rev_person, rev_phone FROM t_scm_order_dealer WHERE dealer_order_id = ?";
			expressBean = this.getSupportJdbcTemplate().queryForBean(sql, ShipExpressBean.class,dealerOrderId);
			return expressBean;
		} catch (Exception e) {
			throw new NegativeException(MCode.V_400,"发货信息");
		}
		
	}
}

