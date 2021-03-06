package cn.m2c.scm.port.adapter.restful.web.dealer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.m2c.common.MCode;
import cn.m2c.common.MPager;
import cn.m2c.common.MResult;
import cn.m2c.ddd.common.auth.RequirePermissions;
import cn.m2c.scm.application.dealer.DealerApplication;
import cn.m2c.scm.application.dealer.command.DealerAddOrUpdateCommand;
import cn.m2c.scm.application.dealer.data.bean.DealerBean;
import cn.m2c.scm.application.dealer.data.representation.DealerDetailRepresentation;
import cn.m2c.scm.application.dealer.data.representation.DealerNameListRepresentation;
import cn.m2c.scm.application.dealer.data.representation.DealerRepresentation;
import cn.m2c.scm.application.dealer.data.representation.DealerShopRepresentation;
import cn.m2c.scm.application.dealer.query.DealerQuery;
import cn.m2c.scm.application.dealerclassify.query.DealerClassifyQuery;
import cn.m2c.scm.application.utils.Utils;
import cn.m2c.scm.domain.IDGenerator;
import cn.m2c.scm.domain.NegativeException;

@RestController
@RequestMapping("/dealer/sys")
public class DealerAgent {
	private final static Logger log = LoggerFactory
			.getLogger(DealerAgent.class);

	@Autowired
	DealerApplication application;

	@Autowired
	DealerQuery dealerQuery;

	@Autowired
	DealerClassifyQuery dealerClassifyQuery;

	@Autowired
	private HttpServletRequest request;

	/**
	 * 获取主键id uuid
	 * 
	 * @return
	 */
	@RequestMapping(value = "/id", method = RequestMethod.GET)
	public ResponseEntity<MResult> getBrandId() {
		MResult result = new MResult(MCode.V_1);
		try {
			String dealerId = IDGenerator.get(IDGenerator.DEALER_PREFIX_TITLE);
			result.setContent(dealerId);
			result.setStatus(MCode.V_200);
		} catch (Exception e) {
			log.error("获取dealerId异常", e);
			result = new MResult(MCode.V_400, e.getMessage());
		}
		return new ResponseEntity<MResult>(result, HttpStatus.OK);
	}

