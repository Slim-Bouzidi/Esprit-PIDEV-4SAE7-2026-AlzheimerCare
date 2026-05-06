import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MlPredictionService, MLPredictResponse } from '../../core/services/ml-prediction.service';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { SliderModule } from 'primeng/slider';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';

@Component({
  selector: 'app-risk-assessment',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ToastModule,
    SliderModule,
    InputNumberModule,
    DropdownModule,
    CardModule,
    ButtonModule,
    ProgressSpinnerModule
  ],
  providers: [MessageService],
  template: `
    <div class="risk-container">
      <p-toast></p-toast>
      
      <div class="header-section">
        <span class="badge">AI-Powered Diagnostic Tool</span>
        <h1>Alzheimer's Risk <span class="gradient-text">Predictor</span></h1>
        <p>Complete the patient profile below to generate a real-time risk assessment using our trained machine learning model.</p>
      </div>

      <div class="main-grid">
        <!-- Form Section -->
        <div class="form-card card-glass">
          <form [formGroup]="predictionForm" (ngSubmit)="onPredict()">
            
            <div class="form-grid">
              <!-- Demographics -->
              <div class="section-title">Demographics</div>
              <div class="form-group">
                <label>Age</label>
                <p-inputNumber formControlName="Age" [min]="60" [max]="90"></p-inputNumber>
              </div>

              <div class="form-group">
                <label>Gender</label>
                <p-dropdown [options]="genderOptions" formControlName="Gender" optionLabel="label" optionValue="value"></p-dropdown>
              </div>

              <div class="form-group">
                <label>Education Level <span class="range-info">0 - 3</span></label>
                <p-inputNumber formControlName="EducationLevel" [min]="0" [max]="3"></p-inputNumber>
              </div>

              <div class="form-group">
                <label>BMI</label>
                <p-inputNumber formControlName="BMI" [minFractionDigits]="2" [maxFractionDigits]="2"></p-inputNumber>
              </div>

              <!-- Lifestyle -->
              <div class="section-title">Lifestyle & Habits</div>
              <div class="form-group">
                <label>Physical Activity <span class="range-info">0 - 10</span></label>
                <div class="slider-wrapper">
                  <p-slider formControlName="PhysicalActivity" [min]="0" [max]="10" [step]="0.1" class="w-full"></p-slider>
                  <span class="value-display">{{predictionForm.value.PhysicalActivity}}</span>
                </div>
              </div>

              <div class="form-group">
                <label>Diet Quality <span class="range-info">0 - 10</span></label>
                <div class="slider-wrapper">
                  <p-slider formControlName="DietQuality" [min]="0" [max]="10" [step]="0.1" class="w-full"></p-slider>
                  <span class="value-display">{{predictionForm.value.DietQuality}}</span>
                </div>
              </div>

              <div class="form-group">
                <label>Sleep Quality <span class="range-info">0 - 10</span></label>
                <div class="slider-wrapper">
                  <p-slider formControlName="SleepQuality" [min]="0" [max]="10" [step]="0.1" class="w-full"></p-slider>
                  <span class="value-display">{{predictionForm.value.SleepQuality}}</span>
                </div>
              </div>

              <div class="form-group">
                <label>Smoking Status</label>
                <p-dropdown [options]="[{label:'Non-Smoker', value:0}, {label:'Smoker', value:1}]" formControlName="Smoking" optionLabel="label" optionValue="value"></p-dropdown>
              </div>

              <!-- Cognitive & Medical -->
              <div class="section-title">Cognitive & Medical</div>
              <div class="form-group">
                <label>MMSE Score <span class="range-info">0 - 30</span></label>
                <div class="slider-wrapper">
                  <p-slider formControlName="MMSE" [min]="0" [max]="30" class="w-full"></p-slider>
                  <span class="value-display">{{predictionForm.value.MMSE}}</span>
                </div>
              </div>

              <div class="form-group">
                <label>ADL Score <span class="range-info">0 - 10</span></label>
                <div class="slider-wrapper">
                  <p-slider formControlName="ADL" [min]="0" [max]="10" [step]="0.1" class="w-full"></p-slider>
                  <span class="value-display">{{predictionForm.value.ADL}}</span>
                </div>
              </div>

              <!-- Critical Toggles -->
              <div class="form-group toggle-group">
                <label class="toggle-label">
                  <input type="checkbox" formControlName="MemoryComplaints">
                  <span>Memory Complaints</span>
                </label>
                <label class="toggle-label">
                  <input type="checkbox" formControlName="FamilyHistoryAlzheimers">
                  <span>Family History</span>
                </label>
                <label class="toggle-label">
                  <input type="checkbox" formControlName="CardiovascularDisease">
                  <span>Cardio Disease</span>
                </label>
              </div>
            </div>

            <button pButton type="submit" label="Generate AI Analysis" icon="pi pi-bolt" [loading]="loading" class="submit-btn p-button-raised p-button-rounded"></button>
          </form>
        </div>

        <!-- Result Section -->
        <div class="result-card card-glass" [class.active]="result">
          <div *ngIf="!result && !loading" class="placeholder">
            <i class="pi pi-chart-line"></i>
            <p>Enter clinical data and click generate</p>
          </div>

          <div *ngIf="loading" class="loader">
            <p-progressSpinner styleClass="custom-spinner" strokeWidth="4"></p-progressSpinner>
            <p>AI Model is processing 32 features...</p>
          </div>

          <div *ngIf="result" class="prediction-content fade-in">
            <div class="risk-meter" [class.high]="result.prediction === 1 || (result.probability || 0) >= 0.4">
              <div class="meter-icon">
                <i [class]="(result.prediction === 1 || (result.probability || 0) >= 0.4) ? 'pi pi-exclamation-triangle' : 'pi pi-check-circle'"></i>
              </div>
              <div class="risk-label">
                <h2>{{ (result.prediction === 1 || (result.probability || 0) >= 0.4) ? 'High Risk Detected' : 'Low Risk / Stable' }}</h2>
                <p>Calculated via Random Forest Classifier</p>
              </div>
            </div>

            <div class="stats-row">
              <div class="stat-box">
                <span class="label">Probability</span>
                <span class="value">{{ (result.probability || 0) | percent:'1.1-1' }}</span>
              </div>
              <div class="stat-box">
                <span class="label">Clinical Status</span>
                <span class="value tag" [class.tag-danger]="result.prediction === 1 || (result.probability || 0) >= 0.4">{{ (result.prediction === 1 || (result.probability || 0) >= 0.4) ? 'High Concern' : 'Normal Range' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Insights Section -->
      <div class="insights-section mt-8">
        <div class="section-header">
          <h2>Clinical <span class="gradient-text">Insights</span> & References</h2>
          <p>Understand the data patterns that influence the AI's decision process.</p>
        </div>

        <div class="insights-grid">
          <div class="insight-card card-glass">
            <h3><i class="pi pi-info-circle"></i> Factor Importance</h3>
            <div class="factor-list">
              <div class="factor-item">
                <span>MMSE Score</span>
                <div class="bar-bg"><div class="bar-fill" style="width: 95%"></div></div>
              </div>
              <div class="factor-item">
                <span>Functional State</span>
                <div class="bar-bg"><div class="bar-fill" style="width: 80%"></div></div>
              </div>
              <div class="factor-item">
                <span>Memory Complaints</span>
                <div class="bar-bg"><div class="bar-fill" style="width: 70%"></div></div>
              </div>
            </div>
          </div>

          <!-- Card 2: Persona Table (RESTORED) -->
          <div class="insight-card card-glass">
            <h3><i class="pi pi-users text-purple-500"></i> Persona Guide</h3>
            <div class="persona-table">
              <table>
                <thead>
                  <tr>
                    <th>Metric</th>
                    <th class="text-success">Healthy</th>
                    <th class="text-danger">At Risk</th>
                  </tr>
                </thead>
                <tbody>
                  <tr><td>MMSE</td><td>28 - 30</td><td>< 15</td></tr>
                  <tr><td>ADL</td><td>> 8.5</td><td>< 4.0</td></tr>
                  <tr><td>Age</td><td>60 - 70</td><td>80+</td></tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- Shadcn-Style Reference Cards (Cleaned up) -->
        <div class="reference-grid mt-16">
          <div class="ref-card">
            <div class="ref-header">
              <i class="pi pi-database text-blue-500 ref-icon"></i>
              <h4>MMSE Protocol</h4>
            </div>
            <p>The Mini-Mental State Examination is a 30-point questionnaire used extensively in clinical settings to measure cognitive impairment. It evaluates orientation, registration, and recall.</p>
            <div class="ref-footer">Clinical Standard</div>
          </div>

          <div class="ref-card">
            <div class="ref-header">
              <i class="pi pi-heart-fill text-red-500 ref-icon"></i>
              <h4>ADL Independence</h4>
            </div>
            <p>Activities of Daily Living (ADL) measures a patient's ability to live independently. It tracks basic skills like self-feeding, dressing, and mobility without assistance.</p>
            <div class="ref-footer">Functional Metric</div>
          </div>

          <div class="ref-card">
            <div class="ref-header">
              <i class="pi pi-filter text-purple-500 ref-icon"></i>
              <h4>Genetic Risk</h4>
            </div>
            <p>Presence of APOE-ε4 alleles or family history significantly increases susceptibility. Our AI weights this against lifestyle factors to determine overall risk.</p>
            <div class="ref-footer">Biological Marker</div>
          </div>

          <div class="ref-card">
            <div class="ref-header">
              <i class="pi pi-sun text-orange-500 ref-icon"></i>
              <h4>Lifestyle Impact</h4>
            </div>
            <p>Diet quality and physical activity (0-10 scale) are proven to be protective factors that can delay the onset of symptoms even in high-risk individuals.</p>
            <div class="ref-footer">Protective Factor</div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .risk-container {
      padding: 4rem 2rem;
      max-width: 1200px;
      margin: 0 auto;
      min-height: 100vh;
      font-family: 'Inter', sans-serif;
    }

    .header-section {
      text-align: center;
      margin-bottom: 3rem;
    }

    .section-header {
      margin-top: 5rem;
      margin-bottom: 2rem;
      text-align: left;
    }

    .section-header h2 { font-size: 2.2rem; margin-bottom: 0.5rem; }

    .badge {
      background: rgba(59, 130, 246, 0.1);
      color: #3b82f6;
      padding: 0.5rem 1.2rem;
      border-radius: 50px;
      font-size: 0.85rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 1px;
    }

    h1 {
      font-size: 3.5rem;
      font-weight: 800;
      margin: 1.5rem 0;
      color: #1e293b;
    }

    .gradient-text {
      background: linear-gradient(135deg, #3b82f6, #8b5cf6);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }

    .main-grid {
      display: grid;
      grid-template-columns: 1.6fr 1fr;
      gap: 2rem;
    }

    .card-glass {
      background: rgba(255, 255, 255, 0.8);
      backdrop-filter: blur(20px);
      border: 1px solid rgba(255, 255, 255, 0.4);
      border-radius: 24px;
      padding: 2.5rem;
      box-shadow: 0 20px 40px rgba(0,0,0,0.05);
    }

    .form-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 2rem;
      margin-bottom: 2rem;
    }

    .section-title {
      grid-column: span 2;
      font-size: 1.1rem;
      font-weight: 800;
      color: #1e293b;
      padding-bottom: 0.5rem;
      border-bottom: 2px solid #f1f5f9;
      margin-top: 1rem;
      margin-bottom: 0.5rem;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
    }

    .range-info {
      float: right;
      font-size: 0.75rem;
      color: #94a3b8;
      font-weight: 400;
    }

    label {
      font-weight: 700;
      font-size: 0.85rem;
      color: #334155;
    }

    .slider-wrapper {
      display: flex;
      align-items: center;
      gap: 1.5rem;
    }

    .value-display {
      background: #3b82f6;
      color: white;
      padding: 0.25rem 0.75rem;
      border-radius: 8px;
      font-weight: 700;
      font-size: 0.8rem;
      min-width: 65px;
      text-align: center;
    }

    .hint { color: #94a3b8; font-size: 0.75rem; }

    .submit-btn {
      width: 100%;
      height: 4rem;
      background: #1e293b !important;
      border: none !important;
      font-size: 1.2rem;
      font-weight: 600;
      border-radius: 16px !important;
    }

    .insights-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 2rem;
    }

    .insight-card h3 {
      font-size: 1.2rem;
      margin-bottom: 1.5rem;
      color: #1e293b;
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }

    .factor-item { margin-bottom: 1.2rem; }
    .factor-item span { display: block; font-size: 0.85rem; margin-bottom: 0.4rem; color: #64748b; }
    .bar-bg { background: #f1f5f9; height: 8px; border-radius: 10px; overflow: hidden; }
    .bar-fill { background: #3b82f6; height: 100%; border-radius: 10px; }

    .persona-table table { width: 100%; border-collapse: collapse; }
    .persona-table th { text-align: left; padding: 1rem 0.5rem; border-bottom: 2px solid #f1f5f9; font-size: 0.8rem; text-transform: uppercase; color: #94a3b8; }
    .persona-table td { padding: 1rem 0.5rem; border-bottom: 1px solid #f1f5f9; font-size: 0.9rem; font-weight: 600; }
    .text-success { color: #059669; }
    .text-danger { color: #dc2626; }

    .reference-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 2.5rem;
      margin-top: 4rem;
    }

    .ref-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 16px;
      padding: 2rem;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    }

    .ref-icon { 
      font-size: 1.25rem !important;
      background: #f8fafc;
      padding: 0.75rem;
      border-radius: 10px;
    }

    .ref-card h4 {
      margin: 0;
      font-size: 1.1rem;
      font-weight: 700;
      color: #0f172a;
    }

    .ref-card p {
      font-size: 0.875rem;
      color: #64748b;
      line-height: 1.6;
      margin-bottom: 1.25rem;
    }

    .ref-footer {
      font-size: 0.75rem;
      font-weight: 600;
      color: #94a3b8;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    .toggle-group {
      grid-column: span 2;
      display: flex;
      gap: 2rem;
      background: #f8fafc;
      padding: 1.5rem;
      border-radius: 16px;
      margin-top: 1rem;
    }

    .toggle-label {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      cursor: pointer;
      font-weight: 600;
      color: #334155;
    }

    .toggle-label input {
      width: 1.2rem;
      height: 1.2rem;
      accent-color: #3b82f6;
    }

    .result-card {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      min-height: 450px;
    }

    .risk-meter {
      background: #f0fdf4;
      padding: 2.5rem;
      border-radius: 24px;
      width: 100%;
      text-align: center;
      color: #166534;
    }

    .risk-meter.high { background: #fef2f2; color: #991b1b; }
    .meter-icon i { font-size: 4rem; margin-bottom: 1rem; }
    .stats-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-top: 2rem; width: 100%; }
    .stat-box { background: #f8fafc; padding: 1.5rem; border-radius: 20px; display: flex; flex-direction: column; gap: 0.5rem; }
    .stat-box .label { font-size: 0.75rem; color: #94a3b8; text-transform: uppercase; }
    .stat-box .value { font-size: 1.8rem; font-weight: 800; }

    .w-full { width: 100%; }
    .mt-8 { margin-top: 2rem; }
    .fade-in { animation: fadeIn 0.6s ease-out; }
    @keyframes fadeIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }
  `]
})
export class RiskAssessmentComponent {
  predictionForm: FormGroup;
  loading = false;
  result: MLPredictResponse | null = null;

