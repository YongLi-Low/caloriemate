import { DatePipe, Location } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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

  constructor(private activatedRoute: ActivatedRoute, private fb: FormBuilder,
              private location: Location, private datePipe: DatePipe,
              private httpClient: HttpClient, private snackBar: MatSnackBar) {};
              
  ngOnInit(): void {
    this.username = this.activatedRoute.snapshot.params['username'];
    this.exerciseName = this.activatedRoute.snapshot.params['exercise'];
    this.addExerciseForm = this.createForm();
  }

  createForm() {
    return this.fb.group({
      title: this.fb.control(this.exerciseName, [Validators.required]),
      description: this.fb.control(''),
      startDateTime: this.fb.control('', [Validators.required]),
      endDateTime: this.fb.control('', [Validators.required]),
      username: this.fb.control(this.username)
    })
  }

  schedule() {
    const formData = this.addExerciseForm.value;
    const originalStartDateTime = this.addExerciseForm.value.startDateTime;
    const originalEndDateTime = this.addExerciseForm.value.endDateTime;
    const startDateTime = this.datePipe.transform(originalStartDateTime, 'yyyy-MM-ddTHH:mm:ss+08:00');
    const endDateTime = this.datePipe.transform(originalEndDateTime, 'yyyy-MM-ddTHH:mm:ss+08:00');
    formData.startDateTime = startDateTime;
    formData.endDateTime = endDateTime;

    lastValueFrom(this.httpClient.post<string>('/api/exercises/events', formData))
      .then((response: any) => {
        this.showSnackBar('Added to Calendar', 'success');
        // console.info(response.response);
        // const eventLink = response.eventLink;
        // console.log('Event link:', eventLink);
      })
      .catch(error => {
        {
          console.log('Error creating event:', error);
          this.showSnackBar('Failed to add to Calendar', 'error');
        }}
      )
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
