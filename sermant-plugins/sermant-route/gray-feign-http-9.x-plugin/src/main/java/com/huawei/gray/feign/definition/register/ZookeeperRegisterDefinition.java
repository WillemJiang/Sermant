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

package com.huawei.gray.feign.definition.register;

import com.huawei.sermant.core.agent.definition.EnhanceDefinition;
import com.huawei.sermant.core.agent.definition.MethodInterceptPoint;
import com.huawei.sermant.core.agent.matcher.ClassMatcher;
import com.huawei.sermant.core.agent.matcher.ClassMatchers;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * 拦截springcloud zookeeper注册中心方法，获取当前服务名
 *
 * @author lilai
 * @since 2021-11-03
 */
public class ZookeeperRegisterDefinition implements EnhanceDefinition {

    /**
     * Intercept class.
     */
    private static final String INTERCEPT_CLASS = "com.huawei.gray.feign.interceptor.ZookeeperRegisterInterceptor";

    @Override
    public ClassMatcher enhanceClass() {
        return ClassMatchers.named("org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistry");
    }

    @Override
    public MethodInterceptPoint[] getMethodInterceptPoints() {
        return new MethodInterceptPoint[]{
                MethodInterceptPoint.newInstMethodInterceptPoint(
                        INTERCEPT_CLASS, ElementMatchers.<MethodDescription>named("register")
                )
        };
    }
}

