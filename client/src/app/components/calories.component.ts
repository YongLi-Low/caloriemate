import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-calories',
  templateUrl: './calories.component.html',
  styleUrls: ['./calories.component.css']
})
export class CaloriesComponent implements OnInit {

  calForm!: FormGroup;
  // BMR
  result: number | null = null;
  id!: string;
  username!: string;

  constructor(private fb: FormBuilder, private activatedRoute: ActivatedRoute,
              private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.params['id'];
    this.username = this.activatedRoute.snapshot.params['username'];
    // console.info(">>> ID: ", this.id)
    this.calForm = this.createForm();
  }

  createForm() {
    return this.fb.group({
      gender: this.fb.control('', [Validators.required]),
      age: this.fb.control('', [Validators.required]),
      weight: this.fb.control('', [Validators.required]),
      height: this.fb.control('', [Validators.required]),
      activityLevel: this.fb.control('', [Validators.required])
    })
  }
  
  calculateCal() {
    const formValue = this.calForm.value;
    const gender = formValue['gender'];
    const age = formValue['age'];
    const weight = formValue['weight'];
    const height = formValue['height'];
    const activityLevel = formValue['activityLevel'];

    if (gender === 'male') {
      this.result = 66.5 + (13.75 * weight) + (5.003 * height) - (6.75 * age);
    }
    else {
      this.result = 665.1 + (9.563 * weight) + (1.85 * height) - (4.676 * age);
    }

    if (activityLevel === 'sedentary') {
      this.result *= 1.2;
    }
    else if (activityLevel === 'lightlyActive') {
      this.result *= 1.375;
    }
    else if (activityLevel === 'moderatelyActive') {
      this.result *= 1.55;
    }
    else if (activityLevel === 'veryActive') {
      this.result *= 1.725;
    }
    else if (activityLevel === 'superActive') {
      this.result *= 1.9;
    }

    this.result = Math.round(this.result);
    this.updateCal(this.id, this.result);
  }

  updateCal(id: string, cal: number) {
    lastValueFrom(this.httpClient.put<string>('/login/' + id + '/calories', cal))
      .then((response: any) => {
        console.info(response.response)
      })
      .catch(error => console.log(error))
  }
}
