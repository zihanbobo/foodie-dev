package com.imooc.service.center;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.Orders;
import com.imooc.pojo.vo.MyOrdersVO;

/**
 * @Author: 张启航
 * @Date: 2020/12/4 16:10
 * @Description:
 */
public interface MyOrdersService {

    /**
     * 查询我的订单列表
     *
     * @param userId
     * @param orderStatus
     * @param page
     * @param pageSize
     * @return
     */
    public Page<MyOrdersVO> queryMyOrders(String userId,
                                              Integer orderStatus,
                                              Integer page,
                                              Integer pageSize);

    /**
     * 订单状态 --> 商家发货
     * @param orderId
     */
    public void updateDeliverOrderStatus(String orderId);

    /**
     * 查询我的订单
     *
     * @param userId
     * @param orderId
     * @return
     */
    public Orders queryMyOrder(String userId, String orderId);

    /**
     * 更新订单状态 —> 确认收货
     *
     * @return
     */
    public boolean updateReceiveOrderStatus(String orderId);

    /**
     * 删除订单（逻辑删除）
     * @param userId
     * @param orderId
     * @return
     */
    public boolean deleteOrder(String userId, String orderId);
}
