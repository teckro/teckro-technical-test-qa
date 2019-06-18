import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BookingService } from './booking.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  constructor(
    private http: HttpClient,
    private bookingService: BookingService
  ) {

  }
  checkAvailabiltyDate = new FormControl('')
  checkInDate = new FormControl('');
  numOfDays = new FormControl();
  bookingResponse: any;
  roomAvailabilityResponse: any;

  check() {
    this.bookingService.checkAvailability(this.checkAvailabiltyDate.value)
      .subscribe(
        (result: any) => this.roomAvailabilityResponse = result,
        (err: any) => this.roomAvailabilityResponse = err
      );
  }

  book() {
    const payload = {
      checkInDate: this.checkInDate.value,
      numOfDays: this.numOfDays.value
    }
    this.bookingService.bookRoom(payload)
      .subscribe(
        (result: any) => this.bookingResponse = result,
        (err: any) => this.bookingResponse = err
      );
  }
}
