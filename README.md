<p align="center">
  <img src="https://github.com/camueller/SmartApplianceEnabler/blob/master/pics/logo-384x384.png">
  <h3 align="center">Smart Appliance Enabler</h3>
  <p align="center">
    <a href="https://github.com/camueller/SmartApplianceEnabler/releases/download/1.6.20/SmartApplianceEnabler-1.6.20.war">
      <img src="https://img.shields.io/badge/Download-1.6.20-brightgreen.svg">
    </a>
    <a href="https://hub.docker.com/repository/docker/avanux/smartapplianceenabler-arm32">
      <img src="https://img.shields.io/badge/Docker-arm32-blue">
    </a>
    <a href="https://hub.docker.com/repository/docker/avanux/smartapplianceenabler-amd64">
      <img src="https://img.shields.io/badge/Docker-amd64-blue">
    </a>
    <a href="https://www.gnu.org/licenses/old-licenses/gpl-2.0.html">
      <img src="https://img.shields.io/badge/license-GPLv2-blue.svg">
    </a>
  </p>
</p>

![Donate](pics/donate.jpeg)

**Seit mehreren Jahren habe ich einen Grossteil meiner Freizeit in Entwicklung, Dokumentation und Support dieses Open-Source-Projektes gesteckt. Ich habe mich bewusst dazu entschieden, diese Software kostenfrei zur Verfügung zu stellen und kontinuierlich um Features zu erweitern, die für möglichst viele Nutzer von Interesse sein könnten. Durch die Nutzung dieser Software lassen sich die Stromkosten signifikant senken, indem die Nutzung des selbst erzeugten Stroms maximiert wird. Ich würde es als Anerkennung meiner Arbeit betrachten, wenn ein Teil dieser Ersparnis als Beitrag zur Förderung dieses Projekts verwendet werden würde. Das geht ganz einfach per [Paypal](https://paypal.me/CarlAxelMueller) oder als klassische Banküberweisung (Kontoinhaber: Axel Müller, IBAN: DE83 5185 0079 1140 0764 37, BIC: HELADEF1FRI, Verwendungszweck: Förderbeitrag Smart Appliance Enabler)**
<br><br>

# Überblick
**Der *Smart Appliance Enabler* ist eine Software zur Integration beliebiger Geräte ([Wallbox](doc/EVCharger_DE.md), Wärmepumpe, Waschmaschine, Geschirrspüler, ...) mit dem [Sunny Home Manager](https://www.sma.de/produkte/monitoring-control/sunny-home-manager-20.html) von [SMA](http://www.sma.de), um eine **Maximierung des Eigenverbrauchs von Photovoltaik-Anlagen** zu ermöglichen.**

![SmartHomeEnablerSchema](pics/SmartApplianceEnabler.png)

Dazu meldet der *Smart Appliance Enabler* dem *Sunny Home Manager* **Bedarfsanforderungen** dieser Geräte um diesem eine optimale Planung des Eigenverbrauchs zu ermöglichen. Entsprechend dieser Planung empfängt der *Smart Appliance Enabler* **Schaltbefehle**, die er an die von ihm verwalteten Geräte weiterleitet. Falls für diese Geräte individuelle, **digitale Stromzähler** verwendet werden, können diese ausgelesen werden und der Stromverbrauch an den *Sunny Home Manager* gemeldet werden, um diesen beim Lernen der Verbrauchscharakteristik zu unterstüzen und Verbräuche im [Sunny Portal](https://www.sunnyportal.com/) zu visualieren.

Die nachfolgende Grafik aus dem [Sunny Portal](https://www.sunnyportal.com/) zeigt einen sonnigen Herbstag, an dem die [Wallbox](doc/EVCharger_DE.md) optimal angesteuert wurde, um das E-Auto so zu laden, dass Netzbezug vermieden wird. Gleichzeitig musste der *Sunny Home Manager* den Geschirrspüler und die Waschmaschine entsprechend der eingestellten Anforderungen laufen lassen. Zusätzlich waren auch die Wärmepumpe (Warmwasser und Heizung) und die Espressomaschine zeitweise eingeschaltet, die nicht durch den *Sunny Home Manager* gesteuert werden, aber natürlich auch Strom verbrauchen.

![SHM_Verbraucherbilanz_GuterTag](pics/shm/Verbraucherbilanz_GuterTag.png)

# Hardware

## Anforderungen

Der *Smart Appliance Enabler* wurde in **Java** implementiert und läuft grundsätzlich auf jedem Gerät, für das eine Java Virtual Machine mit Java 11 existiert. Neben dem compilierten Code werden diverse Scripts bereitgestellt in denen von **Linux** als Betriebssystem ausgegangen wird. Falls Geräte über [GPIO](https://www.itwissen.info/GPIO-general-purpose-input-output.html) angebunden werden sollen, benötigt der *Smart Appliance Enabler* einen [**Raspberry Pi**](doc/Raspberry_DE.md) als Hardware. 

## Stromzähler

Aktuell unterstützt der *Smart Appliance Enabler* folgende Möglichkeiten, den Stromverbrauch eines Gerätes zu messen, um ihn an den *Sunny Home Manager* zu melden:

| Protokolle    | Produkte                                                                                                                                                                                                                                                                                                                                                                      |
| ------------- |-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| GPIO | [S0-Zähler](doc/S0Meter_DE.md)                                                                                                                                                                                                                                                                                                                                                |
| Modbus | [Modbus-basierte Zähler](doc/ModbusMeter_DE.md)                                                                                                                                                                                                                                                                                                                               |
| HTTP | [HTTP-basierte Zähler](doc/HttpMeter_DE.md) allgemein<ul></ul>kompatible Produkte<ul><li>[Shelly Plug/3EM/4PM](doc/Shelly_DE.md)</li><li>[Edimax SP-2101W](doc/EdimaxSP2101W_DE.md)</li></ul>alle Produkte mit [Tasmota](doc/Tasmota_DE.md)-Firmware |

## Schalter

Zum Ein-/Ausschalten eines Gerätes unterstützt der *Smart Appliance Enabler* derzeit folgende Möglichkeiten:

| Protokolle    | Produkte      |
| ------------- | ------------- |
| GPIO | [GPIO-basierte Schalter](doc/GPIOSwitch_DE.md) |
| Modbus | [Modbus-basierte Schalter](doc/ModbusSwitch_DE.md)<br><br>[Wallboxen mit Modbus-Schnittstelle](doc/EVCharger_DE.md)|
| HTTP | [HTTP-basierte Schalter](doc/HttpSwitch_DE.md) allgemein<ul></ul>kompatible Produkte<ul><li>[Shelly Plug/4PM](doc/Shelly_DE.md)</li><li>[Edimax SP-2101W](doc/EdimaxSP2101W_DE.md)</li></ul>alle Produkte mit [Tasmota](doc/Tasmota_DE.md)-Firmware<ul></ul>[Wallboxen mit HTTP-Schnittstelle](doc/EVCharger_DE.md)|

[Details zu diesen Schaltern und weiteren Software-basierten Schalten (z.B. Anlaufstromerkennung)](doc/Control_DE.md)

# Montage

Falls der *Smart Appliance Enabler* auf einem Raspberry Pi betrieben werden soll, sollten dafür die [Montage-Hinweise](doc/Montage_DE.md) beachtet werden.

# Installation / Update

Die [Standard-Installation](doc/Installation_DE.md) läuft automatisch ab und erfordert keine Linux-Kenntnisse.

Falls notwendig, kann alternativ auch die [manuelle Installation](doc/ManualInstallation_DE.md) gewählt werden, bei der sämtliche Befehle der Dokumentation manuell auszuführen sind. 

Die [Installation von Updates](doc/Update_DE.md) ermöglicht die Nutzung neuer Funktionen und Fehlerbehebungen.

<a href="doc/Docker_DE.md"><img align="left" src="pics/Docker.png"></a> Bei Bedarf lässt sich der *Smart Appliance Enabler* auch im Container mit den bereitgestellten [Docker-Images](doc/Docker_DE.md) betreiben.<br><br>

# Konfiguration und Steuerung

Die [Konfiguration](doc/Configuration_DE.md) des *Smart Appliance Enabler* erfolgt mit dem **Web-Browser**.

Der Status aller steuerbaren Geräte wird in der [Status-Anzeige](doc/Status_DE.md) übersichtlich dargestellt, wobei der Status jedes Gerätes durch eine **Ampel** visualisiert wird. Die Ampel kann auch zur **manuellen Steuerung** verwendet werden.

In dem nachfolgenden Video kann man einen Eindruck von der Konfiguration des *Smart Appliance Enabler* gewinnen. Dieses Video wurde im Rahmen der Durchführung automatisierter Tests erstellt, welche die Qualität des *Smart Appliance Enabler* sicherstellen sollen.

[![Web-Oberfläche](pics/fe/BrowserstackPreview.png)](https://drive.google.com/file/d/1g7mgRbnyaPVroLKrlPBE5j1Jmu51WwqD/view)

# Integration in das Sunny Portal
Neben der Steuerung durch den *Sunny Home Manager* ermöglicht der *Smart Appliance Enabler* die [Integration von Geräten in das Sunny Portal](doc/SunnyPortal_DE.md).

![Verbraucherbilanz](pics/shm/Verbraucherbilanz.png)

# Häufige Fragen und Antworten
<a href="doc/QA_DE.md"><img align="left" src="pics/fragen_antworten.jpeg"></a>[Diese Seite führt bei Problemen hoffentlich zur Lösung](doc/QA_DE.md). Ausgehend von häufig gestellten Fragen werden Antworten gegeben sowie Verweise zu relevanten Teilen der Dokumentation.
<br><br><br><br><br>

# Unterstützung
<a href="doc/Support_DE.md"><img align="left" src="pics/support.jpeg"></a>
Diskussionen und Fragen rund um den *Smart Appliance Enabler* sind in [Github Diskussion](https://github.com/camueller/SmartApplianceEnabler/discussions) richtig platziert.

Darüber hinaus biete ich einen [persönlichen, individuellen, kostenpflichtigen Support mit Beratung](doc/Support_DE.md) an - quasi das "Rundum-sorglos Paket".
<br><br><br>

# Mitmachen
<a href="doc/Contribute_DE.md"><img align="left" src="pics/IWantYou.png"></a>Zum [Mitmachen](doc/Contribute_DE.md) muss man **kein Programmierer** sein! [Mitmachen](doc/Contribute_DE.md) kann **jeder** auf vielfältige Weise! [Eure Anwendunsszenarien für den *Smart Appliance Enabler*](doc/ApplicationScenarios_DE.md) helfen Anderen weiter!
<br><br><br><br><br><br>

# Entwicklung
Alle Informationen rund um die [Entwicklung](doc/Development_DE.md) des *Smart Appliance Enabler* einschliesslich der Dokumentation der Build- und Testumgebungen.
<br><br>

# Dank und Anerkennung
Das Projekt *Smart Appliance Enabler* wurde **von [SMA](http://www.sma.de) unterstützt** durch die kostenfreie Bereitstellung eines [Sunny Home Manager 2.0](https://www.sma.de/produkte/monitoring-control/sunny-home-manager-20.html)!

Der *Smart Appliance Enabler* würde so nicht existieren ohne folgende Tools und Frameworks, deren Entwicklern ich Dank und Anerkennung zolle:
* [Angular](https://angular.io) für das Web-Frontend
* [Angular Material](https://material.angular.io/) für die Material-Design-Komponenten des Web-Frontends
* [Browserstack](https://www.browserstack.com) für automatisierte Multi-Browser-Tests und kostenlose Lizenz für Open-Source-Projekte
* [Cling](http://4thline.org/projects/cling) für UPnP (SEMP-Protokoll)
* [Docker](https://www.docker.com/) für die Entwicklung einer Container-Platform
* [Docker Hub](https://hub.docker.com/) für den Betrieb einer Community-Platform zum Austausch von für Docker-Images
* [GitHub](https://github.com/) für den Betrieb einer Community-Platform für Open-Source-Projekte
* [IntelliJ](https://www.jetbrains.com/de-de/idea/) für die beste IDE (vor allem auch unter Linux) und kostenlose Lizenz für Open-Source-Projekte
* [Pi4J](http://pi4j.com) für den Zugriff auf die GPIO-Ports des Raspberry
* [Spring Boot](https://spring.io/projects/spring-boot) für Spring-basierte Anwendungen mit eingebettetem Web-Container
* [Testcafe](https://devexpress.github.io/testcafe/) für einfaches Testen von Web-Anwendungen
* [Travis CI](https://www.travis-ci.com/) für den Betrieb einer Continuous Integration-Platform
* [WebStorm](https://www.jetbrains.com/de-de/webstorm/) für die beste Web-IDE (vor allem auch unter Linux) und kostenlose Lizenz für Open-Source-Projekte
<br><br>

# Lizenz
Die Inhalte in diesem Repository sind lizensiert unter der [GNU GENERAL PUBLIC LICENSE](LICENSE.txt), falls nicht anders angegeben.
