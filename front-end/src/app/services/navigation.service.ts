import { Injectable } from '@angular/core';
import {Router} from '@angular/router';
import { Location } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class NavigationService {
  constructor(private router: Router, private location: Location) {}

  /**
   * Generic method to navigate to any route with optional query parameters.
   * If no query parameters are provided, it navigates without them.
   * @param route The route to navigate to.
   * @param options Optional object containing query parameters to pass with the navigation.
   */
  navigateTo(route: string, options?: { queryParams?: object }): void {
    if (options?.queryParams) {
      this.router.navigate([`/${route}`], { queryParams: options.queryParams });
    } else {
      this.router.navigate([`/${route}`]);
    }
  }

  /**
   * Navigate back to the previous page in the browser history.
   */
  goBack(): void {
    this.location.back();
  }
}
