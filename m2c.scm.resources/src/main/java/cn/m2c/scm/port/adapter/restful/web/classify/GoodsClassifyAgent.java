package cn.m2c.scm.port.adapter.restful.web.classify;

import cn.m2c.common.MCode;
import cn.m2c.common.MResult;
import cn.m2c.ddd.common.auth.RequirePermissions;
import cn.m2c.scm.application.classify.GoodsClassifyApplication;
import cn.m2c.scm.application.classify.command.GoodsClassifyAddCommand;
import cn.m2c.scm.application.classify.command.GoodsClassifyModifyCommand;
import cn.m2c.scm.application.classify.data.bean.GoodsClassifyBean;
import cn.m2c.scm.application.classify.data.representation.GoodsClassifyRepresentation;
import cn.m2c.scm.application.classify.query.GoodsClassifyQueryApplication;
import cn.m2c.scm.domain.NegativeException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品分类
 */
@RestController
public class GoodsClassifyAgent {
    private final static Logger LOGGER = LoggerFactory.getLogger(GoodsClassifyAgent.class);

    @Autowired
    GoodsClassifyApplication goodsClassifyApplication;
    @Autowired
    GoodsClassifyQueryApplication goodsClassifyQueryApplication;

    @Autowired
    private HttpServletRequest request;
    
    /**
     * 增加商品分类
     *
     * @param classifyName     分类名称(增加一、二、三级分类必传)
     * @param subClassifyNames 子分类名称list的json字符串,["短袖","裙子"]
     * @param parentClassifyId 子分类上级分类的id(增加一、二、三级分类必传，一级分类传-1)
     * @param level            层级，1：一级分类,2：二级分类,3：三级分类...
     * @return
     */
    @RequestMapping(value = "/goods/classify/mng", method = RequestMethod.POST)
    @RequirePermissions(value = {"scm:goodsClassify:add"})
    public ResponseEntity<MResult> addGoodsClassify(
            @RequestParam(value = "classifyName", required = false) String classifyName,
            @RequestParam(value = "subClassifyNames", required = false) String subClassifyNames,
            @RequestParam(value = "parentClassifyId", required = false) String parentClassifyId,
            @RequestParam(value = "level", required = false) Integer level) {
        MResult result = new MResult(MCode.V_1);
        try {
            GoodsClassifyAddCommand command = new GoodsClassifyAddCommand(classifyName, subClassifyNames, parentClassifyId, level);
            boolean isRateNull = goodsClassifyApplication.addGoodsClassify(command);
            result.setStatus(MCode.V_200);
            result.setContent(isRateNull);
        } catch (NegativeException ne) {
            LOGGER.error("addGoodsClassify NegativeException e:", ne);
            result = new MResult(ne.getStatus(), ne.getMessage());
        } catch (Exception e) {
            LOGGER.error("addGoodsClassify Exception e:", e);
            result = new MResult(MCode.V_400, "添加商品分类失败");
        }
        return new ResponseEntity<MResult>(result, HttpStatus.OK);
    }

    /**
     * 修改商品分类名称
     *
     * @param classifyName
     * @return
     */
    @RequestMapping(value = "/goods/classify/mng/{classifyId}/name", method = RequestMethod.PUT)
    @RequirePermissions(value = {"scm:goodsClassify:update"})
    public ResponseEntity<MResult> modifyGoodsClassifyName(
            @PathVariable("classifyId") String classifyId,
            @RequestParam(value = "classifyName", required = false) String classifyName) {
        MResult result = new MResult(MCode.V_1);
        try {
            GoodsClassifyModifyCommand command = new GoodsClassifyModifyCommand(classifyId, classifyName);
            String _attach = request.getHeader("attach");
            goodsClassifyApplication.modifyGoodsClassifyName(command, _attach);
            result.setStatus(MCode.V_200);
        } catch (NegativeException ne) {
            LOGGER.error("modifyGoodsClassifyName NegativeException e:", ne);
            result = new MResult(ne.getStatus(), ne.getMessage());
        } catch (Exception e) {
            LOGGER.error("modifyGoodsClassifyName Exception e:", e);
            result = new MResult(MCode.V_400, "修改商品分类名称失败");
        }
        return new ResponseEntity<MResult>(result, HttpStatus.OK);
    }

    /**
     * 修改商品分类费率
     *
     * @param serviceRate
     * @return
     */
    @RequestMapping(value = "/goods/classify/mng/{classifyId}/service/rate", method = RequestMethod.PUT)
    @RequirePermissions(value = {"scm:serviceRate:modify"})
    public ResponseEntity<MResult> modifyGoodsClassifyServiceRate(
            @PathVariable("classifyId") String classifyId,
            @RequestParam(value = "serviceRate", required = false) Float serviceRate) {
        MResult result = new MResult(MCode.V_1);
        try {
            GoodsClassifyModifyCommand command = new GoodsClassifyModifyCommand(classifyId, serviceRate);
            String _attach = request.getHeader("attach");
            goodsClassifyApplication.modifyGoodsClassifyServiceRate(command, _attach);
            result.setStatus(MCode.V_200);
        } catch (NegativeException ne) {
            LOGGER.error("modifyGoodsClassifyServiceRate NegativeException e:", ne);
            result = new MResult(ne.getStatus(), ne.getMessage());
        } catch (Exception e) {
            LOGGER.error("modifyGoodsClassifyServiceRate Exception e:", e);
            result = new MResult(MCode.V_400, "修改商品分类费率失败");
        }
        return new ResponseEntity<MResult>(result, HttpStatus.OK);
    }

