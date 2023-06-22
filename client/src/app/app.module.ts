import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { RegisterComponent } from './components/register.component';
import { LoginComponent } from './components/login.component';
import { HttpClientModule } from '@angular/common/http';
import { RegisterSuccessComponent } from './components/register-success.component';
import { LoginHomeComponent } from './components/login-home.component';
import { BmiComponent } from './components/bmi.component';
import { CaloriesComponent } from './components/calories.component';
import { NutritionSearchComponent } from './components/nutrition-search.component';
import { DatePipe } from '@angular/common';
import { ConfirmDialogComponent } from './components/confirm-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { ExerciseComponent } from './components/exercise.component';
import { ExerciseCalendarComponent } from './components/exercise-calendar.component';
import { ProfileComponent } from './components/profile.component';
import { ServiceWorkerModule } from '@angular/service-worker';
import { NgChartsModule } from 'ng2-charts';

@NgModule({
  declarations: [
    AppComponent,
    RegisterComponent,
    LoginComponent,
    RegisterSuccessComponent,
    LoginHomeComponent,
    BmiComponent,
    CaloriesComponent,
    NutritionSearchComponent,
    ConfirmDialogComponent,
    ExerciseComponent,
    ExerciseCalendarComponent,
    ProfileComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatDialogModule,
    NgChartsModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    }),
  ],
  entryComponents: [ConfirmDialogComponent],
  providers: [DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
