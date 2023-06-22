import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NutritionService } from '../services/nutrition.service';
import { Nutrition } from '../models/Nutrition';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Calories } from '../models/Calories';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-nutrition-search',
  templateUrl: './nutrition-search.component.html',
  styleUrls: ['./nutrition-search.component.css']
})
export class NutritionSearchComponent implements OnInit {

  nutritionForm!: FormGroup;
  nutritionData: Nutrition[] = [];
  caloriesData!: Calories;
  showNoResults: boolean = false;
  id!: string;
  username!: string;
  currentDate!: string;
  isSearching: boolean = false;
  // Custom Form
  customForm!: FormGroup;
  maxFormDate!: string;
  selectedDate!: string;

  constructor(private fb: FormBuilder, private nutritionSvc: NutritionService,
              private router: Router, private activatedRoute: ActivatedRoute,
              private datePipe: DatePipe, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.params['id'];
    this.username = this.activatedRoute.snapshot.params['username'];
    this.nutritionForm = this.createForm();
    // get the current date
    this.currentDate = this.getCurrentDateInMySQLFormat();

    // Custom Form
    this.selectedDate = this.getCurrentDateInMySQLFormat();
    this.customForm = this.createCustomForm();
  }

  createForm() {
    return this.fb.group({
      foodName: this.fb.control('', [Validators.required, Validators.minLength(2)]),
      quantity: this.fb.control('')
    })
  }

  createCustomForm() {
    return this.fb.group({
      foodName: this.fb.control('', [Validators.required, Validators.minLength(2)]),
      quantity: this.fb.control('', [Validators.required]),
      calories: this.fb.control('', [Validators.required]),
      selectedDate:  this.fb.control(this.selectedDate, [Validators.required])
    })
  }

  // Searching for the food
  search() {
    const formValue = this.nutritionForm.value;
    const foodName = formValue['foodName'];
    const quantity = formValue['quantity'];
    this.isSearching = true;
    this.nutritionSvc.getNutrition(foodName, quantity)
      .then((response: any) => {
        this.nutritionData = response;
        this.showNoResults = this.nutritionData.length === 0;
        // console.info(">>> Response: ", response)
        this.isSearching = false;
      })
      .catch(error => {
        console.error(error);
        this.showNoResults = true;
      });
  }

  // Posting the calories to spring boot (then to SQL)
  addCalories(n: Nutrition) {
    this.caloriesData = {} as Calories;
    this.caloriesData.foreignId = this.id;
    this.caloriesData.username = this.username;
    this.caloriesData.foodName = n.name;
    this.caloriesData.quantity = n.servingSize;
    this.caloriesData.calories = n.calories;
    this.caloriesData.entryDate = this.currentDate;
    this.nutritionSvc.postCalories(this.caloriesData)
      .then((response:any) => {
        // console.info(">>>> Response: ", response);
        this.showSnackBar('Food added successfully', 'success');
        this.resetForm(this.nutritionForm);
        this.nutritionData = []; // clear the search results
      })
      .catch(error => {
        console.error(error);
        this.showSnackBar('Fail to add food', 'failure');
      })
  }

  // Posting the calories to spring boot (then to SQL)
  addCustomFood() {
    const formValue = this.customForm.value;
    this.caloriesData = {} as Calories;
    this.caloriesData.foreignId = this.id;
    this.caloriesData.username = this.username;
    this.caloriesData.foodName = formValue['foodName'];
    this.caloriesData.quantity = formValue['quantity'];
    this.caloriesData.calories = formValue['calories'];
    this.selectedDate = formValue['selectedDate'];
    this.selectedDate = this.datePipe.transform(this.selectedDate, 'yyyy-MM-dd') || '';
    this.caloriesData.entryDate = this.selectedDate;
    this.nutritionSvc.postCalories(this.caloriesData)
      .then((response: any) => {
        this.showSnackBar('Food added successfully', 'success');
        this.resetForm(this.customForm);
      })
      .catch(error => {
        console.error(error);
        this.showSnackBar('Fail to add food', 'failure');
      })
  }

  goBack() {
    this.router.navigate(['login', this.username, this.id])
  }

  getCurrentDateInMySQLFormat(): string {
    const date = new Date();
    return this.datePipe.transform(date, 'yyyy-MM-dd') || '';
  }

  showSnackBar(message: string, panelClass: string) {
    this.snackBar.open(message, 'Close', {
      duration: 5000, panelClass: panelClass
    });
  }

  resetForm(form: FormGroup) {
    form.reset();
    form.markAsPristine();
    form.markAsUntouched();
  }
}
