import { AfterViewInit, Component, ElementRef, NgModule, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UsernameService } from '../services/username.service';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom } from 'rxjs';
import { NutritionService } from '../services/nutrition.service';
import { Calories } from '../models/Calories';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from './confirm-dialog.component';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-login-home',
  templateUrl: './login-home.component.html',
  styleUrls: ['./login-home.component.css']
})
export class LoginHomeComponent implements OnInit {

  dateForm!: FormGroup;
  maxFormDate!: string;
  selectedDate!: string;
  username!: string;
  id!: string;
  bmi!: number | null;
  calories!: number | null;
  caloriesList!: Calories[];
  currentDate!: string
  dateList: string[] = [];
  totalCalories!: number;
  totalCaloriesList: number[] = []; // List of calories for each of the past 7 days
  caloriesLeft!: number;
  caloriesExceeded: boolean = false;
  // Retrieving profile pic
  profileImage: string | undefined;
  noImageAvailable: boolean = false;

  chart!: any;

  constructor(private activatedRoute: ActivatedRoute, private usernameSvc: UsernameService,
              private httpClient: HttpClient, private nutritionSvc: NutritionService,
              private datePipe: DatePipe, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.username = this.activatedRoute.snapshot.params['username']
    this.id = this.activatedRoute.snapshot.params['id']
    this.usernameSvc.setUserInfo(this.username, this.id)
    this.selectedDate = this.getCurrentDateInMySQLFormat();
    this.currentDate = this.getCurrentDateInMySQLFormat();
    // console.info("Current Date: ", this.currentDate)
    // Get profile pic
    this.getProfile();

    // Date Form, then get the list of calories after the date is selected
    this.dateForm = new FormGroup({
      selectedDate: new FormControl(this.selectedDate, [Validators.required])
    })
    this.maxFormDate = this.getCurrentDateInMySQLFormat();
    this.dateForm.get('selectedDate')?.valueChanges.subscribe(
      date => {
        if (date !== null) {
          this.selectedDate = this.datePipe.transform(date, 'yyyy-MM-dd') || '';
          // console.info(">>> Selected Date: ", this.selectedDate)
          this.nutritionSvc.getCalories(this.id, this.selectedDate)
            .then((response: any) => {
              this.caloriesList = response;
              // console.info(">>> Response: ", response)
              this.totalCalories = 0;
              this.caloriesList.forEach((calories: Calories) => {
                this.totalCalories += calories.calories;
              })
              // Calculate the calories left for the day
              this.calculateCaloriesLeft();
            })
            .catch(error => {
              console.error(error);
            })
        }
      }
    )

    // Get a list of past 7 days (dates)
    const startDate = new Date(this.currentDate);
    for (let i = 6; i >= 0; i--) {
      const date = new Date(startDate);
      date.setDate(date.getDate() - i);
      const formattedDate = date.toISOString().split('T')[0];

      this.dateList.push(formattedDate);
      // console.info("Past 7 days: ", date)
    }
    // console.info("Date List: ", this.dateList);

    // Get a list of totalCalories for each day for the past 7 days
    this.calculateTotalCalories()
    .then(() => {
      // console.info("Total Calories List: ", this.totalCaloriesList);
      this.createChart(this.dateList, this.totalCaloriesList);
    })
    .catch(error => {
      console.error(error);
    });
    
    // Calculate caloriesLeft and set caloriesExceeded
    if (this.calories != null) {
      if (this.caloriesLeft !== null && this.caloriesLeft < 0 && this.caloriesLeft * -1 > this.calories) {
        this.caloriesExceeded = true;
      }
    }

    // Get statement to fetch the list of food and calories eaten
    this.nutritionSvc.getCalories(this.id, this.currentDate)
      .then((response: any) => {
        this.caloriesList = response;
        // console.info(">>> Response: ", response)
        this.totalCalories = 0;
        this.caloriesList.forEach((calories: Calories) => {
          this.totalCalories += calories.calories;
        })
        // console.info(">>> Total calories: ", this.totalCalories);
      })
      .catch(error => {
        console.error(error);
      })

    // Get statement to fetch the BMI data
    lastValueFrom(this.httpClient.get<number>('/login/'+ this.id + '/getbmi'))
      .then((response: any) => {
        let bmiString: string = response.response;
        if (bmiString !== 'null') {
          this.bmi = parseFloat(bmiString);
        }
        else {
          this.bmi = null;
        }
        // having this method ensure that they are executed only after the respective HTTP requests complete and the values are updated.
        this.processBmi();
      })
      .catch(error => console.log(error))

    // Get statement to fetch calories data
    lastValueFrom(this.httpClient.get<number>('/login/' + this.id + '/getcalories'))
      .then((response: any) => {
        let caloriesString: string = response.response;
        if (caloriesString !== 'null') {
          this.calories = parseInt(caloriesString);
          // Calculate the calories left for the day
          this.calculateCaloriesLeft();
        }
        else {
          this.calories = null;
        }
        // having this method ensure that they are executed only after the respective HTTP requests complete and the values are updated.
        this.processCalories();
      })
      .catch(error => console.log(error))

    // console.info(">>> ID: ", this.id)
  }

