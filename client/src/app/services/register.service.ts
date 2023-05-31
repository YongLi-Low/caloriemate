import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private httpClient: HttpClient) { }

  register(form: any) {
    const formData = new FormData;

    formData.set("username", form['username']);
    formData.set("email", form['email']);
    formData.set("password", form['password']);
    formData.set("confirmPassword", form['confirmPassword']);

    return lastValueFrom(this.httpClient.post<string>('/registration', formData));
  }
}
