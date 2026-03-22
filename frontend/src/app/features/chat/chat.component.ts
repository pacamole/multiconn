import { ChangeDetectorRef, Component, Inject, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { PaymentService } from '../../core/services/payment.service';
import { WalletService } from '../../core/services/wallet.service';
import { CommonModule, isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="chat-dashboard">
      <header class="chat-header">
        <h2>Multi-Agent Workspace</h2>

        <div class="wallet-section">
          <span class="balanc-badge">Saldo {{ balance | currency: 'USD' }}</span>
          <button class="buy-credits-btn" (click)="buyCredits(10)">Comprar $10 Créditos</button>
        </div>
      </header>

      <main class="chat-area">
        @if (balance === 0) {
          <p class="empty-state">
            Seus Agentes de IA estão dormindo. Compre créditos para acordá-los!
          </p>
        }
        @if (balance > 0) {
          <p class="active-state">
            Seus Agentes de IA estão prontos! Como eles te ajudarão dessa vez?
          </p>
        }
      </main>
    </div>
  `,
  styleUrl: './chat.component.css',
})
export class ChatComponent implements OnInit {
  public balance: number = 0;
  private isBrowser: boolean;

  constructor(
    private paymentService: PaymentService,
    private walletService: WalletService,
    @Inject(PLATFORM_ID) platformId: Object,
    private cdr: ChangeDetectorRef,
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  ngOnInit(): void {
    if (this.isBrowser) {
      this.fetchBalance();
    }
  }

  public buyCredits(amount: number): void {
    this.paymentService.createCheckoutSession(amount).subscribe({
      next: (response) => {
        window.location.href = response.url;
      },
      error: (err) => {
        console.error('Failed to create checkout session:', err);
        alert('Não foi possível iniciar o pagamento. Por favor, tente novamente.');
      },
    });
  }

  public fetchBalance(): void {
    this.walletService.getBalance().subscribe({
      next: (response) => {
        this.balance = response.balance;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Falha ao buscar o saldo:', err);
      },
    });
  }
}
