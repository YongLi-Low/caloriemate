import { AuthConfig } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {
  issuer: 'https://accounts.google.com',
  redirectUri: window.location.origin + '/callback',
  clientId: '230416168962-p5emq7hqcq9s3m83ouo43sko2hkapf7l.apps.googleusercontent.com',
  responseType: 'code',
  scope: 'openid profile email https://www.googleapis.com/auth/calendar',
  showDebugInformation: true,
};
