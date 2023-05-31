import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-bmi',
  templateUrl: './bmi.component.html',
  styleUrls: ['./bmi.component.css']
})
export class BmiComponent implements OnInit{

  bmiForm!: FormGroup;
  result: number | null = null;
  id!: string;

  constructor(private fb: FormBuilder, private activatedRoute: ActivatedRoute,
              private httpClient: HttpClient) { }

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.params['id'];
    // console.info(">>> ID: ", this.id);
    this.bmiForm = this.createForm();
  }

  createForm() {
    return this.fb.group({
      weight: this.fb.control('', [Validators.required]),
      height: this.fb.control('', [Validators.required])
    })
  }

  calculateBmi() {
    const formValue = this.bmiForm.value;
    const height = formValue['height']
    const weight = formValue['weight']

    const heightInMetres = height / 100;
    const bmi = weight / (heightInMetres * heightInMetres);

    this.result = parseFloat(bmi.toFixed(1));
    this.updateBmi(this.id, this.result);
  }

  updateBmi(id: string, bmi: number) {

    lastValueFrom(this.httpClient.put<string>('/login/' + id + '/bmi', bmi))
      .then((response: any) => {
        console.info(response.response)
      })
      .catch(error => console.log(error))
  }
}
