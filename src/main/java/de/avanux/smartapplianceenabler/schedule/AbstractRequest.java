/*
 * Copyright (C) 2020 Axel Müller <axel.mueller@avanux.de>
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

package de.avanux.smartapplianceenabler.schedule;

import de.avanux.smartapplianceenabler.control.Control;
import de.avanux.smartapplianceenabler.control.ev.EVChargerState;
import de.avanux.smartapplianceenabler.control.ev.ElectricVehicle;
import de.avanux.smartapplianceenabler.control.ev.SocValues;
import de.avanux.smartapplianceenabler.meter.Meter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
abstract public class AbstractRequest implements Request {
    private transient String applianceId;
    private transient Meter meter;
    private transient Control control;
    private transient boolean next;
    private transient boolean enabled;
    private transient boolean enabledBefore;
    private transient int runtimeUntilLastStatusChange;
    private transient LocalDateTime controlStatusChangedAt;
    private transient Boolean acceptControlRecommendations;
    private transient TimeframeIntervalStateProvider timeframeIntervalStateProvider;


    public AbstractRequest() {
    }

    protected Logger getLogger() {
        return LoggerFactory.getLogger(AbstractRequest.class);
    }

    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
    }

    protected String getApplianceId() {
        return applianceId;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    protected Meter getMeter() {
        return meter;
    }

    @Override
    public void setControl(Control control) {
        this.control = control;
    }

    protected Control getControl() {
        return control;
    }

    public void setTimeframeIntervalStateProvider(TimeframeIntervalStateProvider timeframeIntervalStateProvider) {
        this.timeframeIntervalStateProvider = timeframeIntervalStateProvider;
    }

    abstract public Integer getMin(LocalDateTime now);

    abstract public Integer getMax(LocalDateTime now);

    @Override
    public Integer getMinOrMax(LocalDateTime now) {
        return getMin(now) != null ? getMin(now) : getMax(now);
    }

    @Override
    public boolean isNext() {
        return next;
    }

    @Override
    public void setNext(boolean next) {
        this.next = next;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabledBefore() {
        return enabledBefore;
    }

    protected void resetEnabledBefore() {
        this.enabledBefore = false;
    }

    public boolean isActive() {
        return this.timeframeIntervalStateProvider != null
                && this.timeframeIntervalStateProvider.getState() == TimeframeIntervalState.ACTIVE;
    }

    protected boolean isExpired() {
        return this.timeframeIntervalStateProvider != null
                && this.timeframeIntervalStateProvider.getState() == TimeframeIntervalState.EXPIRED;
    }

    public boolean isControlOn() {
        return control.isOn();
    }

    @Override
    public Boolean isAcceptControlRecommendations() {
        return this.acceptControlRecommendations;
    }

    @Override
    public void setAcceptControlRecommendations(Boolean acceptControlRecommendations) {
        this.acceptControlRecommendations = acceptControlRecommendations;
    }

    @Override
    public void update() {
    }

    @Override
    public void timeframeIntervalCreated(LocalDateTime now, TimeframeInterval timeframeInterval) {
    }

    @Override
    public void activeIntervalChanged(LocalDateTime now, String applianceId, TimeframeInterval deactivatedInterval, TimeframeInterval activatedInterval, boolean wasRunning) {
    }

    @Override
    public Integer getRuntime(LocalDateTime now) {
        if(isEnabledBefore()) {
            return runtimeUntilLastStatusChange + (control.isOn() ? getSecondsSinceStatusChange(now) : 0);
        }
        return 0;
    }

    protected void resetRuntime() {
        runtimeUntilLastStatusChange = 0;
    }

    @Override
    public LocalDateTime getControlStatusChangedAt() {
        return controlStatusChangedAt;
    }

    protected int getSecondsSinceStatusChange(LocalDateTime now) {
        try {
            if(controlStatusChangedAt != null && now != null) {
                return Long.valueOf(
                        Duration.between(controlStatusChangedAt, LocalDateTime.from(now)).toSeconds()).intValue();
            }
        }
        catch(IllegalArgumentException e) {
            getLogger().warn("{} Invalid interval: start={} end={}", getApplianceId(), controlStatusChangedAt,
                    now);
        }
        return 0;
    }

    @Override
    public void controlStateChanged(LocalDateTime now, boolean switchOn) {
        getLogger().trace("{} request={} switchOn={} active={} expired={}", getApplianceId(), toString(now), switchOn, isActive(), isExpired());
        if (isActive() || isExpired()) {
            if (switchOn) {
                enabledBefore = true;
                if (meter != null) {
                    meter.startEnergyMeter();
                }
            } else {
                if (meter != null) {
                    meter.stopEnergyMeter();
                }
                runtimeUntilLastStatusChange += getSecondsSinceStatusChange(now);
            }
            controlStatusChangedAt = now;
        }
    }

    @Override
    public void onEVChargerStateChanged(LocalDateTime now, EVChargerState previousState, EVChargerState newState,
                                        ElectricVehicle ev) {
    }

    @Override
    public void onEVChargerSocChanged(LocalDateTime now, SocValues socValues) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AbstractRequest that = (AbstractRequest) o;

        return new EqualsBuilder()
                .append(enabled, that.enabled)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(enabled)
                .toHashCode();
    }

    @Override
    public String toString() {
        return isEnabled() ? "ENABLED" : "DISABLED";
    }

    @Override
    public abstract String toString(LocalDateTime now);
}
