export const environment = {
  production: false,
  /** API via Spring Cloud Gateway (see docker-compose api-gateway :8080) */
  apiUrl: 'http://localhost:8080/api',
  /** Keycloak public URL (host port 8081 → container 8080) */
  keycloakUrl: 'http://localhost:8081',
  keycloakRealm: 'alzheimer-realm',
  keycloakClientId: 'alzheimer-angular-client',
  supportNetworkApiUrl: 'http://localhost:8080/api',
  supportNetworkWebSocketUrl: 'http://localhost:8080/ws',
};
