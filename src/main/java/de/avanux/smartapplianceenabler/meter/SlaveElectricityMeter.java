/*
 * Copyright (C) 2021 Axel Müller <axel.mueller@avanux.de>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package de.avanux.smartapplianceenabler.meter;

import de.avanux.smartapplianceenabler.appliance.ApplianceIdConsumer;
import de.avanux.smartapplianceenabler.configuration.ConfigurationException;
import de.avanux.smartapplianceenabler.configuration.Validateable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.time.LocalDateTime;
import java.util.Timer;

@XmlAccessorType(XmlAccessType.FIELD)
public class SlaveElectricityMeter implements ApplianceIdConsumer, Meter, Validateable {
    private transient Logger logger = LoggerFactory.getLogger(SlaveElectricityMeter.class);
    @XmlAttribute
    private String masterElectricityMeterApplianceId;
    private transient String applianceId;

    @Override
    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
    }

    public String getApplianceId() {
        return applianceId;
    }

    public String getMasterElectricityMeterApplianceId() {
        return masterElectricityMeterApplianceId;
    }

    @Override
    public void setMqttTopic(String mqttTopic) {
    }

    @Override
    public void validate() throws ConfigurationException {
        logger.debug("{}: configured: masterElectricityMeterApplianceId={}", applianceId, masterElectricityMeterApplianceId);
    }

    @Override
    public void init() {
    }

    @Override
    public void start(LocalDateTime now, Timer timer) {
    }

    @Override
    public void stop(LocalDateTime now) {
    }

    @Override
    public void startEnergyMeter() {
    }

    @Override
    public void stopEnergyMeter() {
    }

    @Override
    public void resetEnergyMeter() {
    }
}
