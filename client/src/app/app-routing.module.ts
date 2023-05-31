import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login.component';
import { RegisterComponent } from './components/register.component';
import { RegisterSuccessComponent } from './components/register-success.component';
import { LoginHomeComponent } from './components/login-home.component';
import { BmiComponent } from './components/bmi.component';
import { CaloriesComponent } from './components/calories.component';
import { NutritionSearchComponent } from './components/nutrition-search.component';
import { ExerciseComponent } from './components/exercise.component';

const routes: Routes = [
  {path:"", component:LoginComponent},
  {path:"register", component:RegisterComponent},
  {path:"register/success", component:RegisterSuccessComponent},
  {path:"login/:username/:id", component:LoginHomeComponent},
  {path:"login/:username/:id/caloriecal", component:CaloriesComponent},
  {path:"login/:username/:id/bmi", component:BmiComponent},
  {path:"login/:username/:id/searchnutrition", component:NutritionSearchComponent},
  {path:"login/:username/:id/searchexercise", component:ExerciseComponent},
  {path:"**", redirectTo:"", pathMatch:"full"}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
