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

package com.huawei.flowcontrol.adapte.cse.datasource;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.huawei.flowcontrol.adapte.cse.resolver.BulkheadRuleResolver;
import com.huawei.flowcontrol.adapte.cse.rule.BulkheadRule;
import com.huawei.flowcontrol.adapte.cse.rule.isolate.IsolateThreadRule;
import com.huawei.flowcontrol.adapte.cse.rule.isolate.IsolateThreadRuleManager;
import com.huawei.sermant.core.common.LoggerFactory;
import com.huawei.flowcontrol.adapte.cse.ResolverManager;
import com.huawei.flowcontrol.adapte.cse.RuleSyncer;
import com.huawei.flowcontrol.adapte.cse.resolver.CircuitBreakerRuleResolver;
import com.huawei.flowcontrol.adapte.cse.resolver.RateLimitingRuleResolver;
import com.huawei.flowcontrol.adapte.cse.resolver.listener.ConfigUpdateListener;
import com.huawei.flowcontrol.adapte.cse.rule.CircuitBreakerRule;
import com.huawei.flowcontrol.adapte.cse.rule.RateLimitingRule;
import com.huawei.flowcontrol.core.datasource.DataSourceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 数据源管理
 *
 * @author zhouss
 * @since 2021-11-24
 */
public class CseDataSourceManager implements DataSourceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private RuleSyncer ruleSyncer;

    @Override
    public void start() {
        registerFlowRuleDataSource();
        registerBreakerRuleDataSource();
        registerIsolateThreadRuleDataSource();
        startCseConfigSyncer();
    }

    @Override
    public void stop() {
        ruleSyncer.stop();
    }

    private void startCseConfigSyncer() {
        ruleSyncer = new RuleSyncer();
        ruleSyncer.start();
    }

    /**
     * 隔离仓规则
     */
    private void registerIsolateThreadRuleDataSource() {
        try {
            final CseKieDataSource<BulkheadRule, IsolateThreadRule> isolateDataSource =
                    new CseKieDataSource<BulkheadRule, IsolateThreadRule>(new Converter<List<BulkheadRule>, List<IsolateThreadRule>>() {
                        @Override
                        public List<IsolateThreadRule> convert(List<BulkheadRule> source) {
                            if (source == null) {
                                return Collections.emptyList();
                            }
                            final ArrayList<IsolateThreadRule> isolateThreadRules = new ArrayList<IsolateThreadRule>();
                            for (BulkheadRule bulkheadRule : source) {
                                isolateThreadRules.addAll(bulkheadRule.convertToSentinelRule());
                            }
                            return isolateThreadRules;
                        }
                    });
            IsolateThreadRuleManager.register2Property(isolateDataSource.getProperty());
            final ConfigUpdateListener<BulkheadRule> configUpdateListener = new ConfigUpdateListener<BulkheadRule>() {
                @Override
                public void notify(Map<String, BulkheadRule> rules) {
                    isolateDataSource.updateConfig(new ArrayList<BulkheadRule>(rules.values()));
                }
            };
            ResolverManager.INSTANCE.registerListener(BulkheadRuleResolver.CONFIG_KEY, configUpdateListener);
        } catch (Exception ex) {
            LOGGER.warning(String.format(Locale.ENGLISH, "Init cse kie isolate rule failed! %s", ex.getMessage()));
        }
    }

    /**
     * 注册熔断规则
     */
    private void registerBreakerRuleDataSource() {
        try {
            final CseKieDataSource<CircuitBreakerRule, DegradeRule> degradeRuleDataSource =
                    new CseKieDataSource<CircuitBreakerRule, DegradeRule>(new Converter<List<CircuitBreakerRule>, List<DegradeRule>>() {
                        @Override
                        public List<DegradeRule> convert(List<CircuitBreakerRule> source) {
                            if (source == null) {
                                return Collections.emptyList();
                            }
                            final List<DegradeRule> degradeRules = new ArrayList<DegradeRule>();
                            for (CircuitBreakerRule rule : source) {
                                degradeRules.addAll(rule.convertToSentinelRule());
                            }
                            return degradeRules;
                        }
                    });
            DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
            final ConfigUpdateListener<CircuitBreakerRule> configUpdateListener = new ConfigUpdateListener<CircuitBreakerRule>() {
                @Override
                public void notify(Map<String, CircuitBreakerRule> rules) {
                    degradeRuleDataSource.updateConfig(new ArrayList<CircuitBreakerRule>(rules.values()));
                }
            };
            ResolverManager.INSTANCE.registerListener(CircuitBreakerRuleResolver.CONFIG_KEY, configUpdateListener);
        } catch (Exception ex) {
            LOGGER.warning(String.format(Locale.ENGLISH, "Init cse kie degrade rule failed! %s", ex.getMessage()));
        }
    }

    /**
     * 注册流控规则
     */
    private void registerFlowRuleDataSource() {
        try {
            final CseKieDataSource<RateLimitingRule, FlowRule> flowRuleDataSource =
                    new CseKieDataSource<RateLimitingRule, FlowRule>(new Converter<List<RateLimitingRule>, List<FlowRule>>() {
                        @Override
                        public List<FlowRule> convert(List<RateLimitingRule> source) {
                            if (source == null) {
                                return Collections.emptyList();
                            }
                            final List<FlowRule> flowRules = new ArrayList<FlowRule>();
                            for (RateLimitingRule rule : source) {
                                flowRules.addAll(rule.convertToSentinelRule());
                            }
                            return flowRules;
                        }
                    });
            FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
            final ConfigUpdateListener<RateLimitingRule> configUpdateListener = new ConfigUpdateListener<RateLimitingRule>() {

                @Override
                public void notify(Map<String, RateLimitingRule> rules) {
                    flowRuleDataSource.updateConfig(new ArrayList<RateLimitingRule>(rules.values()));
                }
            };
            ResolverManager.INSTANCE.registerListener(RateLimitingRuleResolver.CONFIG_KEY, configUpdateListener);
        } catch (Exception ex) {
            LOGGER.warning(String.format(Locale.ENGLISH, "Init cse kie flow rule failed! %s", ex.getMessage()));
        }
    }
}
