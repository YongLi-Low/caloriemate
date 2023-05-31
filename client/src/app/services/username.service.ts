import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

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

  constructor() { }

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
}
