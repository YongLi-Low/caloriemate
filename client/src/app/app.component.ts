import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MenuItem } from './models/MenuItem';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { UsernameService } from './services/username.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'CalorieMate';
  username!: string
  id!: string
  currentDate!: Date
  formattedDate!: string

  logout = () => {
    this.router.navigate(['/']).then(() => {
      this.router.navigateByUrl(this.router.url);
    });
    this.username = '';
    this.id = '';
    this.menuItems = [
      { label: 'Register an Account', link: '/register' }
    ];
    this.menuItems2 = [
      { label: 'Register an Account', link: '/register' }
    ];
  };

  constructor(private router: Router, private usernameSvc: UsernameService) {}

  menuItems: MenuItem[] = [];
  menuItems2: MenuItem[] = [];

  ngOnInit(): void {
    this.currentDate = new Date();
    const formatter = new Intl.DateTimeFormat('en-SG', {
      weekday: 'short',
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
    this.formattedDate = formatter.format(this.currentDate);

    this.usernameSvc.userInfo$.subscribe(userInfo => {
      this.username = userInfo.username;
      this.id = userInfo.id;
      // console.info("Username, ID: ", this.username, this.id)
      setTimeout(() => {
        this.updateMenuItems(this.username, this.id);
      });
    })
  }

  updateMenuItems(username: string, id: string) {
        const currentUrl = this.router.url.toLowerCase();
        // console.info("Username, ID after login: ", username, id)
        if (currentUrl.includes('/login')) {
          this.menuItems = [
            { label: 'Home', link: '/login/' + username + '/' + id },
            { label: 'BMI Calculator', link: '/login/' + username + '/' + id + '/bmi' },
            { label: 'Calories Calculator', link: '/login/' + username + '/' + id + '/caloriecal' },
            { label: 'Calories Tracker', link: '/login/' + username + '/' + id + '/searchnutrition' },
            { label: 'Exercises', link: '/login/' + username + '/' + id + '/searchexercise' },
            { label: 'Update Profile', link: '/login/' + username + '/' + id + '/profile' },
            { label: 'Logout', action: this.logout }
          ];
          this.menuItems2 = [
            { label: 'Calories Tracker', link: '/login/' + username + '/' + id + '/searchnutrition' },
            { label: 'Exercises', link: '/login/' + username + '/' + id + '/searchexercise' },
            { label: 'Logout', action: this.logout }
          ];
        } 
        else {
          this.menuItems = [
            { label: 'Register an Account', link: '/register' }
          ];
          this.menuItems2 = [
            { label: 'Register an Account', link: '/register' }
          ];
        }
    };
  
  

  // updateMenuItems(username: string, id: string) {
  //   this.router.events.subscribe(event => {
  //     if (event instanceof NavigationEnd) {
  //       const currentUrl = event.url.toLowerCase();
  //       console.info("Current URL: ", currentUrl);
  
  //       if (currentUrl.includes('/login')) {
  //         this.menuItems = [
  //           { label: 'Home', link: '/login/' + username + '/' + id },
  //           { label: 'BMI Calculator', link: '/login/' + username + '/' + id + '/bmi' },
  //           { label: 'Calories Calculator', link: '/login/' + username + '/' + id + '/caloriecal' },
  //           { label: 'Calories Tracker', link: '/login/' + username + '/' + id + '/searchnutrition' },
  //           { label: 'Exercises', link: '/login/' + username + '/' + id + '/searchexercise' },
  //           { label: 'Update Profile', link: '/login/' + username + '/' + id + '/profile' },
  //           { label: 'Logout', action: this.logout }
  //         ];
  //       }
  //     }
  //   });
  // }

  // updateMenuItems(username: string, id: string) {
  //   // Dynamically update the menu items based on the current route or component
  //   // For example, you can check the current route URL and update the menu items accordingly
  //   const currentUrl = this.router.url.toLowerCase();
  //   console.info("Current URL: ", currentUrl)

  //   if (currentUrl.includes('/login')) {
  //     this.menuItems = [
  //       { label: 'Home', link: '/login/' + username + '/' + id },
  //       { label: 'BMI Calculator', link: '/login/' + username + '/' + id + '/bmi' },
  //       { label: 'Calories Calculator', link: '/login/' + username + '/' + id + '/caloriecal' },
  //       { label: 'Calories Tracker', link: '/login/' + username + '/' + id + '/searchnutrition' },
  //       { label: 'Exercises', link: '/login/' + username + '/' + id + '/searchexercise' },
  //       { label: 'Update Profile', link: '/login/' + username + '/' + id + '/profile' },
  //       { label: 'Logout', action: this.logout }
  //     ];
  //   }
  //   else {
  //     this.menuItems = [
  //       { label: 'Register an Account', link: '/register' }
  //     ];
  //   }
  // }
}
