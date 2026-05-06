const host = window.location.hostname;

export const environment = {
  production: true,
  apiUrl: `http://${host}:30080/api`,
  keycloakUrl: `http://${host}:30081`,
  keycloakRealm: 'alzheimer-realm',
  keycloakClientId: 'alzheimer-angular-client',
  supportNetworkApiUrl: `http://${host}:30080/api`,
  supportNetworkWebSocketUrl: `ws://${host}:30080/ws`,
};
