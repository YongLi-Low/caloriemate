import { Component, OnInit } from '@angular/core';
import { MenuItem } from './models/MenuItem';
import { NavigationEnd, Router } from '@angular/router';
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

  constructor(private router: Router, private usernameSvc: UsernameService) {}

  menuItems: MenuItem[] = [
    { label: 'Register an Account', link: '/register' },
  ];

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
      setTimeout(() => {
        this.updateMenuItems(this.username, this.id);
        // console.info(">>> ID: ", this.id);
        // console.info(">>> Username: ", this.username);
      });
    })
  }

  updateMenuItems(username: string, id: string) {
    // Dynamically update the menu items based on the current route or component
    // For example, you can check the current route URL and update the menu items accordingly
    const currentUrl = this.router.url.toLowerCase();

    if (currentUrl.includes('/login')) {
      this.menuItems = [
        { label: 'Home', link: '/login/' + username + '/' + id },
        { label: 'Food Calories Tracker', link: '/login/' + username + '/' + id + '/searchnutrition' },
        { label: 'Calorie Calculator', link: '/login/' + username + '/' + id + '/caloriecal' },
        { label: 'BMI Calculator', link: '/login/' + username + '/' + id + '/bmi' },
        { label: 'Feedback', link: '/register' },
        { label: 'Logout', link: '' }
      ];
    }
  }

  logout() {

  }
}
