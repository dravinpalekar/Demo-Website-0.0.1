import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";

export function FileValidation(allowedTypes: string[], maxSizeMB: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const file = control.value;

    // If no file selected → no error
    if (!file) {
      return null;
    }

    // If not a File object (e.g., a fake path string), skip validation
    if (!(file instanceof File)) {
      return null;
    }

    // ✅ File type validation
    if (!allowedTypes.includes(file.type)) {
      return { invalidFileType: true };
    }

    // ✅ File size validation
    const maxSizeBytes = maxSizeMB * 1024 * 1024;
    if (file.size > maxSizeBytes) {
      return { maxFileSize: true };
    }

    return null; // ✅ valid
  };
}