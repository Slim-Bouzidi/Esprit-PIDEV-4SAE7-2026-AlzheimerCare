const host = typeof window !== 'undefined' ? window.location.hostname : 'localhost';

export const environment = {
  production: false,
  apiUrl: `http://${host}:30080/api`,
  keycloakUrl: `http://${host}:30081`,
  keycloakRealm: 'alzheimer-realm',
  keycloakClientId: 'alzheimer-angular-client',
  supportNetworkApiUrl: `http://${host}:30080/api`,
  supportNetworkWebSocketUrl: `ws://${host}:30080/ws`,
};
