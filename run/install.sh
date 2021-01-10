#!/bin/sh
#
# Copyright (C) 2021 Axel Müller <axel.mueller@avanux.de>
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#

LOG=/tmp/sae-install.log
PREFIX="**********"
PASSWORD="sae"

echo "$PREFIX Update software catalog ..." >> $LOG
apt update 2>&1 >> $LOG

echo "$PREFIX Upgrading system ..." >> $LOG
apt upgrade -y 2>&1 >> $LOG

echo "$PREFIX Setting time zone ..." >> $LOG
echo 'Europe/Berlin' > /etc/timezone
echo /etc/timezone >> $LOG

echo "$PREFIX Copy zoneinfo ..." >> $LOG
cp /usr/share/zoneinfo/Europe/Berlin /etc/localtime 2>&1 >> $LOG

echo "$PREFIX Install OpenJDK 11 ..." >> $LOG
apt install openjdk-11-jre-headless -y 2>&1 >> $LOG

echo "$PREFIX Install Wiring Pi ..." >> $LOG
apt install wiringpi -y 2>&1 >> $LOG

echo "$PREFIX Setting up user ..." >> $LOG
mkdir /opt/sae 2>&1 >> $LOG
groupadd sae 2>&1 >> $LOG
useradd -d /opt/sae -c "SmartApplianceEnabler" -g sae -M sae -s /bin/bash 2>&1 >> $LOG
usermod -a -G gpio,sudo sae 2>&1 >> $LOG
echo $PASSWORD | passwd sae --stdin 2>&1 >> $LOG
cp /home/pi/.profile /opt/sae 2>&1 >> $LOG
cp /home/pi/.bashrc /opt/sae 2>&1 >> $LOG

echo "$PREFIX Installing SAE ..." >> $LOG
wget https://github.com/camueller/SmartApplianceEnabler/raw/master/run/smartapplianceenabler -P /opt/sae 2>>$LOG
chmod 755 /opt/sae/smartapplianceenabler 2>&1 >> $LOG

wget https://github.com/camueller/SmartApplianceEnabler/raw/master/run/etc/default/smartapplianceenabler -P /etc/default 2>>$LOG
chown root.root /etc/default/smartapplianceenabler 2>&1 >> $LOG
chmod 644 /etc/default/smartapplianceenabler 2>&1 >> $LOG

wget https://github.com/camueller/SmartApplianceEnabler/raw/master/run/logback-spring.xml -P /opt/sae 2>>$LOG
chmod 644 /opt/sae/logback-spring.xml 2>&1 >> $LOG

wget https://github.com/camueller/SmartApplianceEnabler/releases/download/1.6.8/SmartApplianceEnabler-1.6.8.war -P /opt/sae 2>>$LOG

chown -R sae:sae /opt/sae 2>&1 >> $LOG

echo "$PREFIX Configure systemd ..." >> $LOG
wget https://github.com/camueller/SmartApplianceEnabler/raw/master/run/lib/systemd/system/smartapplianceenabler.service -P /lib/systemd/system 2>>$LOG
chown root.root /lib/systemd/system/smartapplianceenabler.service 2>&1 >> $LOG
chmod 755 /lib/systemd/system/smartapplianceenabler.service 2>&1 >> $LOG

systemctl enable smartapplianceenabler.service 2>&1 >> $LOG
systemctl daemon-reload 2>&1 >> $LOG

echo "$PREFIX Starting SAE ..." >> $LOG
systemctl start smartapplianceenabler.service 2>&1 >> $LOG

echo "$PREFIX $0 finished" >> $LOG