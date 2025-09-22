import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PasswordManagerService, PasswordEntry, PasswordEntryRequest } from './password-manager.service';
import { WebCryptoService } from './web-crypto.service';

@Component({
  selector: 'app-password-manager',
  templateUrl: './password-manager.component.html',
  styleUrls: ['./password-manager.component.css']
})
export class PasswordManagerComponent implements OnInit {
  passwordForm!: FormGroup;
  privateKeyForm!: FormGroup;
  
  passwords: PasswordEntry[] = [];
  userPublicKey: string | null = null;
  userPrivateKey: CryptoKey | null = null;
  
  showAddForm = false;
  showPrivateKeyForm = false;
  isPrivateKeyLoaded = false;
  
  loading = false;
  message = '';
  error = '';

  decryptedPasswords: { [key: number]: string } = {};

  constructor(
    private fb: FormBuilder,
    private passwordService: PasswordManagerService,
    private cryptoService: WebCryptoService
  ) {}

  ngOnInit() {
    this.initForms();
    this.loadUserPublicKey();
    this.loadPasswords();
  }

  initForms() {
    this.passwordForm = this.fb.group({
      siteName: ['', Validators.required],
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    this.privateKeyForm = this.fb.group({
      privateKeyFile: [null],
      privateKeyText: ['']
    });
  }

  loadUserPublicKey() {
    this.passwordService.getCurrentUserPublicKey().subscribe({
      next: (response) => {
        this.userPublicKey = response.publicKey;
        this.message = 'Public key loaded successfully';
      },
      error: (err) => {
        this.error = 'Failed to load public key: ' + (err.error?.message || 'Unknown error');
      }
    });
  }

  loadPasswords() {
    this.loading = true;
    this.passwordService.getUserPasswordEntries().subscribe({
      next: (passwords) => {
        this.passwords = passwords;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load passwords: ' + (err.error?.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  onPrivateKeyFileChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.privateKeyForm.patchValue({ privateKeyText: e.target.result });
      };
      reader.readAsText(file);
    }
  }

  async loadPrivateKey() {
    try {
      const privateKeyPem = this.privateKeyForm.get('privateKeyText')?.value;
      if (!privateKeyPem) {
        this.error = 'Please provide private key';
        return;
      }

      this.userPrivateKey = await this.cryptoService.importPrivateKey(privateKeyPem);
      this.isPrivateKeyLoaded = true;
      this.showPrivateKeyForm = false;
      this.message = 'Private key loaded successfully';
      this.error = '';
    } catch (error) {
      this.error = 'Failed to load private key: ' + error;
    }
  }

  async savePassword() {
    if (!this.userPublicKey) {
      this.error = 'Public key not available';
      return;
    }

    try {
      this.loading = true;
      
      const formValue = this.passwordForm.value;
      const publicKey = await this.cryptoService.importPublicKey(this.userPublicKey);
      const encryptedPassword = await this.cryptoService.encryptPassword(formValue.password, publicKey);

      const request: PasswordEntryRequest = {
        siteName: formValue.siteName,
        username: formValue.username,
        encryptedPassword: encryptedPassword
      };

      this.passwordService.savePasswordEntry(request).subscribe({
        next: (savedEntry) => {
          this.passwords.push(savedEntry);
          this.passwordForm.reset();
          this.showAddForm = false;
          this.message = 'Password saved successfully';
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to save password: ' + (err.error?.message || 'Unknown error');
          this.loading = false;
        }
      });
    } catch (error) {
      this.error = 'Encryption failed: ' + error;
      this.loading = false;
    }
  }

  async decryptPassword(entry: PasswordEntry) {
    if (!this.userPrivateKey) {
      this.error = 'Private key not loaded';
      return;
    }

    try {
      const decryptedPassword = await this.cryptoService.decryptPassword(entry.encryptedPassword, this.userPrivateKey);
      this.decryptedPasswords[entry.id] = decryptedPassword;
      this.message = 'Password decrypted successfully';
    } catch (error) {
      this.error = 'Failed to decrypt password: ' + error;
    }
  }

  hidePassword(entryId: number) {
    delete this.decryptedPasswords[entryId];
  }

  deletePassword(entryId: number) {
    if (confirm('Are you sure you want to delete this password?')) {
      this.passwordService.deletePasswordEntry(entryId).subscribe({
        next: () => {
          this.passwords = this.passwords.filter(p => p.id !== entryId);
          delete this.decryptedPasswords[entryId];
          this.message = 'Password deleted successfully';
        },
        error: (err) => {
          this.error = 'Failed to delete password: ' + (err.error?.message || 'Unknown error');
        }
      });
    }
  }

  clearMessages() {
    this.message = '';
    this.error = '';
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      this.message = 'Password copied to clipboard';
    }).catch(() => {
      this.error = 'Failed to copy to clipboard';
    });
  }
}