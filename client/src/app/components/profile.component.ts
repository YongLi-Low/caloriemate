import { HttpClient } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { UsernameService } from '../services/username.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  @Input()
  requiredFileType!: string;

  fileName = '';
  id!: string;
  profileImage: string | undefined;
  noImageAvailable: boolean = false;

  constructor(private httpClient: HttpClient, private activatedRoute: ActivatedRoute,
              private snackBar: MatSnackBar, private usernameSvc: UsernameService) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.params['id'];
    this.getProfile();
  }

  onFileSelected(event: any) {
    const file:File = event.target.files[0];

    if (file) {
      this.fileName = file.name;
      const formData = new FormData();
      formData.append("thumbnail", file);

      lastValueFrom(this.httpClient.put<string>("/profile/" + this.id, formData))
        .then((response: any) => {
          // console.info(response.response)
          this.showSnackbar('Image uploaded successfully', 'Close', 5000);
          this.resetForm();
          this.getProfile();
        })
        .catch(error => console.log(error.response))
    }
  }

  resetForm() {
    // Reset the file input element
    const fileInput: HTMLInputElement | null = document.querySelector('.file-input');
    if (fileInput) {
      fileInput.value = '';
    }
  
    // Clear the fileName variable
    this.fileName = '';
  }

  showSnackbar(message: string, action: string, duration: number) {
    this.snackBar.open(message, action, { duration: duration });
  }

  async getProfile(): Promise<void> {

    try {
      const response = await lastValueFrom(this.usernameSvc.getProfile(this.id));
      if (response) {
        this.profileImage = 'data:image/jpeg;base64,' + response;
        this.noImageAvailable = false;
      }
      else {
        this.noImageAvailable = true;
      }
      // console.info(">>> Response: ", response);
    } 
    catch (error) {
      console.error('Failed to retrieve profile image:', error);
    }
  }
}