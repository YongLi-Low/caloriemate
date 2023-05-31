import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ExerciseService } from '../services/exercise.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Exercise } from '../models/Exercise';

@Component({
  selector: 'app-exercise',
  templateUrl: './exercise.component.html',
  styleUrls: ['./exercise.component.css']
})
export class ExerciseComponent implements OnInit{

  exerciseForm!: FormGroup
  exerciseData: Exercise[] = [];
  equipment!: string;
  showNoResults: boolean = false;
  isSearching: boolean = false;
  id!: string;
  username!: string;

  constructor(private fb: FormBuilder, private exerciseSvc: ExerciseService,
              private router: Router, private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.params['id'];
    this.username = this.activatedRoute.snapshot.params['username'];
    this.exerciseForm = this.createForm();
  }

  createForm() {
    return this.fb.group({
      name: this.fb.control(''),
      type: this.fb.control(''),
      muscle: this.fb.control(''),
      difficulty: this.fb.control('')
    })
  }

  search() {
    const formValue = this.exerciseForm.value;
    const name = formValue['name'];
    const type = formValue['type'];
    const muscle = formValue['muscle'];
    const difficulty = formValue['difficulty'];
    this.isSearching = true;
    this.exerciseSvc.getExercise(name, type, muscle, difficulty)
      .then((response: any) => {
        this.exerciseData = response;
        for (const exercise of this.exerciseData) {
          this.equipment = exercise.equipment.replaceAll('_', ' ');
          exercise.equipment = this.equipment;
        }
        this.showNoResults = this.exerciseData.length === 0;
        // console.info(">>> Exercises: ", response)
        this.isSearching = false;
      })
      .catch (error => {
        console.error(error);
        this.showNoResults = true;
      })
  }

  goBack() {
    this.router.navigate(['login', this.username, this.id]);
  }
}