  genderOptions = [
    { label: 'Male', value: 0 },
    { label: 'Female', value: 1 }
  ];

  constructor(
    private fb: FormBuilder,
    private mlService: MlPredictionService,
    private messageService: MessageService
  ) {
    this.predictionForm = this.fb.group({
      Age: [70, [Validators.required]],
      Gender: [0, [Validators.required]],
      Ethnicity: [0],
      EducationLevel: [1],
      BMI: [25.0, [Validators.required]],
      Smoking: [0],
      AlcoholConsumption: [5.0],
      PhysicalActivity: [5.0],
      DietQuality: [5.0],
      SleepQuality: [7.0],
      FamilyHistoryAlzheimers: [0],
      CardiovascularDisease: [0],
      Diabetes: [0],
      Depression: [0],
      HeadInjury: [0],
      Hypertension: [0],
      SystolicBP: [120],
      DiastolicBP: [80],
      CholesterolTotal: [200],
      CholesterolLDL: [100],
      CholesterolHDL: [50],
      CholesterolTriglycerides: [150],
      MMSE: [25],
      FunctionalAssessment: [8.0],
      MemoryComplaints: [0],
      BehavioralProblems: [0],
      ADL: [8.0],
      Confusion: [0],
      Disorientation: [0],
      PersonalityChanges: [0],
      DifficultyCompletingTasks: [0],
      Forgetfulness: [0]
    });
  }

