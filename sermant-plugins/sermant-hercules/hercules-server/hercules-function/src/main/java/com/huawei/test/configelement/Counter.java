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

package com.huawei.test.configelement;

import com.huawei.test.configelement.config.CounterConfig;
import com.huawei.test.configelement.service.ExecuteTimesInfo;

/**
 * 功能描述：计数器接口
 *
 * @author zl
 * @since 2021-12-09
 */
public abstract class Counter extends ConfigElement<CounterConfig>{
	/**
	 * 获取下一个数字，如果指定了数字格式，则返回的是一个格式字符串，如果没有就是一个数字
	 *
	 * @return 表达式字符串
	 */
	public abstract String nextNumber();

	/**
	 * 是否还有下一个取值
	 *
	 * @return true：有， false：没有
	 */
	public abstract boolean hasNext();
}
