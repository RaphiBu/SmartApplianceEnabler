import {
  assertCheckbox,
  assertInput,
  assertSelectOption,
  clickButton,
  inputText,
  selectOption,
  selectorCheckboxByFormControlName,
  selectorCheckboxCheckedByFormControlName,
  selectorInputByFormControlName,
  selectorSelectByFormControlName,
  selectorSelectedByFormControlName,
  setCheckboxEnabled
} from '../../shared/form';
import {Appliance} from '../../../../../main/angular/src/app/appliance/appliance';
import {Selector} from 'testcafe';

export class AppliancePage {

  private static SAVE_BUTTON_SELECTOR = 'button[type="submit"]';

  public static pageSelector(t: TestController): Selector {
    return Selector('form.ApplianceComponent');
  }

  public static async waitForPage(t: TestController): Promise<void> {
    await t.expect(await this.pageExists(t)).ok();
  }

  public static async pageExists(t: TestController): Promise<boolean> {
    return (await this.pageSelector(t)).exists;
  }

  public static async setAppliance(t: TestController, appliance: Appliance) {
    await this.waitForPage(t);
    await AppliancePage.setId(t, appliance.id);
    await AppliancePage.setVendor(t, appliance.vendor);
    await AppliancePage.setName(t, appliance.name);
    await AppliancePage.setType(t, appliance.type);
    await AppliancePage.setSerial(t, appliance.serial);
    await AppliancePage.setMinPowerConsumption(t, appliance.minPowerConsumption);
    await AppliancePage.setMaxPowerConsumption(t, appliance.maxPowerConsumption);
    await AppliancePage.setInterruptionsAllowed(t, appliance.interruptionsAllowed);
    if (appliance.interruptionsAllowed) {
      await AppliancePage.setMinOnTime(t, appliance.minOnTime);
      await AppliancePage.setMaxOnTime(t, appliance.maxOnTime);
      await AppliancePage.setMinOffTime(t, appliance.minOffTime);
      await AppliancePage.setMaxOffTime(t, appliance.maxOffTime);
    }
    await AppliancePage.setNotificationSenderId(t, appliance.notificationSenderId);
  }

  public static async assertAppliance(t: TestController, appliance: Appliance) {
    await this.waitForPage(t);
    await AppliancePage.assertId(t, appliance.id);
    await AppliancePage.assertVendor(t, appliance.vendor);
    await AppliancePage.assertName(t, appliance.name);
    await AppliancePage.assertType(t, appliance.type);
    await AppliancePage.assertSerial(t, appliance.serial);
    await AppliancePage.assertMinPowerConsumption(t, appliance.minPowerConsumption);
    await AppliancePage.assertMaxPowerConsumption(t, appliance.maxPowerConsumption);
    await AppliancePage.assertInterruptionsAllowed(t, appliance.interruptionsAllowed);
    if (appliance.interruptionsAllowed) {
      await AppliancePage.assertMinOnTime(t, appliance.minOnTime);
      await AppliancePage.assertMaxOnTime(t, appliance.maxOnTime);
      await AppliancePage.assertMinOffTime(t, appliance.minOffTime);
      await AppliancePage.assertMaxOffTime(t, appliance.maxOffTime);
    }
    await AppliancePage.assertNotificationSenderId(t, appliance.notificationSenderId);
  }

  public static async setId(t: TestController, id: string) {
    await inputText(t, selectorInputByFormControlName('id'), id);
  }
  public static async assertId(t: TestController, id: string) {
    await assertInput(t, selectorInputByFormControlName('id'), id);
  }

  public static async setVendor(t: TestController, vendor: string) {
    await inputText(t, selectorInputByFormControlName('vendor'), vendor);
  }
  public static async assertVendor(t: TestController, vendor: string) {
    await assertInput(t, selectorInputByFormControlName('vendor'), vendor);
  }

  public static async setName(t: TestController, name: string) {
    await inputText(t, selectorInputByFormControlName('name'), name);
  }
  public static async assertName(t: TestController, name: string) {
    await assertInput(t, selectorInputByFormControlName('name'), name);
  }

