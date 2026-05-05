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
      gap: 0.6rem;

      label {
        font-weight: 700;
        font-size: 0.8rem;
        text-transform: uppercase;
        letter-spacing: 0.05em;
        color: #64748b;
        padding-left: 0.25rem;
      }

      ::ng-deep .p-inputtext, 
      ::ng-deep .p-dropdown,
      ::ng-deep .p-inputnumber-input {
        width: 100% !important;
        background: rgba(255, 255, 255, 0.5) !important;
        border: 1px solid rgba(0, 0, 0, 0.05) !important;
        border-radius: 14px !important;
        padding: 0.8rem 1rem !important;
        font-size: 0.95rem !important;
        transition: all 0.2s ease !important;
        color: #1e293b !important;

        &:focus {
          background: #fff !important;
          border-color: #6366f1 !important;
          box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1) !important;
        }
      }

      ::ng-deep .p-dropdown {
          padding: 0 !important;
          .p-dropdown-label { padding: 0.8rem 1rem !important; }
          .p-dropdown-trigger { width: 3rem !important; }
      }
    }

    ::ng-deep .create-user-dialog {
        .p-dialog {
            background: rgba(255, 255, 255, 0.72) !important;
            backdrop-filter: blur(20px) saturate(180%) !important;
            -webkit-backdrop-filter: blur(20px) saturate(180%) !important;
            border: 1px solid rgba(255, 255, 255, 0.4) !important;
            border-radius: 24px !important;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1) !important;
        }
        
        .p-dialog-header {
            background: transparent !important;
            padding: 2rem 2rem 1rem !important;
            border-top-left-radius: 24px !important;
            border-top-right-radius: 24px !important;
            
            .p-dialog-title {
                font-weight: 800 !important;
                font-size: 1.5rem !important;
                color: #0f172a !important;
                letter-spacing: -0.02em !important;
            }
        }
        
        .p-dialog-content {
            background: transparent !important;
            padding: 0 2rem 2rem !important;
        }
        
        .p-dialog-footer {
            background: rgba(255, 255, 255, 0.4) !important;
            padding: 1.25rem 2rem !important;
            border-bottom-left-radius: 24px !important;
            border-bottom-right-radius: 24px !important;
            border-top: 1px solid rgba(0, 0, 0, 0.05) !important;
        }

        .p-button.p-button-text {
            color: #64748b !important;
            font-weight: 700 !important;
        }

        .p-button:not(.p-button-text) {
            background: #6366f1 !important;
            border: none !important;
            border-radius: 12px !important;
            padding: 0.75rem 1.5rem !important;
            font-weight: 700 !important;
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2) !important;
            
            &:hover {
                background: #4f46e5 !important;
                transform: translateY(-1px) !important;
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
