import { Component, NgModule, OnInit } from '@angular/core';
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
import { MatDatepickerInput, MatDatepickerInputEvent } from '@angular/material/datepicker';

@Component({
  selector: 'app-login-home',
  templateUrl: './login-home.component.html',
  styleUrls: ['./login-home.component.css']
})
export class LoginHomeComponent implements OnInit{

  dateForm!: FormGroup;
  maxFormDate!: string;
  selectedDate!: string;
  username!: string;
  id!: string;
  bmi!: number | null;
  calories!: number | null;
  caloriesList!: Calories[];
  currentDate!: string
  totalCalories!: number;
  caloriesLeft!: number;

  constructor(private activatedRoute: ActivatedRoute, private usernameSvc: UsernameService,
              private httpClient: HttpClient, private nutritionSvc: NutritionService,
              private datePipe: DatePipe, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.username = this.activatedRoute.snapshot.params['username']
    this.id = this.activatedRoute.snapshot.params['id']
    this.usernameSvc.setUserInfo(this.username, this.id)
    this.selectedDate = this.getCurrentDateInMySQLFormat();
    this.currentDate = this.getCurrentDateInMySQLFormat();

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
  
  
  
  
  
  
  

}
