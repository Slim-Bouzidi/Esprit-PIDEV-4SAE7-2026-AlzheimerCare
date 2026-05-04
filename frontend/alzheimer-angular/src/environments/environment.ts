const vmIp = window.location.hostname;

export const environment = {
  production: false,
  apiUrl: `http://${vmIp}:30080/api`,
  keycloakUrl: `http://${vmIp}:30081`,
  keycloakRealm: 'alzheimer-realm',
  keycloakClientId: 'alzheimer-angular-client',
  supportNetworkApiUrl: '/api',
  supportNetworkWebSocketUrl: `http://${vmIp}:30080/ws`
};
