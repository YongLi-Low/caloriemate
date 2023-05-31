import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from '../services/login.service';
import { Router } from '@angular/router';
import { MatSnackBar, MatSnackBarRef } from '@angular/material/snack-bar';
import { LOCAL_STORAGE, StorageService } from 'ngx-webstorage-service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;
  snackBarRef!: MatSnackBarRef<any> | null;
  id!: string;

  constructor(private fb: FormBuilder, private loginSvc: LoginService,
              private router: Router, private snackBar: MatSnackBar,
              @Inject(LOCAL_STORAGE) private storageService: StorageService) { }

  ngOnInit(): void {
      this.loginForm = this.createForm();
      const storedUsername = this.storageService.get('username');
      const storedPassword = this.storageService.get('password');
      if (storedUsername && storedPassword) {
        this.loginForm.patchValue({
          username: storedUsername,
          password: storedPassword
        })
      }
  }

  createForm() {
    return this.fb.group({
      username: this.fb.control('', [Validators.required, Validators.minLength(3)]),
      password: this.fb.control('', [Validators.required]),
      rememberMe: this.fb.control('false')
    })
  }

  login() {
    const formValue = this.loginForm.value;
    const rememberMe = formValue.rememberMe;

    this.loginSvc.login(formValue)
      .then((response:any) => {
        if (response.response == 'username and password mismatch') {
          this.snackBar.open('Username and Password do not match!', 'Dismiss');
          this.snackBarRef?.onAction().subscribe(() => {
            this.snackBarRef?.dismiss()
          })
        }
        else {
          if (rememberMe) {
            this.loginSvc.saveCredentials(formValue.username, formValue.password);
          }
          this.id = response.response
          this.router.navigate(['/login', formValue['username'], this.id])
        }
      })
      .catch(error => console.log(error))
  }
}