  public static async setType(t: TestController, type: string) {
    await selectOption(t, selectorSelectByFormControlName('type'), type);
  }
  public static async assertType(t: TestController, type: string) {
    await assertSelectOption(t, selectorSelectedByFormControlName('type'), type, 'ApplianceComponent.type.');
  }

  public static async setSerial(t: TestController, serial: string) {
    await inputText(t, selectorInputByFormControlName('serial'), serial);
  }
  public static async assertSerial(t: TestController, serial: string) {
    await assertInput(t, selectorInputByFormControlName('serial'), serial);
  }

  public static async setMinPowerConsumption(t: TestController, minPowerConsumption: number) {
    if (minPowerConsumption) {
      await inputText(t, selectorInputByFormControlName('minPowerConsumption'), minPowerConsumption.toString());
    }
  }
  public static async assertMinPowerConsumption(t: TestController, minPowerConsumption: number) {
    if (minPowerConsumption) {
      await assertInput(t, selectorInputByFormControlName('minPowerConsumption'), minPowerConsumption.toString());
    }
  }

  public static async setMaxPowerConsumption(t: TestController, maxPowerConsumption: number) {
    await inputText(t, selectorInputByFormControlName('maxPowerConsumption'), maxPowerConsumption.toString());
  }
  public static async assertMaxPowerConsumption(t: TestController, maxPowerConsumption: number) {
    await assertInput(t, selectorInputByFormControlName('maxPowerConsumption'), maxPowerConsumption.toString());
  }

  public static async setInterruptionsAllowed(t: TestController, interruptionsAllowed: boolean) {
    await setCheckboxEnabled(t, selectorCheckboxByFormControlName('interruptionsAllowed'), interruptionsAllowed);
  }
  public static async assertInterruptionsAllowed(t: TestController, interruptionsAllowed: boolean) {
    await assertCheckbox(t, selectorCheckboxCheckedByFormControlName('interruptionsAllowed'), interruptionsAllowed);
  }

  public static async setMinOnTime(t: TestController, minOnTime: number) {
    await inputText(t, selectorInputByFormControlName('minOnTime'), minOnTime && minOnTime.toString());
  }
  public static async assertMinOnTime(t: TestController, minOnTime: number) {
    await assertInput(t, selectorInputByFormControlName('minOnTime'), minOnTime ? minOnTime.toString() : '');
  }

  public static async setMaxOnTime(t: TestController, maxOnTime: number) {
    await inputText(t, selectorInputByFormControlName('maxOnTime'), maxOnTime && maxOnTime.toString());
  }
  public static async assertMaxOnTime(t: TestController, maxOnTime: number) {
    await assertInput(t, selectorInputByFormControlName('maxOnTime'), maxOnTime ? maxOnTime.toString() : '');
  }

  public static async setMinOffTime(t: TestController, minOffTime: number) {
    await inputText(t, selectorInputByFormControlName('minOffTime'), minOffTime && minOffTime.toString());
  }
  public static async assertMinOffTime(t: TestController, minOffTime: number) {
    await assertInput(t, selectorInputByFormControlName('minOffTime'), minOffTime ? minOffTime.toString() : '');
  }

  public static async setMaxOffTime(t: TestController, maxOffTime: number) {
    await inputText(t, selectorInputByFormControlName('maxOffTime'), maxOffTime && maxOffTime.toString());
  }
  public static async assertMaxOffTime(t: TestController, maxOffTime: number) {
    await assertInput(t, selectorInputByFormControlName('maxOffTime'), maxOffTime ? maxOffTime.toString() : '');
  }

  public static async setNotificationSenderId(t: TestController, notificationSenderId: string) {
    await inputText(t, selectorInputByFormControlName('notificationSenderId'), notificationSenderId);
  }
  public static async assertNotificationSenderId(t: TestController, notificationSenderId: string) {
    await assertInput(t, selectorInputByFormControlName('notificationSenderId'), notificationSenderId);
  }

  public static async clickSave(t: TestController) {
    await clickButton(t, AppliancePage.SAVE_BUTTON_SELECTOR);
  }
}