	/**
	 * 新增经销商
	 * 
	 * @param userId
	 * @param dealerName
	 * @param dealerClassify
	 * @param cooperationMode
	 * @param startSignDate
	 * @param endSignDate
	 * @param dealerProvince
	 * @param dealerCity
	 * @param dealerarea
	 * @param dealerPcode
	 * @param dealerCcode
	 * @param dealerAcode
	 * @param dealerDetailAddress
	 * @param countMode
	 * @param deposit
	 * @param isPayDeposit
	 * @param managerName
	 * @param managerPhone
	 * @param managerqq
	 * @param managerWechat
	 * @param managerEmail
	 * @param managerDepartment
	 * @param sellerId
	 * @return
	 */
	@RequestMapping(value = "/mng", method = RequestMethod.POST)
	@RequirePermissions(value = { "scm:dealer:add" })
	public ResponseEntity<MResult> add(
			@RequestParam(value = "dealerId", required = true) String dealerId,
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "userName", required = true) String userName,
			@RequestParam(value = "userPhone", required = true) String userPhone,
			@RequestParam(value = "dealerName", required = true) String dealerName,
			@RequestParam(value = "dealerClassify", required = true) String dealerClassify,
			@RequestParam(value = "cooperationMode", required = true) Integer cooperationMode,
			@RequestParam(value = "startSignDate", required = true) String startSignDate,
			@RequestParam(value = "endSignDate", required = true) String endSignDate,
			@RequestParam(value = "dealerProvince", defaultValue = "") String dealerProvince,
			@RequestParam(value = "dealerCity", defaultValue = "") String dealerCity,
			@RequestParam(value = "dealerArea", defaultValue = "") String dealerArea,
			@RequestParam(value = "dealerPcode", defaultValue = "") String dealerPcode,
			@RequestParam(value = "dealerCcode", defaultValue = "") String dealerCcode,
			@RequestParam(value = "dealerAcode", defaultValue = "") String dealerAcode,
			@RequestParam(value = "dealerDetailAddress", required = false, defaultValue = "") String dealerDetailAddress,
			@RequestParam(value = "countMode", required = false) Integer countMode,
			@RequestParam(value = "deposit", required = false) String deposit,
			@RequestParam(value = "isPayDeposit", required = true, defaultValue = "0") Integer isPayDeposit,
			@RequestParam(value = "managerName", required = false) String managerName,
			@RequestParam(value = "managerPhone", required = false) String managerPhone,
			@RequestParam(value = "managerqq", required = false) String managerqq,
			@RequestParam(value = "managerWechat", required = false) String managerWechat,
			@RequestParam(value = "managerEmail", required = false) String managerEmail,
			@RequestParam(value = "managerDepartment", required = false) String managerDepartment,
			@RequestParam(value = "sellerId", required = true) String sellerId,
			@RequestParam(value = "sellerName", required = true) String sellerName,
			@RequestParam(value = "sellerPhone", required = true) String sellerPhone) {
		MResult result = new MResult(MCode.V_1);
		try {
			DealerAddOrUpdateCommand command = new DealerAddOrUpdateCommand(
					dealerId, userId, userName, userPhone, dealerName,
					dealerClassify, cooperationMode, startSignDate,
					endSignDate, dealerProvince, dealerCity, dealerArea,
					dealerPcode, dealerCcode, dealerAcode, dealerDetailAddress,
					countMode, deposit, isPayDeposit, managerName,
					managerPhone, managerqq, managerWechat, managerEmail,
					managerDepartment, sellerId, sellerName, sellerPhone);
			application.addDealer(command);
			result.setStatus(MCode.V_200);
		} catch (IllegalArgumentException e) {
			log.error("添加经销商出错", e);
			result = new MResult(MCode.V_1, e.getMessage());
		} catch (NegativeException ne) {
			log.error("添加经销商出错", ne);
			result = new MResult(ne.getStatus(), ne.getMessage());
		} catch (Exception e) {
			log.error("添加经销商出错" + e.getMessage(), e);
			result = new MResult(MCode.V_400, "服务器开小差了");
		}
		return new ResponseEntity<MResult>(result, HttpStatus.OK);
	}

	/**
	 * 修改经销商
	 * 
	 * @param dealerId
	 * @param userId
	 * @param dealerName
	 * @param dealerClassify
	 * @param cooperationMode
	 * @param startSignDate
	 * @param endSignDate
	 * @param dealerProvince
	 * @param dealerCity
	 * @param dealerarea
	 * @param dealerPcode
	 * @param dealerCcode
	 * @param dealerAcode
	 * @param dealerDetailAddress
	 * @param countMode
	 * @param deposit
	 * @param isPayDeposit
	 * @param managerName
	 * @param managerPhone
	 * @param managerqq
	 * @param managerWechat
	 * @param managerEmail
	 * @param managerDepartment
	 * @param sellerId
	 * @return
	 */
	@RequestMapping(value = "/mng", method = RequestMethod.PUT)
	@RequirePermissions(value = { "scm:dealer:update" })
	public ResponseEntity<MResult> update(
			@RequestParam(value = "dealerId", required = true) String dealerId,
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "userName", required = true) String userName,
			@RequestParam(value = "userPhone", required = true) String userPhone,
			@RequestParam(value = "dealerName", required = true) String dealerName,
			@RequestParam(value = "dealerClassify", required = true) String dealerClassify,
			@RequestParam(value = "cooperationMode", required = true) Integer cooperationMode,
			@RequestParam(value = "startSignDate", required = true) String startSignDate,
			@RequestParam(value = "endSignDate", required = true) String endSignDate,
			@RequestParam(value = "dealerProvince", defaultValue = "") String dealerProvince,
			@RequestParam(value = "dealerCity", defaultValue = "") String dealerCity,
			@RequestParam(value = "dealerArea", defaultValue = "") String dealerArea,
			@RequestParam(value = "dealerPcode", defaultValue = "") String dealerPcode,
			@RequestParam(value = "dealerCcode", defaultValue = "") String dealerCcode,
			@RequestParam(value = "dealerAcode", defaultValue = "") String dealerAcode,
			@RequestParam(value = "dealerDetailAddress", required = false, defaultValue = "") String dealerDetailAddress,
			@RequestParam(value = "countMode", required = true) Integer countMode,
			@RequestParam(value = "deposit", required = false) String deposit,
			@RequestParam(value = "isPayDeposit", required = true) Integer isPayDeposit,
			@RequestParam(value = "managerName", required = false) String managerName,
			@RequestParam(value = "managerPhone", required = false) String managerPhone,
			@RequestParam(value = "managerqq", required = false) String managerqq,
			@RequestParam(value = "managerWechat", required = false) String managerWechat,
			@RequestParam(value = "managerEmail", required = false) String managerEmail,
			@RequestParam(value = "managerDepartment", required = false) String managerDepartment,
			@RequestParam(value = "sellerId", required = true) String sellerId,
			@RequestParam(value = "sellerName", required = true) String sellerName,
			@RequestParam(value = "sellerPhone", required = true) String sellerPhone) {
		MResult result = new MResult(MCode.V_1);
		try {
			DealerAddOrUpdateCommand command = new DealerAddOrUpdateCommand(
					dealerId, userId, userName, userPhone, dealerName,
					dealerClassify, cooperationMode, startSignDate,
					endSignDate, dealerProvince, dealerCity, dealerArea,
					dealerPcode, dealerCcode, dealerAcode, dealerDetailAddress,
					countMode, deposit, isPayDeposit, managerName,
					managerPhone, managerqq, managerWechat, managerEmail,
					managerDepartment, sellerId, sellerName, sellerPhone);
			String _attach = request.getHeader("attach");
			application.updateDealer(command, _attach);
			result.setStatus(MCode.V_200);
		} catch (NumberFormatException na) {
			log.error("输入押金参数不合法", na);
			result = new MResult(MCode.V_1, "输入押金参数不合法");
		} catch (IllegalArgumentException e) {
			log.error("修改经销商出错", e);
			result = new MResult(MCode.V_1, e.getMessage());
		} catch (NegativeException ne) {
			log.error("添加经销商出错", ne);
			result = new MResult(MCode.V_1, ne.getMessage());
		} catch (Exception e) {
			log.error("修改经销商出错" + e.getMessage(), e);
			result = new MResult(MCode.V_400, "服务器开小差了");
		}
		return new ResponseEntity<MResult>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<MPager> list(
			@RequestParam(value = "dealerClassify", required = false) String dealerClassify,
			@RequestParam(value = "cooperationMode", required = false) Integer cooperationMode,
			@RequestParam(value = "countMode", required = false) Integer countMode,
			@RequestParam(value = "isPayDeposit", required = false) Integer isPayDeposit,
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
			@RequestParam(value = "rows", required = false, defaultValue = "10") Integer rows) {
		MPager result = new MPager(MCode.V_1);
		try {
			List<DealerBean> dealerList = dealerQuery.getDealerList(
					dealerClassify, cooperationMode, countMode, isPayDeposit,
					filter, startTime, endTime, pageNum, rows);
			Integer count = dealerQuery.getDealerCount(dealerClassify,
					cooperationMode, countMode, isPayDeposit, filter,
					startTime, endTime, pageNum, rows);
			if (dealerList != null && dealerList.size() > 0) {
				List<DealerRepresentation> list = new ArrayList<DealerRepresentation>();
				for (DealerBean model : dealerList) {
					list.add(new DealerRepresentation(model));
				}
				result.setContent(list);
			}
			result.setPager(count, pageNum, rows);
			result.setStatus(MCode.V_200);
		} catch (Exception e) {
			log.error("修改经销商出错" + e.getMessage(), e);
			result = new MPager(MCode.V_400, "服务器开小差了");
		}
		return new ResponseEntity<MPager>(result, HttpStatus.OK);
	}

	/**
	 * 查询供应商列表
	 */
	@RequestMapping(value = "/dealers", method = RequestMethod.GET)
	public ResponseEntity<MResult> queryDealerList(
			@RequestParam(value = "dealerIds", required = true) String dealerIds) {
		MResult result = new MResult(MCode.V_1);
		try {
			List<DealerShopRepresentation> list = new ArrayList<DealerShopRepresentation>();
			List<DealerBean> dealers = dealerQuery.getDealers(dealerIds);
			for (DealerBean model : dealers) {
				list.add(new DealerShopRepresentation(model));
			}
			result.setContent(list);
			result.setStatus(MCode.V_200);
		} catch (Exception e) {
			log.error("经销商列表出错", e);
			result = new MPager(MCode.V_400, "服务器开小差了，请稍后再试");
		}
		return new ResponseEntity<MResult>(result, HttpStatus.OK);
	}

	/**
	 * 根据经销商id查询经销商信息
	 * @param dealerId
	 * @return
	 */
	@RequestMapping(value = { "/{dealerId}", "/web/{dealerId}" }, method = RequestMethod.GET)
	public ResponseEntity<MResult> queryDealer(
			@PathVariable(value = "dealerId", required = true) String dealerId) {
		MResult result = new MResult(MCode.V_1);
		DealerDetailRepresentation representation = null;
		try {
			DealerBean dealer = dealerQuery.getDealer(dealerId);
			if (dealer != null) {
				representation = new DealerDetailRepresentation(dealer);
			}
			result.setContent(representation);
			result.setStatus(MCode.V_200);
		} catch (Exception e) {
			log.error("查询经销商出错", e);
			result = new MPager(MCode.V_400, "服务器开小差了，请稍后再试");
		}
		return new ResponseEntity<MResult>(result, HttpStatus.OK);
	}

	/**
	 * 根据经销商名称获取经销商
	 * 
	 * @param dealerName
	 * @return
	 */
	@RequestMapping(value = "/getDealerName", method = RequestMethod.GET)
	public ResponseEntity<MResult> getDealerName(
			@RequestParam(value = "dealerName", required = true) String dealerName) {
		MResult result = new MResult(MCode.V_1);
		List<DealerNameListRepresentation> list = new ArrayList<DealerNameListRepresentation>();
		try {
			List<DealerBean> dealerList = dealerQuery
					.getDealerByName(dealerName);
			if (dealerList != null && dealerList.size() > 0) {
				for (DealerBean model : dealerList) {
					list.add(new DealerNameListRepresentation(model));
				}
			}
			result.setContent(list);
			result.setStatus(MCode.V_200);
		} catch (Exception e) {
			log.error("经销商名称列表出错", e);
			result = new MPager(MCode.V_400, "服务器开小差了，请稍后再试");
		}
		return new ResponseEntity<MResult>(result, HttpStatus.OK);
	}

	/**
	 * 根据多个商家id获取商家的状态返回List<Map>
	 */
	@RequestMapping(value = "/dealerStatus-out", method = RequestMethod.GET)
	public ResponseEntity<MResult> queryDealerStatus(
			@RequestParam(value = "dealerIds", required = true) String dealerIds) {
		MResult result = new MResult(MCode.V_1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> dealerStatus = dealerQuery
					.getDealerStatus(dealerIds);
			if (null != dealerStatus && dealerStatus.size() > 0) {
				for (Map<String, Object> map : dealerStatus) {
					String dealerId = (String) map.get("dealerId") == null ? ""
							: (String) map.get("dealerId");
					Integer dealerstatus = (Integer) map.get("dealerStatus") == null ? -1
							: (Integer) map.get("dealerStatus");
					resultMap.put(dealerId, dealerstatus);
				}
				result.setContent(resultMap);
				result.setStatus(MCode.V_200);
			}
		} catch (Exception e) {
			log.error("经销商状态列表出错", e);
			result = new MPager(MCode.V_400, "服务器开小差了，请稍后再试");
		}
		return new ResponseEntity<MResult>(result, HttpStatus.OK);
	}

	/**
	 * 获取平台的押金
	 */
	@RequestMapping(value = { "/dealerDeposit", "/web/dealerDeposit" }, method = RequestMethod.GET)
	public ResponseEntity<MResult> getDealerDeposit(
			@RequestParam(value = "dealerId", required = true) String dealerId) {
		MResult result = new MResult(MCode.V_1);
		try {
			Long dealerDesposit = dealerQuery.getDespositByDealerId(dealerId);
			result.setContent(dealData(dealerDesposit));
			result.setStatus(MCode.V_200);
		} catch (Exception e) {
			log.error("经销商状态列表出错", e);
			result = new MPager(MCode.V_400, "服务器开小差了，请稍后再试");
		}
		return new ResponseEntity<MResult>(result, HttpStatus.OK);
	}

	private String dealData(Long dealerDesposit) {
		BigDecimal d1 = new BigDecimal(dealerDesposit);
		BigDecimal d2 = new BigDecimal(Utils.DIVIDE);
		BigDecimal d3 = d1.divide(d2);
		return d3.toString();
	}

}
