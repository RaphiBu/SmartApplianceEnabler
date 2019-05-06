/*
 * Copyright (C) 2019 Axel Müller <axel.mueller@avanux.de>
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

package de.avanux.smartapplianceenabler.modbus;

import de.avanux.smartapplianceenabler.control.ev.EVControl;
import de.avanux.smartapplianceenabler.control.ev.EVReadValueName;
import de.avanux.smartapplianceenabler.control.ev.EVWriteValueName;
import de.avanux.smartapplianceenabler.modbus.executor.*;
import de.avanux.smartapplianceenabler.util.ParentWithChild;
import de.avanux.smartapplianceenabler.util.RequestCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class EVModbusControl extends ModbusSlave implements EVControl {

    private transient Logger logger = LoggerFactory.getLogger(EVModbusControl.class);
    @XmlElement(name = "ModbusRegisterRead")
    private List<ModbusRegisterRead> registerReads;
    @XmlElement(name = "ModbusRegisterWrite")
    private List<ModbusRegisterWrite> registerWrites;
    private transient Integer pollInterval; // seconds
    private transient RequestCache<ModbusRegisterRead, ModbusReadTransactionExecutor> requestCache;

    public List<ModbusRegisterRead> getRegisterReads() {
        return registerReads;
    }

    public void setRegisterReads(List<ModbusRegisterRead> registerReads) {
        this.registerReads = registerReads;
    }

    public List<ModbusRegisterWrite> getRegisterWrites() {
        return registerWrites;
    }

    public void setRegisterWrites(List<ModbusRegisterWrite> registerWrites) {
        this.registerWrites = registerWrites;
    }

    public void setPollInterval(Integer pollInterval) {
        this.pollInterval = pollInterval;
    }

    public RequestCache<ModbusRegisterRead, ModbusReadTransactionExecutor> getRequestCache() {
        return requestCache;
    }

    public void setRequestCache(RequestCache<ModbusRegisterRead, ModbusReadTransactionExecutor> requestCache) {
        this.requestCache = requestCache;
    }

    @Override
    public void init() {
        int cacheMaxAgeSeconds = this.pollInterval - 1;
        this.requestCache = new RequestCache<>(getApplianceId(), cacheMaxAgeSeconds);

    }

    @Override
    public void validate() {
        logger.debug("{}: Validating configuration", getApplianceId());
        boolean valid = true;
        ModbusValidator validator = new ModbusValidator(getApplianceId());
        for(EVReadValueName valueName: EVReadValueName.values()) {
            List<ParentWithChild<ModbusRegisterRead, ModbusRegisterReadValue>> reads
                    = ModbusRegisterRead.getRegisterReads(valueName.name(), this.registerReads);
            valid = validator.validateReads(valueName.name(), reads);
        }

        for(EVWriteValueName valueName: EVWriteValueName.values()) {
            List<ParentWithChild<ModbusRegisterWrite, ModbusRegisterWriteValue>> writes
                    = ModbusRegisterWrite.getRegisterWrites(valueName.name(), this.registerWrites);
            valid = validator.validateWrites(valueName.name(), writes);
        }
        if(! valid) {
            logger.error("{}: Terminating because of incorrect configuration", getApplianceId());
            System.exit(-1);
        }
    }

    @Override
    public boolean isVehicleNotConnected() {
        return isMatchingVehicleStatus(EVReadValueName.VehicleNotConnected);
    }

    @Override
    public boolean isVehicleConnected() {
        return isMatchingVehicleStatus(EVReadValueName.VehicleConnected);
    }

    @Override
    public boolean isCharging() {
        return isMatchingVehicleStatus(EVReadValueName.Charging);
    }

    @Override
    public boolean isInErrorState()  {
        return isMatchingVehicleStatus(EVReadValueName.Error);
    }

    public boolean isMatchingVehicleStatus(EVReadValueName registerName) {
        List<ParentWithChild<ModbusRegisterRead, ModbusRegisterReadValue>> reads
                = ModbusRegisterRead.getRegisterReads(registerName.name(), this.registerReads);
        if (reads.size() > 0) {
            boolean result = true;
            for (ParentWithChild<ModbusRegisterRead, ModbusRegisterReadValue> read : reads) {
                ModbusRegisterRead registerRead = read.parent();
                try {
                    ModbusReadTransactionExecutor executor = this.requestCache.get(registerRead);
                    if (executor == null) {
                        executor = ModbusExecutorFactory.getReadExecutor(getApplianceId(),
                                registerRead.getType(), registerRead.getAddress(), registerRead.getBytes());
                        executeTransaction(executor, true);
                        this.requestCache.put(registerRead, executor);
                    }
                    else {
                        if(executor instanceof BaseTransactionExecutor) {
                            BaseTransactionExecutor readInputRegisterExecutor = (BaseTransactionExecutor) executor;
                            logger.debug("{}: Using cached input register={}", getApplianceId(),
                                    readInputRegisterExecutor.getAddress());
                        }
                    }
                    if (result) {
                        if (executor != null) {
                            if (executor instanceof ReadStringInputRegisterExecutor) {
                                String registerValue = ((ReadStringInputRegisterExecutor) executor).getValue();
                                logger.debug("{}: Register value={}", getApplianceId(), registerValue);
                                result &= registerValue.matches(read.child().getExtractionRegex());
                            } else if (executor instanceof ReadCoilExecutor) {
                                Boolean registerValue = ((ReadCoilExecutor) executor).getValue();
                                logger.debug("{}: Register value={}", getApplianceId(), registerValue);
                                result &= registerValue;
                            } else if (executor instanceof ReadDiscreteInputExecutor) {
                                Boolean registerValue = ((ReadDiscreteInputExecutor) executor).getValue();
                                logger.debug("{}: Register value={}", getApplianceId(), registerValue);
                                result &= registerValue;
                            }
                        }
                        else {
                            logger.error("{}: no input register executor available", getApplianceId());
                        }
                    } else {
                        logger.debug("{}: Skipping read register {}", getApplianceId(), registerRead.getAddress());
                    }
                } catch (Exception e) {
                    logger.error("{}: Error reading register {}", getApplianceId(), registerRead.getAddress(), e);
                }
            }
            return result;
        }
        return false;
    }

    @Override
    public void setChargeCurrent(int current) {
        logger.debug("{}: Set charge current {}A", getApplianceId(), current);
        ParentWithChild<ModbusRegisterWrite, ModbusRegisterWriteValue> write = ModbusRegisterWrite.getFirstRegisterWrite(
                EVWriteValueName.ChargingCurrent.name(), this.registerWrites);
        if(write != null) {
            if(this.requestCache != null) {
                // the next poll after write should return a fresh response from charger
                this.requestCache.clear();
            }
            ModbusRegisterWrite registerWrite = write.parent();
            try {
                ModbusWriteTransactionExecutor executor = ModbusExecutorFactory.getWriteExecutor(getApplianceId(),
                        registerWrite.getType(), registerWrite.getAddress(), registerWrite.getFactorToValue());
                if(executor != null) {
                    executor.setValue(current);
                    executeTransaction(executor, true);
                }
            }
            catch(Exception e) {
                logger.error("{}: Error setting charge current in register {}", getApplianceId(),
                        registerWrite.getAddress(), e);
            }
        }
    }

    @Override
    public void startCharging() {
        logger.debug("{}: Start charging", getApplianceId());
        setCharging(EVWriteValueName.StartCharging);
    }

    @Override
    public void stopCharging() {
        logger.debug("{}: Stop charging", getApplianceId());
        setCharging(EVWriteValueName.StopCharging);
    }

    private void setCharging(EVWriteValueName registerName) {
        ParentWithChild<ModbusRegisterWrite, ModbusRegisterWriteValue> write
                = ModbusRegisterWrite.getFirstRegisterWrite(registerName.name(), this.registerWrites);
        if(write != null) {
            if(this.requestCache != null) {
                // the next poll after write should return a fresh response from charger
                this.requestCache.clear();
            }
            ModbusRegisterWrite registerWrite = write.parent();
            try {
                ModbusWriteTransactionExecutor executor = ModbusExecutorFactory.getWriteExecutor(getApplianceId(),
                        registerWrite.getType(), registerWrite.getAddress(), registerWrite.getFactorToValue());
                if(executor != null) {
                    String stringValue = write.child().getValue();
                    Object value = null;
                    if(ModbusWriteRegisterType.Coil.equals(registerWrite.getType())) {
                        value = "1".equals(stringValue);
                    }
                    executor.setValue(value);
                    executeTransaction(executor, true);
                }
            }
            catch(Exception e) {
                logger.error("{}: Error enable/disable charging process in register {}", getApplianceId(),
                        registerWrite.getAddress(), e);
            }
        }
    }
}