    /**
     * 删除商品分类
     *
     * @param classifyId
     * @return
     */
    @RequestMapping(value = "/goods/classify/mng/{classifyId}", method = RequestMethod.DELETE)
    @RequirePermissions(value = {"scm:goodsClassify:delete"})
    public ResponseEntity<MResult> deleteGoodsClassify(
            @PathVariable("classifyId") String classifyId) {
        MResult result = new MResult(MCode.V_1);
        try {
        	String _attach = request.getHeader("attach");
            goodsClassifyApplication.deleteGoodsClassify(classifyId, _attach);
            result.setStatus(MCode.V_200);
        } catch (NegativeException ne) {
            LOGGER.error("deleteGoodsClassify NegativeException e:", ne);
            result = new MResult(ne.getStatus(), ne.getMessage());
        } catch (Exception e) {
            LOGGER.error("deleteGoodsClassify Exception e:", e);
            result = new MResult(MCode.V_400, "删除商品分类失败");
        }
        return new ResponseEntity<MResult>(result, HttpStatus.OK);
    }

    /**
     * 查询商品分类结构树
     *
     * @return
     */
    @RequestMapping(value = {"/web/goods/classify/tree", "/goods/classify/tree"}, method = RequestMethod.GET)
    public ResponseEntity<MResult> queryGoodsClassifyTree(
            @RequestParam(value = "parentClassifyId", required = false) String parentClassifyId) {
        MResult result = new MResult(MCode.V_1);
        try {
            List<Map> list = goodsClassifyQueryApplication.recursionQueryGoodsClassifyTree(parentClassifyId);
            result.setContent(list);
            result.setStatus(MCode.V_200);
        } catch (Exception e) {
            LOGGER.error("queryGoodsClassifyTree Exception e:", e);
            result = new MResult(MCode.V_400, "查询商品分类结构树失败");
        }
        return new ResponseEntity<MResult>(result, HttpStatus.OK);
    }

    /**
     * 查询商品分类费率是否为空
     *
     * @return
     */
    @RequestMapping(value = "/goods/classify/service/rate/is/null", method = RequestMethod.GET)
    public ResponseEntity<MResult> queryRateIsNull() {
        MResult result = new MResult(MCode.V_1);
        try {
            boolean isNull = goodsClassifyQueryApplication.rateIsNull();
            result.setContent(isNull);
            result.setStatus(MCode.V_200);
        } catch (Exception e) {
            LOGGER.error("queryRateIsNull Exception e:", e);
            result = new MResult(MCode.V_400, "查询商品分类费率是否为空失败");
        }
        return new ResponseEntity<MResult>(result, HttpStatus.OK);
    }

    /**
     * 查询商品分类详情
     *
     * @return
     */
    @RequestMapping(value = "/goods/classify/{classifyId}", method = RequestMethod.GET)
    public ResponseEntity<MResult> queryGoodsClassifyDetail(@PathVariable("classifyId") String classifyId) {
        MResult result = new MResult(MCode.V_1);
        try {
            GoodsClassifyBean bean = goodsClassifyQueryApplication.queryGoodsClassifiesById(classifyId);
            if (null != bean) {
                result.setContent(new GoodsClassifyRepresentation(bean));
            }
            result.setStatus(MCode.V_200);
        } catch (Exception e) {
            LOGGER.error("queryGoodsClassifyDetail Exception e:", e);
            result = new MResult(MCode.V_400, "查询商品分类详情失败");
        }
        return new ResponseEntity<MResult>(result, HttpStatus.OK);
    }

    /**
     * 根据层级查询分类
     *
     * @param level
     * @return
     */
    @RequestMapping(value = "/goods/classify/level", method = RequestMethod.GET)
    public ResponseEntity<MResult> queryGoodsClassifyByLevel(
            @RequestParam(value = "level", required = false) Integer level) {
        MResult result = new MResult(MCode.V_1);
        try {
            List<GoodsClassifyBean> beans = goodsClassifyQueryApplication.queryGoodsClassifiesByLevel(level);
            if (null != beans && beans.size() > 0) {
                List<GoodsClassifyRepresentation> representations = new ArrayList<>();
                for (GoodsClassifyBean bean : beans) {
                    representations.add(new GoodsClassifyRepresentation(bean));
                }
                result.setContent(representations);
            }
            result.setStatus(MCode.V_200);
        } catch (Exception e) {
            LOGGER.error("queryGoodsClassifyByLevel Exception e:", e);
            result = new MResult(MCode.V_400, "根据层级查询分类失败");
        }
        return new ResponseEntity<MResult>(result, HttpStatus.OK);
    }

    /**
     * 查询服务费率
     *
     * @param classifyId
     * @return
     */
    @RequestMapping(value = {"/web/goods/classify/service/rate","/goods/classify/service/rate"}, method = RequestMethod.GET)
    public ResponseEntity<MResult> queryServiceRateByClassifyId(
            @RequestParam(value = "classifyId", required = false) String classifyId) {
        MResult result = new MResult(MCode.V_1);
        try {
            Float serviceRate = goodsClassifyQueryApplication.queryServiceRateByClassifyId(classifyId);
            result.setContent(serviceRate);
            result.setStatus(MCode.V_200);
        } catch (Exception e) {
            LOGGER.error("queryServiceRateByClassifyId Exception e:", e);
            result = new MResult(MCode.V_400, "查询服务费率失败");
        }
        return new ResponseEntity<MResult>(result, HttpStatus.OK);
    }
}
