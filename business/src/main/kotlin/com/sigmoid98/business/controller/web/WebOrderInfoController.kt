package com.sigmoid98.business.controller.web

import com.sigmoid98.business.resp.CommonResp
import com.sigmoid98.business.service.AfterPayService
import com.sigmoid98.business.service.OrderInfoService
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/web/order-info")
class WebOrderInfoController(
    // @Resource private val orderInfoService: OrderInfoService,
    @Resource private val afterPayService: AfterPayService,
) {

    @GetMapping("/query-order-status/{orderNo}")
    fun queryOrderStatus(@PathVariable orderNo: String): CommonResp<String> {
        val orderStatus = afterPayService.reconcilePaymentStatus(orderNo)
        return CommonResp(content = orderStatus)
    }
}