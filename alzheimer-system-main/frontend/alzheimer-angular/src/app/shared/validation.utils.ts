/**
 * Shared validation & sanitization utilities for the Alzheimer system.
 * Used across Doctor, Soignant, and Aidant dashboards.
 */

// ─── Sanitization ──────────────────────────────────────────────────────────────

/** Strip HTML tags to prevent XSS */
export function sanitizeHtml(value: string): string {
  if (!value) return value;
  return value.replace(/<[^>]*>/g, '').replace(/[<>]/g, '');
}

/** Trim and sanitize a string input */
export function sanitizeInput(value: string): string {
  if (!value) return '';
  return sanitizeHtml(value.trim());
}

/** Sanitize all string fields of an object (shallow, non-destructive) */
export function sanitizeObject<T extends Record<string, any>>(obj: T): T {
  const result = { ...obj };
  for (const key of Object.keys(result)) {
    if (typeof result[key] === 'string') {
      (result as any)[key] = sanitizeInput(result[key]);
    }
  }
  return result;
}

// ─── Validators ────────────────────────────────────────────────────────────────

/** Check if a string is empty or whitespace-only */
export function isEmpty(value: any): boolean {
  if (value === null || value === undefined) return true;
  if (typeof value === 'string') return value.trim().length === 0;
  return false;
}

/** Validate email format */
export function isValidEmail(email: string): boolean {
  if (!email) return true; // empty is OK (use required check separately)
  const re = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return re.test(email.trim());
}

/** Validate phone number (digits, spaces, +, -, parentheses; 8-20 chars) */
export function isValidPhone(phone: string): boolean {
  if (!phone) return true; // empty is OK
  const re = /^[0-9+\-\s()]{8,20}$/;
  return re.test(phone.trim());
}

/** Validate date string is not in the far past (> 1900) */
export function isValidDate(dateStr: string): boolean {
  if (!dateStr) return true;
  const d = new Date(dateStr);
  return !isNaN(d.getTime()) && d.getFullYear() >= 1900;
}

/** Check if endDate >= startDate */
export function isEndDateAfterStart(startDate: string, endDate: string): boolean {
  if (!startDate || !endDate) return true;
  return new Date(endDate) >= new Date(startDate);
}

/** Validate max length */
export function isWithinMaxLength(value: string, max: number): boolean {
  if (!value) return true;
  return value.trim().length <= max;
}

/** Validate min length */
export function isMinLength(value: string, min: number): boolean {
  if (!value) return true;
  return value.trim().length >= min;
}

/** Validate a number is within range */
export function isInRange(value: number | null | undefined, min: number, max: number): boolean {
  if (value === null || value === undefined) return true;
  return value >= min && value <= max;
}

// ─── Validation Error Collector ────────────────────────────────────────────────

export interface ValidationErrors {
  [field: string]: string;
}

/**
 * Helper class to collect validation errors for a form.
 * Usage:
 *   const v = new FormValidator();
 *   v.required('nomComplet', obj.nomComplet, 'Le nom complet est requis');
 *   v.email('email', obj.email, 'Email invalide');
 *   if (v.hasErrors()) { this.formErrors = v.errors; return; }
 */
export class FormValidator {
  errors: ValidationErrors = {};

  required(field: string, value: any, message: string): this {
    if (isEmpty(value)) {
      this.errors[field] = message;
    }
    return this;
  }

  email(field: string, value: string, message: string): this {
    if (value && !isValidEmail(value)) {
      this.errors[field] = message;
    }
    return this;
  }

  phone(field: string, value: string, message: string): this {
    if (value && !isValidPhone(value)) {
      this.errors[field] = message;
    }
    return this;
  }

  maxLength(field: string, value: string, max: number, message: string): this {
    if (!isWithinMaxLength(value, max)) {
      this.errors[field] = message;
    }
    return this;
  }

  minLength(field: string, value: string, min: number, message: string): this {
    if (!isMinLength(value, min)) {
      this.errors[field] = message;
    }
    return this;
  }

  dateValid(field: string, value: string, message: string): this {
    if (value && !isValidDate(value)) {
      this.errors[field] = message;
    }
    return this;
  }

  dateRange(field: string, start: string, end: string, message: string): this {
    if (!isEndDateAfterStart(start, end)) {
      this.errors[field] = message;
    }
    return this;
  }

  range(field: string, value: number | null | undefined, min: number, max: number, message: string): this {
    if (!isInRange(value, min, max)) {
      this.errors[field] = message;
    }
    return this;
  }

  custom(field: string, condition: boolean, message: string): this {
    if (condition) {
      this.errors[field] = message;
    }
    return this;
  }

  hasErrors(): boolean {
    return Object.keys(this.errors).length > 0;
  }

  getError(field: string): string {
    return this.errors[field] || '';
  }

  clear(): void {
    this.errors = {};
  }
}
