import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Nutrition } from '../models/Nutrition';
import { lastValueFrom } from 'rxjs';
import { Calories } from '../models/Calories';

@Injectable({
  providedIn: 'root'
})
export class NutritionService {

  private NUTRITION_API_URL = "/api/nutrition";
  
  private headers = new HttpHeaders()
        .set("Content-Type", "application/json; charset=utf-8")

  constructor(private httpClient: HttpClient) { }

  // Find the food nutrition details from API Ninjas
  getNutrition(foodName: string, quantity: string = '100'): Promise<any> {

    // send these params to the controller in spring boot. Spring Boot will handle the formatting.
    let params = new HttpParams()
      .set('foodName', foodName.replaceAll(' ', '+'))
      .set('quantity', quantity);

    return lastValueFrom(this.httpClient.get<Nutrition[]>(this.NUTRITION_API_URL, {params: params, headers: this.headers}))
  }

  postCalories(calories: Calories) {
    return lastValueFrom(this.httpClient.post<string>(this.NUTRITION_API_URL + '/insertfoodcalories', calories));
  }

  // Find the food, calories and entryDate from caloriestracker table
  getCalories(id: string, entryDate: string): Promise<any> {
    let params = new HttpParams()
      .set('id', id)
      .set('entryDate', entryDate);
    
    return lastValueFrom(this.httpClient.get<Calories[]>(this.NUTRITION_API_URL + '/findfoodcalories', {params: params, headers: this.headers}))
  }

  // delete the food and calories from the user's list
  deleteCalories(id: number): Promise<any> {
    let params = new HttpParams()
      .set('id', id);

    return lastValueFrom(this.httpClient.delete<string>(this.NUTRITION_API_URL + '/deletefoodcalories', {params: params, headers: this.headers}))
  }
}
