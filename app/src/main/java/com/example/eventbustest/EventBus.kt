package com.example.eventbustest

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.lang.RuntimeException
import java.lang.reflect.Method


object EventBus {
    var map = mutableMapOf<Any, ArrayList<SubscribeMethod>>()
    var handler = Handler(Looper.getMainLooper())

    //第一步，提供注册接口给招聘方，保存招聘方的岗位要求
    fun register(any: Any) {
        map[any] ?: putSubscribeMethod(any)

    }

    fun unRegister(any: Any) {
        val list = map[any]
        list!!.clear()
        map.remove(any)
    }

    fun putSubscribeMethod(any: Any?) {
        var subscibeList = mutableListOf<SubscribeMethod>() as ArrayList<SubscribeMethod>
        var clazz: Class<Any>? = any!!::class.java as Class<Any>?

        while (clazz != null) {
            val name = clazz.name

            //如果是系统类我们直接跳出
            if (name.startsWith(".java") || name.startsWith(".javax") || name.startsWith(".android")) {
                break
            }

            val methods = clazz.declaredMethods
            methods.forEach {
                //其实这里就是取出在Eventbus注册了的所有面试岗位，通过注解名来获取
                val annotation = it.getAnnotation(Subscribe::class.java) ?: return@forEach
                val types = it.parameterTypes

                //一个岗位只能发一份简历
                if (types.size > 1) {
                    throw RuntimeException("不能同时注册两个消息")
                }

                //上班地点，线程切换，见注释
                val mode = annotation.threadMode
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    if (mode == ThreadMode.MAIN) {
                        Log.i("swl", "注册线程" + Thread.currentThread().name)
                        putSubscribeMethod(it, types, mode, subscibeList, any)
                    } else {
                        Thread(Runnable {
                            Log.i("swl", "注册线程" + Thread.currentThread().name)
                            putSubscribeMethod(it, types, mode, subscibeList, any)
                        }).start()
                    }
                } else {
                    if (mode == ThreadMode.BACKGROUND) {
                        Log.i("swl", "注册线程" + Thread.currentThread().name)
                        putSubscribeMethod(it, types, mode, subscibeList, any)
                    } else {
                        handler.post(Runnable { putSubscribeMethod(it, types, mode, subscibeList, any) })
                    }
                }


            }

            clazz = clazz.superclass as Class<Any>?

        }

    }


    /**
    保存招聘方的招聘要求信息
     */
    private fun putSubscribeMethod(it: Method, types: Array<Class<*>>, mode: ThreadMode, subscibeList: ArrayList<SubscribeMethod>, any: Any) {
        val subscribeMethod = SubscribeMethod(it, types[0], mode)
        subscibeList.add(subscribeMethod)
        map.put(any, subscibeList)
    }

    /**
    第二部，提供post接口给应聘方发送简历，并遍历查询招聘方的所有岗位，直到找到与应聘方简历符合的岗位后，通知面试
     */
    fun post(any: Any) {
        for ((k, v) in map) {
            v.forEach {
                if (it.eventType == any.javaClass) {
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        if (it.threadMode == ThreadMode.MAIN) {
                            Log.i("swl", "发送线程" + Thread.currentThread().name)
                            /**
                            找到符合双方要求的岗位，通过反射调用，通知面试
                             */
                            it.method.invoke(k, any)
                        } else {
                            Thread(Runnable {
                                Log.i("swl", "发送线程" + Thread.currentThread().name)
                                it.method.invoke(k, any)
                            }).start()
                        }
                    } else {
                        if (it.threadMode == ThreadMode.BACKGROUND) {
                            Log.i("swl", "发送线程" + Thread.currentThread().name)

                        } else {
                            handler.post(Runnable { it.method.invoke(k, any) })
                        }
                    }
                    return
                }
            }
        }
    }

}
