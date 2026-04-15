// Keycloak disabled - Mock object to prevent errors
const tokenData = {
  name: 'User',
  given_name: 'User',
  family_name: 'Test',
  preferred_username: 'user',
  email: 'user@example.com',
  sub: 'user-id-123',
  realm_access: { roles: [] },
  resource_access: {}
};

const keycloak = {
  tokenParsed: tokenData,
  idTokenParsed: tokenData, // Add idTokenParsed for compatibility
  subject: 'user-id-123',
  logout: (options?: any) => {
    // Simple logout - redirect to home
    window.location.href = '/';
  },
  init: () => Promise.resolve(true),
  login: () => Promise.resolve(),
  authenticated: true,
  hasRealmRole: (role: string) => false,
  hasResourceRole: (role: string, resource?: string) => false,
  loadUserProfile: () => Promise.resolve({
    id: 'user-id-123',
    username: 'user',
    email: 'user@example.com',
    firstName: 'User',
    lastName: 'Test'
  })
};

export default keycloak;
