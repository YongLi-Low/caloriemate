import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RegisterService } from '../services/register.service';
import { Router } from '@angular/router';
import { MatSnackBar, MatSnackBarRef } from '@angular/material/snack-bar';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit{

  regForm!: FormGroup
  snackBarRef!: MatSnackBarRef<any> | null;

  constructor(private fb: FormBuilder, private registerSvc: RegisterService,
              private router: Router, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.regForm = this.createForm();

    // Listen to password value changes and update validity status
    this.regForm.get('password')?.valueChanges.subscribe(() => {
      this.regForm.get('confirmPassword')?.updateValueAndValidity();
    });
  }

  createForm() {
    return this.fb.group({
      username: this.fb.control('', [Validators.required, Validators.minLength(3)]),
      email: this.fb.control('', [Validators.required, Validators.email]),
      password: this.fb.control('', Validators.required),
      confirmPassword: this.fb.control('', [Validators.required]),
    }, { validators: this.passwordMatchValidator })
  }

  // Custom validator function for password
  passwordMatchValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    // @ts-ignore
    if (password.value !== confirmPassword.value) {
      confirmPassword?.setErrors({ passwordMismatch: true });
      return { 'passwordMismatch': true };
    }
    else {
      confirmPassword?.setErrors(null);
      return null;
    }
  }

  register() {
    const formVal = this.regForm.value;
    // console.info(formVal)
    this.registerSvc.register(formVal)
      .then((response: any) => {
        // console.info('>>> Response: ', response)
        if (response.response === 'Username exists!') {
          // Show material snackbar with the error message and the close button
          this.snackBar.open('Username already exists!', 'Dismiss');
          this.snackBarRef?.onAction().subscribe(() => {
            this.snackBarRef?.dismiss()
          })
        }
        else {
          this.router.navigate(['/register/success'])
        }
      })
      .catch(error => console.log(error))
  }

  onFileChange(event: any) {
    const file = event.target.files[0];
    this.regForm.patchValue({image:file});
  }
}
