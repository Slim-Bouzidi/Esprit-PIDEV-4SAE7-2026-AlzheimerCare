export const environment = {
  production: false,
  // Local Development (using Docker Compose or local installs)
  apiUrl: 'http://localhost:8080/api',
  keycloakUrl: 'http://localhost:8081',
  keycloakRealm: 'alzheimer-realm',
  keycloakClientId: 'alzheimer-angular-client',
  supportNetworkApiUrl: 'http://localhost:8080/api',
  supportNetworkWebSocketUrl: 'ws://localhost:8080/ws',
};