  onPredict() {
    if (this.predictionForm.invalid) return;

    this.loading = true;
    this.result = null;

    // Convert form values to the 32-feature array expected by the model
    const features = [
      this.predictionForm.value.Age,
      this.predictionForm.value.Gender,
      this.predictionForm.value.Ethnicity,
      this.predictionForm.value.EducationLevel,
      this.predictionForm.value.BMI,
      this.predictionForm.value.Smoking,
      this.predictionForm.value.AlcoholConsumption,
      this.predictionForm.value.PhysicalActivity,
      this.predictionForm.value.DietQuality,
      this.predictionForm.value.SleepQuality,
      this.predictionForm.value.FamilyHistoryAlzheimers ? 1 : 0,
      this.predictionForm.value.CardiovascularDisease ? 1 : 0,
      this.predictionForm.value.Diabetes,
      this.predictionForm.value.Depression,
      this.predictionForm.value.HeadInjury,
      this.predictionForm.value.Hypertension,
      this.predictionForm.value.SystolicBP,
      this.predictionForm.value.DiastolicBP,
      this.predictionForm.value.CholesterolTotal,
      this.predictionForm.value.CholesterolLDL,
      this.predictionForm.value.CholesterolHDL,
      this.predictionForm.value.CholesterolTriglycerides,
      this.predictionForm.value.MMSE,
      this.predictionForm.value.FunctionalAssessment,
      this.predictionForm.value.MemoryComplaints ? 1 : 0,
      this.predictionForm.value.BehavioralProblems ? 1 : 0,
      this.predictionForm.value.ADL,
      this.predictionForm.value.Confusion,
      this.predictionForm.value.Disorientation,
      this.predictionForm.value.PersonalityChanges,
      this.predictionForm.value.DifficultyCompletingTasks,
      this.predictionForm.value.Forgetfulness
    ];

    console.log('Sending features to AI:', features);

    this.mlService.predict(features).subscribe({
      next: (res) => {
        this.result = res;
        this.loading = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Analysis Complete',
          detail: 'Risk profile generated successfully'
        });
      },
      error: (err) => {
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Analysis Failed',
          detail: 'Could not connect to the ML microservice'
        });
      }
    });
  }
}
