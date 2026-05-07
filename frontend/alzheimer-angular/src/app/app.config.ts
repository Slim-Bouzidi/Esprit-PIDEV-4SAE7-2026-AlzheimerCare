import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection, APP_INITIALIZER } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { MessageService, ConfirmationService } from 'primeng/api';
import { MissingTranslationHandler, TranslateModule } from '@ngx-translate/core';
import { authInterceptor } from './auth.interceptor';
import { AppMissingTranslationHandler } from './core/i18n/missing-translation.handler';
import { routes } from './app.routes';
import { AuthService } from './services/auth.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideAnimationsAsync(),
    provideHttpClient(withInterceptors([authInterceptor])),
    importProvidersFrom(
      TranslateModule.forRoot({
        fallbackLang: 'en',
        missingTranslationHandler: {
          provide: MissingTranslationHandler,
          useClass: AppMissingTranslationHandler
        }
      })
    ),
    MessageService,
    ConfirmationService,
    {
      provide: APP_INITIALIZER,
      useFactory: (auth: AuthService) => () => auth.initKeycloak(),
      deps: [AuthService],
      multi: true
    }
  ],
};
