package cn.m2c.scm.port.adapter.messaging.rabbitmq.goods;

import cn.m2c.common.JsonUtils;
import cn.m2c.ddd.common.application.configuration.RabbitmqConfiguration;
import cn.m2c.ddd.common.event.ConsumedEventStore;
import cn.m2c.ddd.common.port.adapter.messaging.rabbitmq.ExchangeListener;
import cn.m2c.scm.application.goods.GoodsApplication;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;

import java.util.Map;

/**
 * 订单支付完成，减商品销量、加实际库存
 */
public class GoodsSkuUpdateByOrderCancelListener extends ExchangeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsSkuUpdateByOrderCancelListener.class);

    @Autowired
    GoodsApplication goodsApplication;

    public GoodsSkuUpdateByOrderCancelListener(RabbitmqConfiguration rabbitmqConfiguration, HibernateTransactionManager hibernateTransactionManager, ConsumedEventStore consumedEventStore) {
        super(rabbitmqConfiguration, hibernateTransactionManager, consumedEventStore);
    }

    @Override
    protected String packageName() {
        return this.getClass().getPackage().getName();
    }

    @Override
    protected void filteredDispatch(String aType, String aTextMessage) throws Exception {
        LOGGER.info("GoodsSkuUpdateByOrderCancelListener start...");
        LOGGER.info("GoodsSkuUpdateByOrderCancelListener aTextMessage =>" + aTextMessage);
        Map map = JsonUtils.toMap4Obj(aTextMessage);
        Map eventMap = JsonUtils.toMap4Obj(JSONObject.toJSONString(map.get("event")));
        Map obj = JsonUtils.toMap4Obj(JSONObject.toJSONString(eventMap.get("sales")));
        goodsApplication.GoodsSkuUpdateByOrderCancel(obj);
        LOGGER.info("GoodsSkuUpdateByOrderCancelListener end...");
    }

    @Override
    protected String[] listensTo() {
        return new String[]{"cn.m2c.scm.domain.model.order.event.OrderCancelEvent"};
    }
}
