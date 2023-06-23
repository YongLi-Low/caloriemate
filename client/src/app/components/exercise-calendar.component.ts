import { DatePipe, Location } from '@angular/common';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-exercise-calendar',
  templateUrl: './exercise-calendar.component.html',
  styleUrls: ['./exercise-calendar.component.css']
})
export class ExerciseCalendarComponent implements OnInit {

  exerciseName!: string;
  addExerciseForm!: FormGroup;
  authenticationLink!: string;
  username!: string;
  id!: string;

  constructor(private activatedRoute: ActivatedRoute, private fb: FormBuilder,
              private location: Location, private datePipe: DatePipe,
              private httpClient: HttpClient, private snackBar: MatSnackBar) {};
              
  ngOnInit(): void {
    this.username = this.activatedRoute.snapshot.params['username'];
    this.id = this.activatedRoute.snapshot.params['id'];
    this.exerciseName = this.activatedRoute.snapshot.params['exercise'];
    this.addExerciseForm = this.createForm();
  }

  createForm() {
    return this.fb.group({
      title: this.fb.control(this.exerciseName, [Validators.required]),
      description: this.fb.control(''),
      startDateTime: this.fb.control('', [Validators.required]),
      endDateTime: this.fb.control('', [Validators.required]),
      username: this.fb.control(this.username),
      code: this.fb.control('')
    })
  }

  getAuthorizationUrl(username: string, id: string, eventData: any): Promise<any> {
    const params = new HttpParams()
      .set("username", username)
      .set("id", id)
      .set("eventData", JSON.stringify(eventData));

    return lastValueFrom(this.httpClient.get<any>("/api/exercises/events/url", { params: params }));
  }

  schedule() {
    const formData = this.addExerciseForm.value;
    const originalStartDateTime = this.addExerciseForm.value.startDateTime;
    const originalEndDateTime = this.addExerciseForm.value.endDateTime;
    const startDateTime = this.datePipe.transform(originalStartDateTime, 'yyyy-MM-ddTHH:mm:ss+08:00');
    const endDateTime = this.datePipe.transform(originalEndDateTime, 'yyyy-MM-ddTHH:mm:ss+08:00');
    formData.startDateTime = startDateTime;
    formData.endDateTime = endDateTime;
    formData.code = " ";

    this.getAuthorizationUrl(this.username, this.id, formData)
      .then((result: any) => {
        if (result.response != "credential exists") {
          window.location.href = result.response;
        }
        lastValueFrom(this.httpClient.post<string>('/api/exercises/events', formData))
          .then((response: any) => {
            if (response.response == 'Event created successfully') {
              this.showSnackBar('Added to Calendar', 'success');
              // console.info(response.response);
            }
          })
          .catch(error => {
            {
              console.log('Error creating event:', error);
              this.showSnackBar('Failed to add to Calendar', 'error');
            }}
          )
      })

    // lastValueFrom(this.httpClient.post<string>('/api/exercises/events', formData))
    //   .then((response: any) => {
    //     if (response.response == 'Event created successfully') {
    //       this.showSnackBar('Added to Calendar', 'success');
    //       // console.info(response.response);
    //     }
    //   })
    //   .catch(error => {
    //     {
    //       console.log('Error creating event:', error);
    //       this.showSnackBar('Failed to add to Calendar', 'error');
    //     }}
    //   )
  }

  showSnackBar(message: string, panelClass: string) {
    this.snackBar.open(message, 'Close', {
      duration: 5000, panelClass: panelClass
    });
  }

  goBack() {
    this.location.back();
  }

  
}
