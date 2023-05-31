import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { Exercise } from '../models/Exercise';

@Injectable({
  providedIn: 'root'
})
export class ExerciseService {

  private EXERCISE_API_URL = '/api/exercises';

  private headers = new HttpHeaders()
        .set("Content-Type", "application/json; charset=utf-8")

  constructor(private httpClient: HttpClient) {}

  // Find the exercises from api ninjas
  getExercise(name: string, type: string, muscle: string, difficulty: string): Promise<any> {
    let params = new HttpParams()
      .set('name', name.replaceAll(' ', '+'))
      .set('type', type)
      .set('muscle', muscle)
      .set('difficulty', difficulty);

    return lastValueFrom(this.httpClient.get<Exercise[]>(this.EXERCISE_API_URL, {params: params, headers: this.headers}));
  }
}