  getCurrentDateInMySQLFormat(): string {
    const date = new Date();
    return this.datePipe.transform(date, 'yyyy-MM-dd') || '';
  }

  processBmi() {
    // console.info(">>> BMI: ", this.bmi);
  }

  processCalories() {
    // console.info(">>> Calories: ", this.calories);
  }

  calculateCaloriesLeft() {
    // @ts-ignore
    this.caloriesLeft = this.calories - this.totalCalories;
  }

  // Progress bar of calories consumed
  calculateProgress(): number {
    let progress: number = 0;
    if (this.calories !== null) {
      progress = (this.totalCalories / this.calories) * 100;
    }
    return Math.min(progress, 100)
  }

  confirmDeleteDialog(id: number) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '250px',
      data: {message: 'Are you sure you want to delete this item?'}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result == true) {
        this.nutritionSvc.deleteCalories(id)
          .then((response: any) => {
            // console.info(">>> Deleted: ", response)
            // Fetch the list of food and calories again
            this.fetchCaloriesList();
          })
          .catch(error => {
            console.error(error)
          });
        console.info("Delete confirmed")
      }
      else {
        console.info("Delete cancelled")
      }
    })
  }

  fetchCaloriesList() {
    this.nutritionSvc.getCalories(this.id, this.currentDate)
      .then((response: any) => {
        this.caloriesList = response;
        console.info(">>> Response: ", response);
      })
      .catch(error => {
        console.error(error);
      });
  }

  // Get profile pic
  async getProfile(): Promise<void> {

    try {
      const response = await lastValueFrom(this.usernameSvc.getProfile(this.id));
      if (response) {
        this.profileImage = 'data:image/jpeg;base64,' + response;
        this.noImageAvailable = false;
      }
      else {
        this.noImageAvailable = true;
      }
      // console.info(">>> Response: ", response);
    } 
    catch (error) {
      console.error('Failed to retrieve profile image:', error);
    }
  }

  // Get a list of totalCalories for each day for the past 7 days
  async calculateTotalCalories(): Promise<void> {
    let totalCaloriesByDate: { [date: string]: number } = {};
  
    const promises = this.dateList.map(date => {
      return this.nutritionSvc.getCalories(this.id, date)
        .then((response: any) => {
          const caloriesList = response;
          let totalCalories = 0;
          caloriesList.forEach((calories: Calories) => {
            totalCalories += calories.calories;
          });
          totalCaloriesByDate[date] = totalCalories;
        });
    });
  
    return Promise.all(promises)
      .then(() => {
        // Create totalCaloriesList from totalCaloriesByDate, in the order of dateList
        this.totalCaloriesList = this.dateList.map(date => totalCaloriesByDate[date]);
        // console.info("Calories List: ", this.totalCaloriesList)
      });
  }
  
  // async calculateTotalCalories(): Promise<void> {
  //   const promises = this.dateList.map(date => {
  //     return this.nutritionSvc.getCalories(this.id, date)
  //       .then((response: any) => {
  //         const caloriesList = response;
  //         let totalCalories = 0;
  //         caloriesList.forEach((calories: Calories) => {
  //           totalCalories += calories.calories;
  //         });
  //         this.totalCaloriesList.push(totalCalories);
  //         console.info("Calories List: ", this.totalCaloriesList)
  //       });
  //   });
  
  //   return Promise.all(promises).then(() => {});
  // }

  createChart(dateList: string[], totalCaloriesList: number[]) {
    this.chart = new Chart("MyChart", {
      type: 'line',
      data: {  //values on x axis
        labels: dateList,
        datasets: [
          {label: "Calories",
          data: totalCaloriesList,
          backgroundColor: 'blue'
          }
        ]
      }//,
      // options: {
      //   aspectRatio: 1
      // }
    })
  };
}
