import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const isApiUrl = req.url.startsWith('http://localhost:8080');
  if (isApiUrl) {
    const clonedRequest = req.clone({
      withCredentials: true,
    });
    return next(clonedRequest);
  }

  return next(req);
};
