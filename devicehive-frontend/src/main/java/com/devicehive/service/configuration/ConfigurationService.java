package com.devicehive.service.configuration;

/*
 * #%L
 * DeviceHive Java Server Common business logic
 * %%
 * Copyright (C) 2016 DataArt
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.devicehive.configuration.Messages;
import com.devicehive.dao.ConfigurationDao;
import com.devicehive.vo.ConfigurationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Component
@Lazy(false)
public class ConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    private ConfigurationDao configurationDao;

    @Transactional
    public <T> ConfigurationVO save(@NotNull String name, T value) {
        //TODO check keys are same
        String str = value != null ? value.toString() : null;
        Optional<ConfigurationVO> existingOpt = findByName(name);

        if (existingOpt.isPresent()) {
            ConfigurationVO existing = existingOpt.get();
            existing.setValue(str);

            return configurationDao.merge(existing);
        }

        ConfigurationVO configuration = new ConfigurationVO();
        configuration.setName(name);
        configuration.setValue(str);
        configurationDao.persist(configuration);

        return configuration;
    }

    public Optional<ConfigurationVO> findByName(String name) {
        return configurationDao.getByName(name);
    }

    public String get(@NotNull String name) {
        return findByName(name)
                .map(ConfigurationVO::getValue)
                .orElseGet(() -> {
                    logger.warn(String.format(Messages.CONFIG_NOT_FOUND, name));
                    return null;
                });
    }

    public long getLong(@NotNull String name, long defaultValue) {
        String val = get(name);
        return val != null ? Long.parseLong(val) : defaultValue;
    }

    public int getInt(@NotNull String name, int defaultValue) {
        String val = get(name);
        return val != null ? Integer.parseInt(val) : defaultValue;
    }

    public boolean getBoolean(@NotNull String name, boolean defaultValue) {
        String val = get(name);
        return val != null ? Boolean.parseBoolean(val) : defaultValue;
    }

    @Transactional
    public <T> int delete(@NotNull String name) {
        int result = configurationDao.delete(name);
        logger.info("Deleted {} configuration entries by name {}", result, name);
        
        return result;
    }

}