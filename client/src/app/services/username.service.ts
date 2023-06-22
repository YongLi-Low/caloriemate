import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UsernameService {

  // private usernameSubject = new BehaviorSubject<string>('');
  // private idSubject = new BehaviorSubject<string>('');
  private userInfoSubject = new BehaviorSubject<{ username: string, id: string }>({ username: '', id: '' });

  // username$ = this.usernameSubject.asObservable();
  // id$ = this.idSubject.asObservable();
  userInfo$ = this.userInfoSubject.asObservable();

  constructor(private httpClient: HttpClient) { }

  // setUsername(username: string) {
  //   this.usernameSubject.next(username);
  // }

  // getUsername() {
  //   return this.usernameSubject.asObservable();
  // }

  // getId() {
  //   return this.idSubject.asObservable();
  // }

  setUserInfo(username: string, id: string) {
    this.userInfoSubject.next({ username, id });
  }

  getUserInfo() {
    return this.userInfoSubject.asObservable();
  }

  // Get profile image
  getProfile(id: string): Observable<string> {
    const url = 'profile/'+ id + '/getprofile';
    return this.httpClient.get(url, {responseType: 'text'});
  }
}
