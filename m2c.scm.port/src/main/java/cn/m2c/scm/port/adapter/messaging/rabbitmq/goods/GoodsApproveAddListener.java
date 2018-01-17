package cn.m2c.scm.port.adapter.messaging.rabbitmq.goods;

import cn.m2c.common.JsonUtils;
import cn.m2c.ddd.common.application.configuration.RabbitmqConfiguration;
import cn.m2c.ddd.common.event.ConsumedEventStore;
import cn.m2c.ddd.common.notification.NotificationReader;
import cn.m2c.ddd.common.port.adapter.messaging.rabbitmq.ExchangeListener;
import cn.m2c.scm.application.goods.GoodsApproveApplication;
import cn.m2c.scm.application.goods.command.GoodsApproveCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;

import java.util.List;

/**
 * 商家管理平台商品审核
 */
public class GoodsApproveAddListener extends ExchangeListener {
    public GoodsApproveAddListener(RabbitmqConfiguration rabbitmqConfiguration, HibernateTransactionManager hibernateTransactionManager, ConsumedEventStore consumedEventStore) {
        super(rabbitmqConfiguration, hibernateTransactionManager, consumedEventStore);
    }

    @Autowired
    GoodsApproveApplication goodsApproveApplication;

    @Override
    protected String packageName() {
        return this.getClass().getPackage().getName();
    }

    @Override
    protected void filteredDispatch(String aType, String aTextMessage) throws Exception {
        NotificationReader reader = new NotificationReader(aTextMessage);
        String goodsId = reader.eventStringValue("goodsId");
        String dealerId = reader.eventStringValue("dealerId");
        String dealerName = reader.eventStringValue("dealerName");
        String goodsName = reader.eventStringValue("goodsName");
        String goodsSubTitle = reader.eventStringValue("goodsSubTitle");
        String goodsClassifyId = reader.eventStringValue("goodsClassifyId");
        String goodsBrandId = reader.eventStringValue("goodsBrandId");
        String goodsBrandName = reader.eventStringValue("goodsBrandName");
        String goodsUnitId = reader.eventStringValue("goodsUnitId");
        Integer goodsMinQuantity = reader.eventIntegerValue("goodsMinQuantity");
        String goodsPostageId = reader.eventStringValue("goodsPostageId");
        String goodsBarCode = reader.eventStringValue("goodsBarCode");
        String goodsKeyWord = reader.eventStringValue("goodsKeyWord");
        List goodsKeyWords = JsonUtils.toList(goodsKeyWord, String.class);

        String goodsGuarantee = reader.eventStringValue("goodsGuarantee");
        List goodsGuarantees = JsonUtils.toList(goodsGuarantee, String.class);

        String goodsMainImages = reader.eventStringValue("goodsMainImages");
        List goodsMainImageList = JsonUtils.toList(goodsMainImages, String.class);
        String goodsMainVideo = reader.eventStringValue("goodsMainVideo");
        String goodsDesc = reader.eventStringValue("goodsDesc");
        String goodsSpecifications = reader.eventStringValue("goodsSpecifications");
        String goodsSkuApproves = reader.eventStringValue("goodsSkuApproves");
        Integer skuFlag = reader.eventIntegerValue("skuFlag");

        GoodsApproveCommand command = new GoodsApproveCommand(goodsId, dealerId, dealerName, goodsName, goodsSubTitle,
                goodsClassifyId, goodsBrandId, goodsBrandName, goodsUnitId, goodsMinQuantity,
                goodsPostageId, goodsBarCode, goodsKeyWords, goodsGuarantees,
                goodsMainImageList, goodsMainVideo, goodsDesc, goodsSpecifications, goodsSkuApproves, skuFlag);

        
        goodsApproveApplication.addGoodsApproveForModifyGoods(command);
    }

    @Override
    protected String[] listensTo() {
        return new String[]{"cn.m2c.scm.domain.model.goods.event.GoodsApproveAddEvent"};
    }
}
