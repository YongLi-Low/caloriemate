import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from '../services/login.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar, MatSnackBarRef } from '@angular/material/snack-bar';
import { LOCAL_STORAGE, StorageService } from 'ngx-webstorage-service';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;
  snackBarRef!: MatSnackBarRef<any> | null;
  username!: string;
  id!: string;
  state!: string | null;
  code!: string | null;

  title!:string;
  description!:string;
  startDateTime!:string;
  endDateTime!:string;

  constructor(private fb: FormBuilder, private loginSvc: LoginService,
              private router: Router, private snackBar: MatSnackBar,
              private activatedRoute: ActivatedRoute, private httpClient: HttpClient,
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

      this.state = this.activatedRoute.snapshot.queryParamMap.get('state');
      this.code = this.activatedRoute.snapshot.queryParamMap.get('code');
      this.waitForVariables();
      
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

  waitForVariables() {
    if (this.code != null && this.state != null) {
      let stateData = JSON.parse(window.atob(this.state));
      this.username = stateData['username'];
      this.id = stateData['id'];
      this.title = stateData['title'];
      this.description = stateData['description'];
      this.startDateTime = stateData['startDateTime'];
      this.endDateTime = stateData['endDateTime'];

      // Create an object with the required data
      let postData = {
        'username': this.username,
        'id': this.id,
        'title': this.title,
        'description': this.description,
        'startDateTime': this.startDateTime,
        'endDateTime': this.endDateTime,
        'code': this.code
      };

      // Use HttpClient to send the POST request
      lastValueFrom(this.httpClient.post('/api/exercises/events', postData))
      .then((response: any) => {
        if (response.response == 'Event created successfully') {
          console.info("Login page: ", response)
          this.showSnackBar('Added to Calendar. Please log in again.', 'success');
        }
      })
      .catch(error => {
        console.info("Login page: error")
      })
    }
  }

  showSnackBar(message: string, panelClass: string) {
    this.snackBar.open(message, 'Close', {
     panelClass: panelClass
    });
  }
}
