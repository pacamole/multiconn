import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root"
})
export class AuthService {
  private readonly GOOGLE_AUTH_URL = "http://localhost:8080/oauth2/authorization/google";

  constructor() {}

  public loginWithGoogle(): void {
    window.location.href = this.GOOGLE_AUTH_URL;
  }
}
