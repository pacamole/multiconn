import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface CheckoutResponse {
  url: string;
}

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  private apiUrl: string = 'http://localhost:8080/api/payments/checkout';

  constructor(private http: HttpClient) {}

  public createCheckoutSession(amount: number): Observable<CheckoutResponse> {
    const payload = { amount: amount };

    return this.http.post<CheckoutResponse>(this.apiUrl, payload, { withCredentials: true });
  }
}
