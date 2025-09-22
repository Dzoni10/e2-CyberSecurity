import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class WebCryptoService {
  
  async importPublicKey(publicKeyBase64: string): Promise<CryptoKey> {
    try {
      const publicKeyBuffer = this.base64ToArrayBuffer(publicKeyBase64);
      
      return await window.crypto.subtle.importKey(
        'spki',
        publicKeyBuffer,
        {
          name: 'RSA-OAEP',
          hash: 'SHA-256'
        },
        false,
        ['encrypt']
      );
    } catch (error) {
      throw new Error('Failed to import public key: ' + error);
    }
  }

  async importPrivateKey(privateKeyPem: string): Promise<CryptoKey> {
    try {
      const privateKeyBase64 = privateKeyPem
        .replace('-----BEGIN PRIVATE KEY-----', '')
        .replace('-----END PRIVATE KEY-----', '')
        .replace('-----BEGIN RSA PRIVATE KEY-----', '')
        .replace('-----END RSA PRIVATE KEY-----', '')
        .replace(/\s/g, '');
      
      const privateKeyBuffer = this.base64ToArrayBuffer(privateKeyBase64);
      
      return await window.crypto.subtle.importKey(
        'pkcs8',
        privateKeyBuffer,
        {
          name: 'RSA-OAEP',
          hash: 'SHA-256'
        },
        false,
        ['decrypt']
      );
    } catch (error) {
      throw new Error('Failed to import private key: ' + error);
    }
  }

  async encryptPassword(password: string, publicKey: CryptoKey): Promise<string> {
    try {
      const passwordBuffer = new TextEncoder().encode(password);
      const encryptedBuffer = await window.crypto.subtle.encrypt(
        { name: 'RSA-OAEP' },
        publicKey,
        passwordBuffer
      );
      
      return this.arrayBufferToBase64(encryptedBuffer);
    } catch (error) {
      throw new Error('Failed to encrypt password: ' + error);
    }
  }

  async decryptPassword(encryptedPasswordBase64: string, privateKey: CryptoKey): Promise<string> {
    try {
      const encryptedBuffer = this.base64ToArrayBuffer(encryptedPasswordBase64);
      const decryptedBuffer = await window.crypto.subtle.decrypt(
        { name: 'RSA-OAEP' },
        privateKey,
        encryptedBuffer
      );
      
      return new TextDecoder().decode(decryptedBuffer);
    } catch (error) {
      throw new Error('Failed to decrypt password: ' + error);
    }
  }

  private arrayBufferToBase64(buffer: ArrayBuffer): string {
    const binary = String.fromCharCode(...new Uint8Array(buffer));
    return window.btoa(binary);
  }

  private base64ToArrayBuffer(base64: string): ArrayBuffer {
    const binary = window.atob(base64);
    const buffer = new ArrayBuffer(binary.length);
    const view = new Uint8Array(buffer);
    for (let i = 0; i < binary.length; i++) {
      view[i] = binary.charCodeAt(i);
    }
    return buffer;
  }
}