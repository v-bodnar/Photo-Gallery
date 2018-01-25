import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CanActivateAuthGuard} from "./services/can-activate-auth-guard";
import {LoginComponent} from "./components/login/login.component";
import {HomeComponent} from "./components/home/home.component";

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full', canActivate: [CanActivateAuthGuard] },
  { path: 'home', component: HomeComponent, canActivate: [CanActivateAuthGuard]},
  { path: 'login', component: LoginComponent}
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
