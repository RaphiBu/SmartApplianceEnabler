import {AfterViewChecked, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ControlContainer, FormGroup, FormGroupDirective, Validators} from '@angular/forms';
import {FormHandler} from '../shared/form-handler';
import {ErrorMessages} from '../shared/error-messages';
import {ErrorMessageHandler} from '../shared/error-message-handler';
import {HttpRead} from './http-read';
import {Logger} from '../log/logger';
import {NestedFormService} from '../shared/nested-form-service';
import {FormMarkerService} from '../shared/form-marker-service';
import {TranslateService} from '@ngx-translate/core';
import {InputValidatorPatterns} from '../shared/input-validator-patterns';
import {HttpReadErrorMessages} from './http-read-error-messages';

@Component({
  selector: 'app-http-read',
  templateUrl: './http-read.component.html',
  styleUrls: ['../global.css'],
  viewProviders: [
    {provide: ControlContainer, useExisting: FormGroupDirective}
  ]
})
export class HttpReadComponent implements OnInit, AfterViewChecked, OnDestroy {
  @Input()
  httpRead: HttpRead;
  @Input()
  valueNames: string[];
  @Input()
  formControlNamePrefix = '';
  form: FormGroup;
  formHandler: FormHandler;
  @Input()
  translationPrefix: string;
  @Input()
  translationKeys: string[];
  translatedStrings: string[];
  errors: { [key: string]: string } = {};
  errorMessages: ErrorMessages;
  errorMessageHandler: ErrorMessageHandler;

  constructor(private logger: Logger,
              private parent: FormGroupDirective,
              private nestedFormService: NestedFormService,
              private formMarkerService: FormMarkerService,
              private translate: TranslateService
  ) {
    this.errorMessageHandler = new ErrorMessageHandler(logger);
    this.formHandler = new FormHandler();
  }

  ngOnInit() {
    this.errorMessages = new HttpReadErrorMessages(this.translate);
    this.form = this.parent.form;
    this.expandParentForm(this.form, this.httpRead, this.formHandler);
    this.form.statusChanges.subscribe(() => {
      this.errors = this.errorMessageHandler.applyErrorMessages4ReactiveForm(this.form, this.errorMessages);
    });
    this.translate.get(this.translationKeys).subscribe(translatedStrings => {
      this.translatedStrings = translatedStrings;
    });
    this.nestedFormService.submitted.subscribe(
      () => this.updateHttpRead(this.httpRead, this.form));
    this.formMarkerService.dirty.subscribe(() => this.form.markAsDirty());
  }

  ngAfterViewChecked() {
    this.formHandler.markLabelsRequired();
  }

  ngOnDestroy() {
    // FIXME: erzeugt Fehler bei Wechsel des Zählertypes
    // this.nestedFormService.submitted.unsubscribe();
  }

  getFormControlName(formControlName: string): string {
    return `${this.formControlNamePrefix}${formControlName.charAt(0).toUpperCase()}${formControlName.slice(1)}`;
  }

  public getTranslatedValueName(valueName: string) {
    const textKey = `${this.translationPrefix}${valueName.toLowerCase()}`;
    return this.translatedStrings[textKey];
  }

  get valueName() {
    if (this.httpRead.readValues && this.httpRead.readValues.length === 1) {
      const httpReadValue = this.httpRead.readValues[0];
      return this.getTranslatedValueName(httpReadValue.name);
    }
    return undefined;
  }

  get disabled() {
    return ! this.form.controls[this.getFormControlName('enabled')].value;
  }

  expandParentForm(form: FormGroup, httpRead: HttpRead, formHandler: FormHandler) {
    formHandler.addFormControl(form, this.getFormControlName('url'),
      httpRead ? httpRead.url : undefined,
      [Validators.required, Validators.pattern(InputValidatorPatterns.URL)]);
    formHandler.addFormControl(form, this.getFormControlName('contentType'),
      httpRead ? httpRead.contentType : undefined);
    formHandler.addFormControl(form, this.getFormControlName('username'),
      httpRead ? httpRead.username : undefined);
    formHandler.addFormControl(form, this.getFormControlName('password'),
      httpRead ? httpRead.password : undefined);
  }

  updateHttpRead(httpRead: HttpRead, form: FormGroup) {
    httpRead.url = this.form.controls[this.getFormControlName('url')].value;
    httpRead.contentType = this.form.controls[this.getFormControlName('contentType')].value;
    httpRead.username = this.form.controls[this.getFormControlName('username')].value;
    httpRead.password = this.form.controls[this.getFormControlName('password')].value;
    this.nestedFormService.complete();
  }
}