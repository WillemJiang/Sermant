/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.flowcontrol.adapte.cse.interceptors;

import com.huawei.flowcontrol.service.ServiceCombServiceMetaService;
import com.huawei.sermant.core.agent.common.BeforeResult;
import com.huawei.sermant.core.agent.interceptor.InstanceMethodInterceptor;
import com.huawei.sermant.core.service.ServiceManager;

import java.lang.reflect.Method;

/**
 * 拦截获取当前服务的版本
 *
 * @author zhouss
 * @since 2021-11-16
 */
public class MetricServiceMetaInterceptor implements InstanceMethodInterceptor {
    private ServiceCombServiceMetaService serviceCombServiceMetaService;

    @Override
    public void before(Object obj, Method method, Object[] arguments, BeforeResult beforeResult) {
        serviceCombServiceMetaService = ServiceManager.getService(ServiceCombServiceMetaService.class);
    }

    @Override
    public Object after(Object obj, Method method, Object[] arguments, Object result) {
        serviceCombServiceMetaService.after(obj, method, arguments, result);
        return result;
    }

    @Override
    public void onThrow(Object obj, Method method, Object[] arguments, Throwable t) {

    }
}
