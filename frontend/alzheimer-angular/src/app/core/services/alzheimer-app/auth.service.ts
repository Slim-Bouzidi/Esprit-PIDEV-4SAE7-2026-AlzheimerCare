import { Injectable, signal } from '@angular/core';
import keycloak from '../../../keycloak';

export interface UserProfile {
    username?: string;
    email?: string;
    firstName?: string;
    lastName?: string;
    roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
    private profileSignal = signal<UserProfile | null>(null);
    readonly profile = this.profileSignal.asReadonly();

    constructor() {
        this.updateProfile();
    }

    private updateProfile(): void {
        if (keycloak.authenticated) {
            const roles = keycloak.realmAccess?.roles || [];
            const username = keycloak.tokenParsed?.['preferred_username'];

            // Set initial profile immediately from token
            this.profileSignal.set({ username, roles });

            // Only try to load full profile if we have a token
            if (keycloak.token) {
                keycloak.loadUserProfile().then((profile: any) => {
                    const userProfile = {
                        username,
                        email: profile.email,
                        firstName: profile.firstName,
                        lastName: profile.lastName,
                        roles
                    };
                    this.profileSignal.set(userProfile);
                }).catch((err: any) => {
                    console.warn('[AuthService] Could not load extended profile, using token data only');
                });
            }
        }
    }

    get username(): string | undefined {
        return this.profile()?.username;
    }

    hasRole(role: string): boolean {
        const userRoles = this.profile()?.roles || [];
        // Keycloak roles often don't have the ROLE_ prefix in the token
        return userRoles.includes(role) || userRoles.includes(`ROLE_${role}`);
    }

    isAdmin(): boolean {
        return this.hasRole('ADMIN');
    }

    isStaff(): boolean {
        return this.hasRole('LIVREUR') || this.hasRole('STAFF');
    }

    logout(): void {
        console.log('[AuthService] Logout initiated');
        try {
            keycloak.logout({ redirectUri: window.location.origin });
        } catch (error) {
            console.error('[AuthService] Logout error:', error);
            // Fallback: clear local storage and redirect
            localStorage.clear();
            sessionStorage.clear();
            window.location.href = window.location.origin;
        }
    }
}
