package com.example.eventbustest

import java.lang.reflect.Method

/**
 *  method:就表示面试岗位
 *  eventType:表示简历
 *  threadMode：指定线程（上班地点，子公司还是母公司），见注释
 */
data class SubscribeMethod(val method: Method, val eventType: Class<*>, val threadMode: ThreadMode) {}