import { Component, EventEmitter, Output, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { PatientService } from '../../../services/patient.service';

@Component({
  selector: 'app-user-create-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    DropdownModule,
    ToastModule
  ],
  template: `
    <p-dialog 
      header="Add New Profile" 
      [visible]="visible()" 
      (visibleChange)="onVisibleChange($event)"
      [modal]="true" 
      [style]="{ width: '450px' }" 
      [draggable]="false"
      [resizable]="false"
      styleClass="create-user-dialog"
    >
      <div class="dialog-content">
        <div class="input-group">
          <label for="role">User Role</label>
          <p-dropdown 
            id="role" 
            [options]="roles" 
            [(ngModel)]="user.role" 
            placeholder="Select a role"
            styleClass="w-full"
          />
        </div>

        <div class="input-row">
          <div class="input-group">
            <label for="firstName">First Name</label>
            <input 
              pInputText 
              id="firstName" 
              [(ngModel)]="user.firstName" 
              placeholder="e.g. John"
            />
          </div>
          <div class="input-group">
            <label for="lastName">Last Name</label>
            <input 
              pInputText 
              id="lastName" 
              [(ngModel)]="user.lastName" 
              placeholder="e.g. Doe"
            />
          </div>
        </div>

        <ng-container *ngIf="user.role === 'Patient'">
          <div class="input-group">
            <label for="age">Age</label>
            <p-inputNumber 
              id="age" 
              [(ngModel)]="user.age" 
              [min]="0" 
              [max]="150"
              placeholder="Enter age"
            />
          </div>
        </ng-container>

        <ng-container *ngIf="user.role === 'Caregiver'">
          <div class="input-group">
            <label for="email">Contact Email</label>
            <input 
              pInputText 
              id="email" 
              [(ngModel)]="user.email" 
              placeholder="caregiver@example.com"
            />
          </div>
        </ng-container>
      </div>

      <ng-template pTemplate="footer">
        <div class="dialog-footer">
          <p-button 
            label="Cancel" 
            [text]="true" 
            severity="secondary"
            (onClick)="onClose()" 
          />
          <p-button 
            label="Create Profile" 
            icon="pi pi-user-plus" 
            [loading]="loading()" 
            (onClick)="onCreate()" 
            [disabled]="!isValid()"
          />
        </div>
      </ng-template>
    </p-dialog>
  `,
  styles: [`
    .dialog-content {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
      padding: 0.5rem;
    }

    .input-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1.25rem;
    }

    .input-group {
      display: flex;
      flex-direction: column;
      gap: 0.4rem;

      label {
        font-weight: 600;
        font-size: 0.875rem;
        color: #000;
        padding-left: 0.1rem;
      }

      ::ng-deep .p-inputtext, 
      ::ng-deep .p-dropdown,
      ::ng-deep .p-inputnumber-input {
        width: 100% !important;
        background: #ffffff !important;
        border: 1px solid #e2e2e2 !important;
        border-radius: 6px !important; /* Sharper corners like Shadcn */
        padding: 0.6rem 0.75rem !important;
        font-size: 0.875rem !important;
        color: #000 !important;
        box-shadow: none !important;
        transition: border-color 0.15s ease;

        &:focus {
          border-color: #000 !important;
          outline: none !important;
        }
      }

      ::ng-deep .p-dropdown {
          padding: 0 !important;
          .p-dropdown-label { padding: 0.6rem 0.75rem !important; }
      }
    }

    /* SHADCN / VERCEL DIALOG OVERRIDE */
    ::ng-deep .create-user-dialog {
        &.p-dialog-mask {
            background: rgba(0, 0, 0, 0.4) !important;
            backdrop-filter: blur(4px) !important;
        }

        .p-dialog {
            background: #ffffff !important;
            border: 1px solid #eaeaea !important;
            border-radius: 8px !important;
            box-shadow: 0 30px 60px rgba(0,0,0,0.12) !important;
        }
        
        .p-dialog-header {
            background: #ffffff !important;
            padding: 1.5rem 1.5rem 0.5rem !important;
            border-bottom: none !important;
            
            .p-dialog-title {
                font-weight: 700 !important;
                font-size: 1.25rem !important;
                color: #000 !important;
                letter-spacing: -0.02em !important;
            }

            .p-dialog-header-icons .p-dialog-header-close {
                color: #666 !important;
                &:hover { color: #000 !important; background: #f5f5f5 !important; }
            }
        }
        
        .p-dialog-content {
            background: #ffffff !important;
            padding: 1rem 1.5rem 1.5rem !important;
        }
        
        .p-dialog-footer {
            background: #fafafa !important;
            padding: 1rem 1.5rem !important;
            border-top: 1px solid #eaeaea !important;
            border-bottom-left-radius: 8px !important;
            border-bottom-right-radius: 8px !important;
            
            .p-button.p-button-text {
                color: #666 !important;
                font-weight: 500 !important;
                &:hover { color: #000 !important; }
            }

            .p-button:not(.p-button-text) {
                background: #000 !important; /* Vercel Black */
                color: #fff !important;
                border: 1px solid #000 !important;
                border-radius: 6px !important;
                padding: 0.5rem 1rem !important;
                font-weight: 500 !important;
                font-size: 0.875rem !important;
                transition: all 0.2s;
                
                &:hover {
                    background: #fff !important;
                    color: #000 !important;
                }
            }
        }
    }
  `]
})
export class UserCreateDialogComponent {
  @Output() closed = new EventEmitter<void>();

  visible = signal(false);
  loading = signal(false);

  roles = [
    { label: 'Patient', value: 'Patient' },
    { label: 'Caregiver', value: 'Caregiver' }
  ];

  user = {
    role: 'Patient',
    firstName: '',
    lastName: '',
    age: null as number | null,
    email: ''
  };

  private patientService = inject(PatientService);
  private messageService = inject(MessageService);

  show() {
    console.log('[Dialog] show() called');
    this.user = {
      role: 'Patient',
      firstName: '',
      lastName: '',
      age: null,
      email: ''
    };
    this.visible.set(true);
    console.log('[Dialog] visible signal set to true');
  }

  onVisibleChange(val: boolean) {
    this.visible.set(val);
    if (!val) {
      this.closed.emit();
    }
  }

  onClose() {
    this.onVisibleChange(false);
  }

  isValid() {
    if (!this.user.firstName || !this.user.lastName) return false;
    if (this.user.role === 'Patient' && (this.user.age === null || this.user.age === undefined)) return false;
    return true;
  }

  onCreate() {
    if (this.user.role === 'Caregiver') {
        this.messageService.add({ 
          severity: 'info', 
          summary: 'Mock Feature', 
          detail: 'Caregiver creation is currently a UI mock.' 
        });
        this.onClose();
        return;
    }

    this.loading.set(true);
    const patientData = {
      firstName: this.user.firstName,
      lastName: this.user.lastName,
      age: this.user.age
    };

    this.patientService.create(patientData).subscribe({
      next: () => {
        this.messageService.add({ 
            severity: 'success', 
            summary: 'Success', 
            detail: 'Patient profile created successfully' 
        });
        this.patientService.triggerRefresh();
        this.loading.set(false);
        this.onClose();
      },
      error: (err) => {
        console.error('Creation failed:', err);
        this.messageService.add({ 
            severity: 'error', 
            summary: 'Error', 
            detail: 'Failed to create patient profile' 
        });
        this.loading.set(false);
      }
    });
  }
}
