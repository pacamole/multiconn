import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface BalanceResponse {
  balance: number;
}

@Injectable({
  providedIn: 'root',
})
export class WalletService {
  private walletUrl: string = 'http://localhost:8080/api/wallet/balance';

  constructor(private http: HttpClient) {}

  public getBalance(): Observable<BalanceResponse> {
    return this.http.get<BalanceResponse>(this.walletUrl, { withCredentials: true });
  }
}
