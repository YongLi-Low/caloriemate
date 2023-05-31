import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { LOCAL_STORAGE, StorageService } from 'ngx-webstorage-service';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private httpClient: HttpClient, @Inject(LOCAL_STORAGE) private storageService: StorageService) { }

  login(form: any) {
    const formData = new FormData;

    formData.set("username", form['username']);
    formData.set("password", form['password']);

    return lastValueFrom(this.httpClient.post<string>('/login', formData));
  }

  saveCredentials(username: string, password: string) {
    this.storageService.set('username', username);
    this.storageService.set('password', password);
  }
}
